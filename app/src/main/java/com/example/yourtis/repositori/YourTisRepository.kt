package com.example.yourtis.repositori

import com.example.yourtis.modeldata.LoginResponse
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.modeldata.Transaksi
import com.example.yourtis.modeldata.User
import com.example.yourtis.service.YourTisApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody


// Interface mendefinisikan kontrak data sesuai SRS [cite: 75]
interface YourTisRepository {
    suspend fun login(email: String, kataSandi: String): LoginResponse
    suspend fun register(user: User, kataSandi: String)
    suspend fun getSayur(): List<Sayur>
    suspend fun getSayurById(id: Int): Sayur
    suspend fun insertSayur(idPetani: RequestBody, nama: RequestBody, harga: RequestBody, stok: RequestBody, desc: RequestBody, img: MultipartBody.Part)
    suspend fun updateSayur(id: Int, nama: RequestBody, harga: RequestBody, stok: RequestBody, desc: RequestBody, img: MultipartBody.Part?)
    suspend fun deleteSayur(id: Int)
    suspend fun checkout(data: Map<String, Any>)
    suspend fun getAllTransaksi(): List<Transaksi>
    // Penamaan disesuaikan agar sinkron dengan PembeliViewModel
    suspend fun getTransaksiByPembeli(idPembeli: Int): List<Transaksi>
    suspend fun updateStatusTransaksi(idTransaksi: String, status: String)
}

class NetworkYourTisRepository(
    private val yourTisApiService: YourTisApiService
) : YourTisRepository {

    override suspend fun login(email: String, kataSandi: String): LoginResponse =
        yourTisApiService.login(mapOf("email" to email, "password" to kataSandi))

    override suspend fun register(user: User, kataSandi: String) {
        yourTisApiService.register(mapOf(
            "username" to user.username,
            "email" to user.email,
            "password" to kataSandi,
            "role" to user.role,
            "no_hp" to user.no_hp,
            "alamat" to user.alamat
        ))
    }

    override suspend fun getSayur(): List<Sayur> = yourTisApiService.getAllSayur()

    override suspend fun getSayurById(id: Int): Sayur {
        // Mengambil detail produk spesifik dari server
        return try {
            yourTisApiService.getSayurById(id)
        } catch (e: Exception) {
            // Fallback: cari dari list jika endpoint spesifik bermasalah
            yourTisApiService.getAllSayur().find { it.id_sayur == id } ?: throw Exception("Produk tidak ditemukan")
        }
    }

    override suspend fun insertSayur(idPetani: RequestBody, nama: RequestBody, harga: RequestBody, stok: RequestBody, desc: RequestBody, img: MultipartBody.Part) {
        yourTisApiService.insertSayur(idPetani, nama, harga, stok, desc, img)
    }

    override suspend fun updateSayur(id: Int, nama: RequestBody, harga: RequestBody, stok: RequestBody, desc: RequestBody, img: MultipartBody.Part?) {
        yourTisApiService.updateSayur(id, nama, harga, stok, desc, img)
    }

    override suspend fun deleteSayur(id: Int) = yourTisApiService.deleteSayur(id).let { Unit }

    // Implementasi Checkout dengan validasi alamat_pengiriman
    override suspend fun checkout(data: Map<String, Any>) {
        try {
            val response = yourTisApiService.checkout(data)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                throw Exception("Gagal membuat pesanan: $errorBody")
            }
        } catch (e: Exception) {
            throw Exception("Terjadi gangguan koneksi: ${e.message}")
        }
    }

    override suspend fun getAllTransaksi(): List<Transaksi> = yourTisApiService.getAllTransaksi()

    // Fungsi vital untuk Halaman Laporan Pesanan Pembeli
    override suspend fun getTransaksiByPembeli(idPembeli: Int): List<Transaksi> {
        return try {
            // Jika API Service mendukung filter ID, panggil langsung
            yourTisApiService.getTransaksiByPembeli(idPembeli)
        } catch (e: Exception) {
            // Jika belum ada endpoint khusus, filter secara manual dari semua transaksi
            yourTisApiService.getAllTransaksi().filter { it.id_pembeli == idPembeli }
        }
    }

    override suspend fun updateStatusTransaksi(idTransaksi: String, status: String) {
        yourTisApiService.updateStatusTransaksi(idTransaksi, mapOf("status" to status))
    }
}