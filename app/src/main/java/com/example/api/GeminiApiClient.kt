package com.example.api

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiEndpoint {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    const val DEFAULT_MODEL = "gemini-3.5-flash"
    const val MODEL_PRO = "gemini-3.1-pro-preview"
    const val MODEL_LITE = "gemini-3.1-flash-lite-preview"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val api: GeminiApiEndpoint by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiEndpoint::class.java)
    }

    private const val SYSTEM_PROMPT =
        "তোমার নাম মায়া (Maya)। তুমি ব্যবহারকারীর অত্যন্ত মিষ্টি, কেয়ারিং, বিনয়ী এবং বুদ্ধিমান ব্যক্তিগত এআই সহকারী। " +
        "তুমি সবসময় প্রাকটিক্যাল, সংক্ষিপ্ত ও মিষ্টি সুরে বাংলায় উত্তর দিবে (যদি ব্যবহারকারী ইংরেজিতে প্রশ্ন করে তবে ইংরেজিতেও সুন্দর উত্তর দিতে পারো)। " +
        "তুমি পরিবারের একজন আপনজনের মতো সহানুভূতিশীল।"

    suspend fun testRunApiKey(
        testKey: String,
        model: String = DEFAULT_MODEL
    ): Result<String> = withContext(Dispatchers.IO) {
        val cleanKey = testKey.trim()
        if (cleanKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("এপিআই কি খালি রাখা যাবে না। অনুগ্রহ করে সঠিক কি দিন।"))
        }

        try {
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(Part(text = "হ্যালো! তোমার কি সফলভাবে সংযোগ হয়েছে? ১ বাক্যে মিষ্টি করে বাংলায় বলো।"))
                    )
                ),
                systemInstruction = Content(
                    parts = listOf(Part(text = SYSTEM_PROMPT))
                ),
                generationConfig = GenerationConfig(
                    temperature = 0.7f,
                    topK = 40,
                    topP = 0.95f
                )
            )

            val response = api.generateContent(
                model = model,
                apiKey = cleanKey,
                request = request
            )
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                Result.success(text.trim())
            } else {
                Result.failure(Exception("সার্ভার থেকে খালি উত্তর এসেছে। কোটা বা অনুমতি পরীক্ষা করুন।"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Test run error: ${e.message}", e)
            val friendlyMsg = when {
                e.message?.contains("API_KEY_INVALID", ignoreCase = true) == true ->
                    "এপিআই কি সঠিক নয় (API_KEY_INVALID)। দয়া করে কি-টি পুনরায় চেক করুন।"
                e.message?.contains("403", ignoreCase = true) == true ->
                    "অনুমতি সমস্যা (403 Forbidden)। API সক্রিয় আছে কিনা বা বিলিং স্ট্যাটাস চেক করুন।"
                e.message?.contains("RESOURCE_EXHAUSTED", ignoreCase = true) == true ->
                    "কোটা শেষ (Resource Exhausted)। কিছুক্ষণ পরে চেষ্টা করুন।"
                else -> "সংযোগ ব্যর্থ: ${e.localizedMessage ?: e.message}"
            }
            Result.failure(Exception(friendlyMsg))
        }
    }

    suspend fun askMaya(
        userPrompt: String,
        conversationHistory: List<Pair<String, String>> = emptyList(),
        customApiKey: String? = null,
        model: String = DEFAULT_MODEL
    ): Result<String> = withContext(Dispatchers.IO) {
        val key = if (!customApiKey.isNullOrBlank()) {
            customApiKey
        } else {
            BuildConfig.GEMINI_API_KEY
        }

        if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "No valid Gemini API key configured.")
            return@withContext Result.failure(IllegalStateException("API_KEY_MISSING"))
        }

        try {
            val contentsList = mutableListOf<Content>()
            // Include up to 4 recent conversation turns
            val recentTurns = conversationHistory.takeLast(4)
            for ((role, text) in recentTurns) {
                contentsList.add(
                    Content(
                        role = if (role == "USER") "user" else "model",
                        parts = listOf(Part(text = text))
                    )
                )
            }
            contentsList.add(
                Content(
                    role = "user",
                    parts = listOf(Part(text = userPrompt))
                )
            )

            val request = GeminiRequest(
                contents = contentsList,
                systemInstruction = Content(
                    parts = listOf(Part(text = SYSTEM_PROMPT))
                ),
                generationConfig = GenerationConfig(
                    temperature = 0.7f,
                    topK = 40,
                    topP = 0.95f
                )
            )

            val response = api.generateContent(
                model = model,
                apiKey = key,
                request = request
            )
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                Result.success(text.trim())
            } else {
                Result.failure(Exception("খালি উত্তর পাওয়া গেছে"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini call error: ${e.message}", e)
            Result.failure(e)
        }
    }
}
