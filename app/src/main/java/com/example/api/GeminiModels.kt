package com.example.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @param:Json(name = "contents") val contents: List<Content>,
    @param:Json(name = "systemInstruction") val systemInstruction: Content? = null,
    @param:Json(name = "generationConfig") val generationConfig: GenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @param:Json(name = "role") val role: String? = "user",
    @param:Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @param:Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @param:Json(name = "temperature") val temperature: Float? = 0.7f,
    @param:Json(name = "topK") val topK: Int? = 40,
    @param:Json(name = "topP") val topP: Float? = 0.95f
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @param:Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @param:Json(name = "content") val content: Content? = null
)
