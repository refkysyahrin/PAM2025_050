package com.example.yourtis.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class DetailTransaksi(
    val id_detail: Int,
    val id_transaksi: Int,
    val id_sayur: Int,
    val qty: Int,
    val subtotal: Int,
    val nama_sayur: String? = null // Diambil dari join table di backend
)
