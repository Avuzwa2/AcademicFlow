package com.academicflow.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for REST API quote response.
 */
data class QuoteDto(
    @SerializedName("q")
    val quote: String? = null,

    @SerializedName("a")
    val author: String? = null
)