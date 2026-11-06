package com.academicflow.app.data.remote

import com.academicflow.app.data.remote.dto.QuoteDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface QuoteApiService {

    @GET("api/random")
    suspend fun getRandomQuote(): List<QuoteDto>

    companion object {
        private const val BASE_URL = "https://zenquotes.io/"

        fun create(): QuoteApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QuoteApiService::class.java)
        }
    }
}