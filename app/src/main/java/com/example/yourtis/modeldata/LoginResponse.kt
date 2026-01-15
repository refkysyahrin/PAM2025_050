package com.example.yourtis.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val message: String,
    val data: User // Objek User di atas
)