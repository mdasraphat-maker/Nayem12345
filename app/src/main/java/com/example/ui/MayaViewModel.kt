package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MayaAccessibilityService
import com.example.actions.ActionResult
import com.example.actions.SystemActionDispatcher
import com.example.api.GeminiApiClient
import com.example.data.ConversationMessage
import com.example.data.MayaDatabase
import com.example.engine.MayaProactiveEngine
import com.example.voice.MayaVoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MayaScreen {
    ASSISTANT,
    ACTIONS,
    HISTORY,
    SETTINGS
}

sealed interface ApiKeyTestStatus {
    object Idle : ApiKeyTestStatus
    object Testing : ApiKeyTestStatus
    data class Success(val reply: String, val model: String) : ApiKeyTestStatus
    data class Error(val errorMessage: String) : ApiKeyTestStatus
}

class MayaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MayaDatabase.getDatabase(application)
    private val dao = db.conversationDao()
    private val prefs = application.getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)

    val conversationHistory: StateFlow<List<ConversationMessage>> = dao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val voiceManager = MayaVoiceManager(application)

    private val _currentScreen = MutableStateFlow(MayaScreen.ASSISTANT)
    val currentScreen: StateFlow<MayaScreen> = _currentScreen.asStateFlow()

    private val _userSpeech = MutableStateFlow("বলুন, আমি শুনছি...")
    val userSpeech: StateFlow<String> = _userSpeech.asStateFlow()

    private val _mayaResponse = MutableStateFlow("হ্যালো! আমি মায়া। আপনার কীভাবে সাহায্য করতে পারি?")
    val mayaResponse: StateFlow<String> = _mayaResponse.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _customApiKey = MutableStateFlow(prefs.getString("custom_gemini_key", "") ?: "")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _selectedModel = MutableStateFlow(
        prefs.getString("selected_gemini_model", GeminiApiClient.DEFAULT_MODEL) ?: GeminiApiClient.DEFAULT_MODEL
    )
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _apiKeyTestStatus = MutableStateFlow<ApiKeyTestStatus>(ApiKeyTestStatus.Idle)
    val apiKeyTestStatus: StateFlow<ApiKeyTestStatus> = _apiKeyTestStatus.asStateFlow()

    private val _showApiKeyDialog = MutableStateFlow(false)
    val showApiKeyDialog: StateFlow<Boolean> = _showApiKeyDialog.asStateFlow()

    private val _ttsPitch = MutableStateFlow(prefs.getFloat("tts_pitch", 1.3f))
    val ttsPitch: StateFlow<Float> = _ttsPitch.asStateFlow()

    private val _ttsRate = MutableStateFlow(prefs.getFloat("tts_rate", 0.95f))
    val ttsRate: StateFlow<Float> = _ttsRate.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(prefs.getString("lang", "bn-BD") ?: "bn-BD")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _statusNotice = MutableStateFlow<String?>(null)
    val statusNotice: StateFlow<String?> = _statusNotice.asStateFlow()

    private val proactiveEngine = MayaProactiveEngine(
        scope = viewModelScope,
        onCheckInTriggered = { phrase ->
            if (!voiceManager.isListening.value && !voiceManager.isSpeaking.value && !_isThinking.value) {
                _mayaResponse.value = phrase
                voiceManager.speak(phrase)
                recordMessage("MAYA", phrase, isProactive = true)
            }
        }
    )

    val isProactiveEnabled: StateFlow<Boolean> = proactiveEngine.isEnabled
    val proactiveInterval: StateFlow<Int> = proactiveEngine.intervalSeconds

    init {
        voiceManager.pitch = _ttsPitch.value
        voiceManager.speechRate = _ttsRate.value
        voiceManager.applyLanguage(_selectedLanguage.value)

        // Greet on startup
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200)
            voiceManager.speak(_mayaResponse.value)
            proactiveEngine.start {
                !voiceManager.isListening.value && !voiceManager.isSpeaking.value && !_isThinking.value
            }
        }
    }

    fun setScreen(screen: MayaScreen) {
        _currentScreen.value = screen
    }

    fun clearStatusNotice() {
        _statusNotice.value = null
    }

    fun startListening() {
        if (voiceManager.isListening.value) {
            voiceManager.stopListening()
            return
        }
        voiceManager.stopSpeaking()
        _userSpeech.value = "শুনছি..."
        voiceManager.startListening(
            onResult = { speech ->
                _userSpeech.value = speech
                processCommand(speech)
            },
            onError = { err ->
                _statusNotice.value = err
            }
        )
    }

    fun stopListening() {
        voiceManager.stopListening()
    }

    fun speakResponse(text: String? = null) {
        val target = text ?: _mayaResponse.value
        voiceManager.speak(target)
    }

    fun stopSpeaking() {
        voiceManager.stopSpeaking()
    }

    fun processCommand(command: String) {
        if (command.isBlank()) return
        stopListening()
        voiceManager.stopSpeaking()

        val context = getApplication<Application>()
        recordMessage("USER", command)

        // 1. Check for system actions
        val actionResult = SystemActionDispatcher.tryParseAndExecute(context, command)
        when (actionResult) {
            is ActionResult.Success -> {
                _mayaResponse.value = actionResult.messageBn
                voiceManager.speak(actionResult.messageBn)
                recordMessage("MAYA", actionResult.messageBn, actionExecuted = actionResult.actionTag)
                return
            }
            is ActionResult.Failure -> {
                _mayaResponse.value = actionResult.reasonBn
                voiceManager.speak(actionResult.reasonBn)
                recordMessage("MAYA", actionResult.reasonBn)
                return
            }
            ActionResult.NotAnAction -> {
                // Proceed to Gemini AI conversational response
            }
        }

        // 2. Query Gemini AI
        _isThinking.value = true
        _mayaResponse.value = "চিন্তা করছি..."

        viewModelScope.launch {
            val historyPairs = conversationHistory.value.take(6).map { it.sender to it.text }
            val result = GeminiApiClient.askMaya(
                userPrompt = command,
                conversationHistory = historyPairs,
                customApiKey = _customApiKey.value.ifBlank { null },
                model = _selectedModel.value
            )

            _isThinking.value = false
            result.onSuccess { reply ->
                _mayaResponse.value = reply
                voiceManager.speak(reply)
                recordMessage("MAYA", reply)
            }.onFailure { ex ->
                val fallbackReply = when {
                    ex.message?.contains("API_KEY_MISSING") == true ->
                        "আমি আপনার কথা শুনতে পেয়েছি: \"$command\"। গভীর এআই উত্তরের জন্য সেটিংস থেকে জেমিনি এপিআই কি দিন ও রান করুন।"
                    else ->
                        "আমি আপনার প্রশ্নটি বুঝেছি: \"$command\"। তবে নেটওয়ার্ক সমস্যার কারণে ব্যাকএন্ডে কিছুটা বিলম্ব হচ্ছে।"
                }
                _mayaResponse.value = fallbackReply
                voiceManager.speak(fallbackReply)
                recordMessage("MAYA", fallbackReply)
            }
        }
    }

    fun setShowApiKeyDialog(show: Boolean) {
        _showApiKeyDialog.value = show
    }

    fun setSelectedModel(model: String) {
        _selectedModel.value = model
        prefs.edit().putString("selected_gemini_model", model).apply()
        _statusNotice.value = "মডেল পরিবর্তন: $model"
    }

    fun resetApiKeyStatus() {
        _apiKeyTestStatus.value = ApiKeyTestStatus.Idle
    }

    fun testAndRunApiKey(candidateKey: String) {
        val key = candidateKey.trim()
        if (key.isBlank()) {
            _apiKeyTestStatus.value = ApiKeyTestStatus.Error("দয়া করে একটি সঠিক এপিআই কি প্রবেশ করান।")
            return
        }

        _apiKeyTestStatus.value = ApiKeyTestStatus.Testing
        viewModelScope.launch {
            val result = GeminiApiClient.testRunApiKey(key, _selectedModel.value)
            result.onSuccess { reply ->
                // Automatically save verified key
                saveCustomApiKey(key)
                _apiKeyTestStatus.value = ApiKeyTestStatus.Success(reply, _selectedModel.value)
                _statusNotice.value = "✅ এপিআই কি সফলভাবে রান হয়েছে!"
                voiceManager.speak("সংযোগ সফল হয়েছে! আপনার জেমিনি এপিআই কি সক্রিয় আছে।")
            }.onFailure { ex ->
                val errMsg = ex.localizedMessage ?: "অজ্ঞাত ত্রুটি দেখা দিয়েছে।"
                _apiKeyTestStatus.value = ApiKeyTestStatus.Error(errMsg)
                _statusNotice.value = "❌ এপিআই কি রান ব্যর্থ হয়েছে"
            }
        }
    }

    fun clearCustomApiKey() {
        _customApiKey.value = ""
        prefs.edit().remove("custom_gemini_key").apply()
        _apiKeyTestStatus.value = ApiKeyTestStatus.Idle
        _statusNotice.value = "এপিআই কি মুছে ফেলা হয়েছে"
    }

    fun toggleProactive(enabled: Boolean) {
        proactiveEngine.setEnabled(enabled) {
            !voiceManager.isListening.value && !voiceManager.isSpeaking.value && !_isThinking.value
        }
    }

    fun setProactiveInterval(seconds: Int) {
        proactiveEngine.setInterval(seconds) {
            !voiceManager.isListening.value && !voiceManager.isSpeaking.value && !_isThinking.value
        }
    }

    fun updatePitch(newPitch: Float) {
        _ttsPitch.value = newPitch
        voiceManager.pitch = newPitch
        prefs.edit().putFloat("tts_pitch", newPitch).apply()
    }

    fun updateRate(newRate: Float) {
        _ttsRate.value = newRate
        voiceManager.speechRate = newRate
        prefs.edit().putFloat("tts_rate", newRate).apply()
    }

    fun updateLanguage(lang: String) {
        _selectedLanguage.value = lang
        voiceManager.applyLanguage(lang)
        prefs.edit().putString("lang", lang).apply()
    }

    fun saveCustomApiKey(key: String) {
        _customApiKey.value = key.trim()
        prefs.edit().putString("custom_gemini_key", key.trim()).apply()
        _statusNotice.value = "এপিআই কি সংরক্ষিত হয়েছে"
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearAll()
            _statusNotice.value = "কথোপকথন হিস্টোরি মুছে ফেলা হয়েছে"
        }
    }

    fun isAccessibilityActive(): Boolean = MayaAccessibilityService.isRunning()

    fun triggerAutoScrollDown() {
        val service = MayaAccessibilityService.instance
        if (service != null) {
            service.performAutoScrollDown()
            _statusNotice.value = "স্ক্রোল ডাউন জেসচার সম্পন্ন হয়েছে"
        } else {
            _statusNotice.value = "অ্যাক্সেসিবিলিটি সার্ভিস সক্রিয় নয়"
        }
    }

    fun triggerAutoScrollUp() {
        val service = MayaAccessibilityService.instance
        if (service != null) {
            service.performAutoScrollUp()
            _statusNotice.value = "স্ক্রোল আপ জেসচার সম্পন্ন হয়েছে"
        } else {
            _statusNotice.value = "অ্যাক্সেসিবিলিটি সার্ভিস সক্রিয় নয়"
        }
    }

    private fun recordMessage(
        sender: String,
        text: String,
        actionExecuted: String? = null,
        isProactive: Boolean = false
    ) {
        viewModelScope.launch {
            dao.insertMessage(
                ConversationMessage(
                    sender = sender,
                    text = text,
                    actionExecuted = actionExecuted,
                    isProactive = isProactive
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        proactiveEngine.stop()
        voiceManager.destroy()
    }
}
