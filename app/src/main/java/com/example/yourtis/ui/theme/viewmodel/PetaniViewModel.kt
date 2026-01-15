package com.example.yourtis.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.modeldata.Transaksi
import com.example.yourtis.repositori.YourTisRepository
import kotlinx.coroutines.launch
import java.io.IOException

// State untuk Dashboard (Pendapatan & List Transaksi)
sealed interface DashboardUiState {
    data class Success(
        val totalPendapatan: Int,
        val jumlahPesanan: Int,
        val listTransaksi: List<Transaksi>
    ) : DashboardUiState
    object Error : DashboardUiState
    object Loading : DashboardUiState
}

// State untuk Produk (CRUD Sayur)
sealed interface ProdukUiState {
    data class Success(val sayur: List<Sayur>) : ProdukUiState
    object Error : ProdukUiState
    object Loading : ProdukUiState
}

class PetaniViewModel(private val repository: YourTisRepository) : ViewModel() {

    var dashboardUiState: DashboardUiState by mutableStateOf(DashboardUiState.Loading)
        private set

    var produkUiState: ProdukUiState by mutableStateOf(ProdukUiState.Loading)
        private set

    init {
        loadDashboard()
        loadProduk()
    }

    // 1. Load Data Dashboard
    fun loadDashboard() {
        viewModelScope.launch {
            dashboardUiState = DashboardUiState.Loading
            try {
                val list = repository.getAllTransaksi()

                // Hitung Total Pendapatan
                val total = list.sumOf { it.total_bayar }
                val count = list.size

                // Ambil 5 transaksi terbaru
                val recent = list.takeLast(5).reversed()

                dashboardUiState = DashboardUiState.Success(total, count, recent)
            } catch (e: Exception) {
                dashboardUiState = DashboardUiState.Error
            }
        }
    }

    // 2. Load Data Produk
    fun loadProduk() {
        viewModelScope.launch {
            produkUiState = ProdukUiState.Loading
            try {
                val listSayur = repository.getSayur()
                produkUiState = ProdukUiState.Success(listSayur)
            } catch (e: Exception) {
                produkUiState = ProdukUiState.Error
            }
        }
    }

    // 3. Hapus Produk
    fun deleteSayur(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteSayur(id)
                loadProduk() // Refresh list setelah hapus
            } catch (e: Exception) {
                // Handle Error
            }
        }
    }

    // 4. Update Status Transaksi (Pending -> Proses -> Selesai)
    fun updateStatusTransaksi(idTransaksi: String, newStatus: String) {
        viewModelScope.launch {
            try {
                repository.updateStatusTransaksi(idTransaksi, newStatus)
                // Refresh data dashboard agar status di UI berubah
                loadDashboard()
            } catch (e: Exception) {
                // Handle Error
            }
        }
    }
}