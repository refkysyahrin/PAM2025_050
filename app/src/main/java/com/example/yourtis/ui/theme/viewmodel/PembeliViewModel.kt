package com.example.yourtis.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.repositori.YourTisRepository
import kotlinx.coroutines.launch
import java.io.IOException

// Model data sederhana untuk item di keranjang
data class CartItem(
    val sayur: Sayur,
    var qty: Int
)

class PembeliViewModel(private val repository: YourTisRepository) : ViewModel() {

    // State Katalog (Sama seperti Petani)
    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    // State Keranjang Belanja (List Mutable)
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    // State Checkout (Loading/Success/Error)
    var checkoutUiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    init {
        getSayur()
    }

    // 1. Ambil Data Katalog
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

    // 2. Tambah ke Keranjang
    fun addToCart(sayur: Sayur) {
        val existingItem = _cartItems.find { it.sayur.id_sayur == sayur.id_sayur }
        if (existingItem != null) {
            // Jika sudah ada, tambah qty (Cek stok dulu idealnya)
            if (existingItem.qty < sayur.stok) {
                val index = _cartItems.indexOf(existingItem)
                _cartItems[index] = existingItem.copy(qty = existingItem.qty + 1)
            }
        } else {
            // Jika belum ada, tambah baru
            _cartItems.add(CartItem(sayur, 1))
        }
    }

    // 3. Kurangi / Hapus dari Keranjang
    fun removeFromCart(cartItem: CartItem) {
        if (cartItem.qty > 1) {
            val index = _cartItems.indexOf(cartItem)
            _cartItems[index] = cartItem.copy(qty = cartItem.qty - 1)
        } else {
            _cartItems.remove(cartItem)
        }
    }

    // Helper: Hitung Total Harga
    fun calculateTotal(): Int {
        return _cartItems.sumOf { it.sayur.harga * it.qty }
    }

    // 4. Proses Checkout (Kirim ke Server)
    fun processCheckout(idPembeli: Int, alamat: String, metodeKirim: String, metodeBayar: String) {
        viewModelScope.launch {
            checkoutUiState = LoginUiState.Loading
            try {
                // Format data item sesuai permintaan Backend (transactionController)
                // items: [ {id_sayur, qty, subtotal}, ... ]
                val itemsList = _cartItems.map {
                    mapOf(
                        "id_sayur" to it.sayur.id_sayur,
                        "qty" to it.qty,
                        "subtotal" to (it.sayur.harga * it.qty)
                    )
                }

                val transactionData = mapOf(
                    "id_pembeli" to idPembeli, // Ambil dari User Session login
                    "total_bayar" to calculateTotal(),
                    "metode_kirim" to metodeKirim,
                    "metode_bayar" to metodeBayar,
                    "items" to itemsList
                )

                repository.checkout(transactionData)

                // Jika sukses
                _cartItems.clear() // Kosongkan keranjang
                checkoutUiState = LoginUiState.Success
            } catch (e: Exception) {
                checkoutUiState = LoginUiState.Error
            }
        }
    }

    fun resetCheckoutState() {
        checkoutUiState = LoginUiState.Idle
    }
}