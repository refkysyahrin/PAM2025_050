package com.example.yourtis.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val message: String,
    val user: User // Objek User di atas
)