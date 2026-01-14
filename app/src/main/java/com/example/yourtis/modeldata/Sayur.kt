package com.example.yourtis.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class Sayur(
    val id_sayur: Int,
    val id_petani: Int,
    val nama_sayur: String,
    val harga: Int,
    val stok: Int,
    val deskripsi: String,
    val gambar: String? = null, // Nama file asli di database
    val gambar_url: String? = null // URL lengkap dari server (http://10.0.2.2...)
)