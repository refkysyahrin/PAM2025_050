package com.example.yourtis.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class Transaksi(
    val id_transaksi: Int,
    val id_pembeli: Int,
    val total_bayar: Int,
    val metode_kirim: String,
    val metode_bayar: String,
    val status: String,
    val tgl_transaksi: String,
    val alamat_pengiriman: String,
    // Tambahkan list items agar jika backend mengirimkan data nested langsung terbaca
    val items: List<DetailTransaksi>? = null
)
