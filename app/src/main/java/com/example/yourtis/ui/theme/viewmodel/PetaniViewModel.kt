package com.example.yourtis.ui.theme.viewmodel

import android.util.Log
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

// State untuk Dashboard Petani (Pendapatan & List Transaksi) [cite: 90, 171]
sealed interface DashboardUiState {
    data class Success(
        val totalPendapatan: Int,
        val jumlahPesanan: Int,
        val listTransaksi: List<Transaksi>
    ) : DashboardUiState
    object Error : DashboardUiState
    object Loading : DashboardUiState
}

// State untuk Produk (CRUD Sayur oleh Petani) [cite: 89, 133]
sealed interface ProdukUiState {
    data class Success(val sayur: List<Sayur>) : ProdukUiState
    object Error : ProdukUiState
    object Loading : ProdukUiState
}

class PetaniViewModel(private val repository: YourTisRepository) : ViewModel() {

    // Status UI untuk Dashboard Laporan [cite: 177]
    var dashboardUiState: DashboardUiState by mutableStateOf(DashboardUiState.Loading)
        private set

    // Status UI untuk Pengelolaan Produk [cite: 138]
    var produkUiState: ProdukUiState by mutableStateOf(ProdukUiState.Loading)
        private set

    init {
        loadDashboard()
        loadProduk()
    }

    /**
     * 1. Load Data Dashboard
     * Mengambil dan menghitung data pesanan secara real-time [cite: 173, 174, 177]
     */
    fun loadDashboard() {
        viewModelScope.launch {
            dashboardUiState = DashboardUiState.Loading
            try {
                // Mengambil seluruh data transaksi [cite: 179]
                val allTransactions = repository.getAllTransaksi()

                // REQ-MON-01: Menghitung total nominal pendapatan hanya dari pesanan yang 'Selesai'
                val totalSelesai = allTransactions
                    .filter { it.status == "Selesai" }
                    .sumOf { it.total_bayar }

                // Menghitung jumlah total pesanan yang masuk [cite: 174]
                val count = allTransactions.size

                // REQ-MON-02: Menyediakan daftar pesanan lengkap untuk ditampilkan di laporan [cite: 180]
                dashboardUiState = DashboardUiState.Success(
                    totalPendapatan = totalSelesai,
                    jumlahPesanan = count,
                    listTransaksi = allTransactions
                )
            } catch (e: Exception) {
                Log.e("PetaniVM", "Error load dashboard: ${e.message}")
                dashboardUiState = DashboardUiState.Error
            }
        }
    }

    /**
     * 2. Load Data Produk
     * Menampilkan katalog sayur yang dikelola petani [cite: 89, 312]
     */
    fun loadProduk() {
        viewModelScope.launch {
            produkUiState = ProdukUiState.Loading
            try {
                val listSayur = repository.getSayur()
                produkUiState = ProdukUiState.Success(listSayur)
            } catch (e: Exception) {
                Log.e("PetaniVM", "Error load produk: ${e.message}")
                produkUiState = ProdukUiState.Error
            }
        }
    }

    /**
     * 3. Hapus Produk (REQ-PROD-03)
     * Menghapus sayur yang sudah tidak dijual lagi [cite: 141, 145]
     */
    fun deleteSayur(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteSayur(id)
                loadProduk() // Refresh list setelah hapus agar data tetap akurat
            } catch (e: Exception) {
                Log.e("PetaniVM", "Error delete produk: ${e.message}")
            }
        }
    }

    /**
     * 4. Update Status Transaksi
     * Mengelola progres pesanan pembeli (CRUD Status) [cite: 340]
     */
    fun updateStatusTransaksi(idTransaksi: String, newStatus: String) {
        viewModelScope.launch {
            try {
                // Mengirim pembaruan status ke backend [cite: 203]
                repository.updateStatusTransaksi(idTransaksi, newStatus)

                // Refresh data dashboard agar total pendapatan terupdate otomatis [cite: 177]
                loadDashboard()
            } catch (e: Exception) {
                Log.e("PetaniVM", "Error update status: ${e.message}")
            }
        }
    }
}