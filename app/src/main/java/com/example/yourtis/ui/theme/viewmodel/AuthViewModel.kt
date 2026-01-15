package com.example.yourtis.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourtis.modeldata.User
import com.example.yourtis.repositori.YourTisRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// State UI untuk Login/Register
sealed interface LoginUiState {
    data class Success(val user: User) : LoginUiState
    object Error : LoginUiState
    object Loading : LoginUiState
    object Idle : LoginUiState
}

class AuthViewModel(private val repository: YourTisRepository) : ViewModel() {

    var uiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    // State untuk Form Input
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var noHp by mutableStateOf("")
    var alamat by mutableStateOf("")
    var role by mutableStateOf("Pembeli") // Default role, bisa diubah via RadioButton

    // Fungsi Login
    fun login() {
        viewModelScope.launch {
            uiState = LoginUiState.Loading
            try {
                val response = repository.login(email, password)

                if (response.data != null) {
                    // Login Berhasil
                    uiState = LoginUiState.Success(response.data)
                } else {
                    // Login Gagal (Response OK tapi data null)
                    uiState = LoginUiState.Error
                }
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = LoginUiState.Error
            }
        }
    }

    // Fungsi Register
    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = LoginUiState.Loading
            try {
                val newUser = User(
                    id_user = 0, // ID akan di-generate oleh database
                    username = username,
                    email = email,
                    role = role,
                    no_hp = noHp,
                    alamat = alamat
                )

                // Kirim data ke repository
                repository.register(newUser, password)

                // Jika tidak ada error (Exception), berarti sukses
                uiState = LoginUiState.Success(newUser)
                onSuccess() // Callback navigasi balik ke login
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = LoginUiState.Error
            }
        }
    }

    // Reset Form (Dipanggil saat pindah halaman atau logout)
    fun resetState() {
        uiState = LoginUiState.Idle
        username = ""
        email = ""
        password = ""
        noHp = ""
        alamat = ""
        role = "Pembeli"
    }
}