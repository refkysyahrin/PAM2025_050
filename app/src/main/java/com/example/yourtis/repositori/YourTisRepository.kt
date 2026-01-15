package com.example.yourtis.repositori

import com.example.yourtis.modeldata.LoginResponse
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.modeldata.Transaksi
import com.example.yourtis.modeldata.User
import com.example.yourtis.service.YourTisApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody


interface YourTisRepository {
    // --- AUTH ---
    suspend fun login(email: String, kataSandi: String): LoginResponse
    suspend fun register(user: User, kataSandi: String)

    // --- PRODUK (SAYUR) ---
    suspend fun getSayur(): List<Sayur>
    suspend fun getSayurById(id: Int): Sayur
    suspend fun insertSayur(idPetani: RequestBody, nama: RequestBody, harga: RequestBody, stok: RequestBody, desc: RequestBody, img: MultipartBody.Part)
    suspend fun updateSayur(id: Int, nama: RequestBody, harga: RequestBody, stok: RequestBody, desc: RequestBody, img: MultipartBody.Part?)
    suspend fun deleteSayur(id: Int)

    // --- TRANSAKSI ---
    suspend fun checkout(data: Map<String, Any>)
    suspend fun getAllTransaksi(): List<Transaksi>
    suspend fun updateStatusTransaksi(idTransaksi: String, status: String)
}

class NetworkYourTisRepository(
    private val yourTisApiService: YourTisApiService
) : YourTisRepository {

    override suspend fun login(email: String, kataSandi: String): LoginResponse =
        yourTisApiService.login(mapOf("email" to email, "password" to kataSandi))

    override suspend fun register(user: User, kataSandi: String) {
        val registerData = mapOf(
            "username" to user.username,
            "email" to user.email,
            "password" to kataSandi,
            "role" to user.role,
            "no_hp" to user.no_hp,
            "alamat" to user.alamat
        )
        yourTisApiService.register(registerData)
    }

    override suspend fun getSayur(): List<Sayur> = yourTisApiService.getAllSayur()

    override suspend fun getSayurById(id: Int): Sayur {
        val allSayur = yourTisApiService.getAllSayur()
        return allSayur.find { it.id_sayur == id } ?: throw Exception("Sayur tidak ditemukan")
    }

    override suspend fun insertSayur(idPetani: RequestBody, nama: RequestBody, harga: RequestBody, stok: RequestBody, desc: RequestBody, img: MultipartBody.Part) {
        yourTisApiService.insertSayur(idPetani, nama, harga, stok, desc, img)
    }

    override suspend fun updateSayur(id: Int, nama: RequestBody, harga: RequestBody, stok: RequestBody, desc: RequestBody, img: MultipartBody.Part?) {
        yourTisApiService.updateSayur(id, nama, harga, stok, desc, img)
    }

    override suspend fun deleteSayur(id: Int) {
        yourTisApiService.deleteSayur(id)
    }

    override suspend fun checkout(data: Map<String, Any>) {
        yourTisApiService.checkout(data)
    }

    override suspend fun getAllTransaksi(): List<Transaksi> {
        return yourTisApiService.getAllTransaksi()
    }

    // PERBAIKAN: Fungsi sekarang memiliki isi body
    override suspend fun updateStatusTransaksi(idTransaksi: String, status: String) {
        yourTisApiService.updateStatusTransaksi(idTransaksi, mapOf("status" to status))
    }
}