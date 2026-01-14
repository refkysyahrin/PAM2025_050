package com.example.yourtis.repositori

import com.example.yourtis.modeldata.LoginResponse
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.modeldata.User
import com.example.yourtis.service.YourTisApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.POST

interface YourTisRepository {
    // Auth
    suspend fun login(email: String, kataSandi: String): LoginResponse
    suspend fun register(user: User, kataSandi: String)

    // Produk
    suspend fun getSayur(): List<Sayur>
    suspend fun insertSayur(
        idPetani: RequestBody,
        nama: RequestBody,
        harga: RequestBody,
        stok: RequestBody,
        desc: RequestBody,
        img: MultipartBody.Part
    )
    suspend fun deleteSayur(id: Int)

    // Transaksi
    suspend fun checkout(data: Map<String, Any>)
}

class NetworkYourTisRepository(
    private val yourTisApiService: YourTisApiService
) : YourTisRepository {

    // IMPLEMENTASI LOGIN
    override suspend fun login(email: String, kataSandi: String): LoginResponse {
        return yourTisApiService.login(mapOf("email" to email, "password" to kataSandi))
    }

    // IMPLEMENTASI REGISTER
    // Membungkus data user dan password menjadi satu Map
    override suspend fun register(user: User, kataSandi: String) {
        val registerData = mapOf(
            "username" to user.username,
            "email" to user.email,
            "password" to kataSandi, // Password dikirim di sini
            "role" to user.role,
            "no_hp" to user.no_hp,
            "alamat" to user.alamat
        )
        yourTisApiService.register(registerData)
    }

    // IMPLEMENTASI GET ALL SAYUR
    override suspend fun getSayur(): List<Sayur> {
        return yourTisApiService.getAllSayur()
    }

    // IMPLEMENTASI INSERT SAYUR
    override suspend fun insertSayur(
        idPetani: RequestBody,
        nama: RequestBody,
        harga: RequestBody,
        stok: RequestBody,
        desc: RequestBody,
        img: MultipartBody.Part
    ) {
        yourTisApiService.insertSayur(idPetani, nama, harga, stok, desc, img)
    }

    // IMPLEMENTASI DELETE SAYUR
    override suspend fun deleteSayur(id: Int) {
        yourTisApiService.deleteSayur(id)
    }

    // IMPLEMENTASI CHECKOUT
    override suspend fun checkout(data: Map<String, Any>) {
        yourTisApiService.checkout(data)
    }
}