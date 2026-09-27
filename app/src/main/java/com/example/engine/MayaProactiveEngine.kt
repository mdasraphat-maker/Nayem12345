package com.example.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MayaProactiveEngine(
    private val scope: CoroutineScope,
    private val onCheckInTriggered: (String) -> Unit
) {
    private var job: Job? = null

    private val _isEnabled = MutableStateFlow(true)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()

    private val _intervalSeconds = MutableStateFlow(15) // default 15s as in user request
    val intervalSeconds: StateFlow<Int> = _intervalSeconds.asStateFlow()

    private val checkInPhrases = listOf(
        "আপনি কি কাজে ব্যস্ত আছেন?",
        "তোমার কি কোনো সাহায্য দরকার?",
        "আজকের কি কোনো পেন্ডিং কাজ মনে করিয়ে দিতে হবে?",
        "আমি আপনার কমান্ডের অপেক্ষায় আছি।",
        "শরীর ও চোখের যত্ন নিতে একটু বিরতি নিন!",
        "আমি মায়া, আপনার সাথে আছি। কোনো অ্যাপ চালু করব?",
        "সবকিছু ঠিক আছে তো? আমাকে যেকোনো প্রশ্ন করতে পারেন।"
    )

    fun start(canTriggerCheck: () -> Boolean) {
        job?.cancel()
        if (!_isEnabled.value) return

        job = scope.launch(Dispatchers.Main) {
            while (isActive) {
                delay(_intervalSeconds.value * 1000L)
                if (_isEnabled.value && canTriggerCheck()) {
                    val prompt = checkInPhrases.random()
                    onCheckInTriggered(prompt)
                }
            }
        }
    }

    fun setEnabled(enabled: Boolean, canTriggerCheck: () -> Boolean) {
        _isEnabled.value = enabled
        if (enabled) {
            start(canTriggerCheck)
        } else {
            job?.cancel()
        }
    }

    fun setInterval(seconds: Int, canTriggerCheck: () -> Boolean) {
        _intervalSeconds.value = seconds.coerceIn(10, 300)
        if (_isEnabled.value) {
            start(canTriggerCheck)
        }
    }

    fun stop() {
        job?.cancel()
    }
}
