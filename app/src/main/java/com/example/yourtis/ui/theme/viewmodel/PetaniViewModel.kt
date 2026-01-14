package com.example.yourtis.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.repositori.YourTisRepository
import kotlinx.coroutines.launch
import java.io.IOException

// Status UI Dashboard
sealed interface HomeUiState {
    data class Success(val sayur: List<Sayur>) : HomeUiState
    object Error : HomeUiState
    object Loading : HomeUiState
}

class PetaniViewModel(private val repository: YourTisRepository) : ViewModel() {

    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        getSayur()
    }

    // Ambil Data Sayur (Refresh)
    fun getSayur() {
        viewModelScope.launch {
            homeUiState = HomeUiState.Loading
            try {
                val listSayur = repository.getSayur()
                homeUiState = HomeUiState.Success(listSayur)
            } catch (e: IOException) {
                homeUiState = HomeUiState.Error
            } catch (e: Exception) {
                homeUiState = HomeUiState.Error
            }
        }
    }

    // Hapus Sayur
    fun deleteSayur(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteSayur(id)
                getSayur() // Refresh data setelah hapus
            } catch (e: Exception) {
                // Handle error jika perlu
            }
        }
    }
}