package io.cheeta.api.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}
private val mapper = jacksonObjectMapper()

open class ApiClient(private val baseUrl: String) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    protected inline fun <reified T> get(path: String, token: String? = null): T {
        val url = "$baseUrl$path"
        val request = buildRequest(url, "GET", token)
        
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw ApiException("GET $url failed: ${response.code} - ${response.body?.string()}")
            }
            mapper.readValue(response.body?.string() ?: "{}", T::class.java)
        }
    }

    protected inline fun <reified T> post(path: String, body: Any?, token: String? = null): T {
        val url = "$baseUrl$path"
        val requestBody = if (body != null) {
            mapper.writeValueAsString(body).toRequestBody("application/json".toMediaType())
        } else {
            "{}".toRequestBody("application/json".toMediaType())
        }
        
        val request = buildRequest(url, "POST", token, requestBody)
        
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw ApiException("POST $url failed: ${response.code} - ${response.body?.string()}")
            }
            mapper.readValue(response.body?.string() ?: "{}", T::class.java)
        }
    }

    protected inline fun <reified T> put(path: String, body: Any?, token: String? = null): T {
        val url = "$baseUrl$path"
        val requestBody = if (body != null) {
            mapper.writeValueAsString(body).toRequestBody("application/json".toMediaType())
        } else {
            "{}".toRequestBody("application/json".toMediaType())
        }
        
        val request = buildRequest(url, "PUT", token, requestBody)
        
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw ApiException("PUT $url failed: ${response.code} - ${response.body?.string()}")
            }
            mapper.readValue(response.body?.string() ?: "{}", T::class.java)
        }
    }

    protected inline fun <reified T> delete(path: String, token: String? = null): T {
        val url = "$baseUrl$path"
        val request = buildRequest(url, "DELETE", token)
        
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw ApiException("DELETE $url failed: ${response.code} - ${response.body?.string()}")
            }
            mapper.readValue(response.body?.string() ?: "{}", T::class.java)
        }
    }

    private fun buildRequest(
        url: String,
        method: String,
        token: String? = null,
        body: okhttp3.RequestBody? = null
    ): Request {
        return Request.Builder()
            .url(url)
            .method(method, body)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .apply {
                if (token != null) {
                    header("Authorization", "Bearer $token")
                }
            }
            .build()
    }
}

class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause)
