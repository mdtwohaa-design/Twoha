package com.example.api

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// --- Moshi Serializable request/response models for Gemini REST API ---

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "temperature") val temperature: Float? = 0.7f,
    @Json(name = "maxOutputTokens") val maxOutputTokens: Int? = 1000
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

object GeminiTutorService {
    
    private const val SYSTEM_PROMPT = """You are an elite clinical anatomy professor and medical licensing exam (USMLE/mrcp) tutor. 
Your goal is to assist medical students in learning detailed human anatomy, physiological mechanisms, and clinical correlations.
Keep responses highly informative, structured with medical sub-bullet points, of extreme clinical depth, and clear. 
Always explain things scientifically. For any organ discussed, touch upon:
1. Histological or structural details.
2. Blood supply, venous drainage, and lymphatic networks if relevant.
3. Pathological breakdowns with high-yield clinical clinical pearls (e.g., specific diagnostics, disease pathophysiology, key signs).
Avoid elementary generalizations; speak directly to a junior physician or medical student level."""

    suspend fun askTutor(organName: String, question: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API Configuration Error: Please set your GEMINI_API_KEY in the AI Studio Secrets panel. The application requires this key to connect with the medical AI tutor."
        }

        val prompt = "Discuss the following organ: $organName.\nStudent's medical question: $question"
        
        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = SYSTEM_PROMPT))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.6f,
                maxOutputTokens = 1200
            )
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "No response could be fetched from the medical AI. Please double-check your network connection or API query boundaries."
        } catch (e: Exception) {
            "Connection Exception occurred: ${e.localizedMessage ?: e.message}\nEnsure your API is correctly provisioned in the AI Studio platform."
        }
    }
}
