package com.example.yourtis.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.modeldata.User
import com.example.yourtis.repositori.YourTisRepository
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data class Success(val sayur: List<Sayur>) : HomeUiState
    object Error : HomeUiState
    object Loading : HomeUiState
}

data class CartItem(
    val sayur: Sayur,
    var qty: Int
)

class PembeliViewModel(private val repository: YourTisRepository) : ViewModel() {

    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    var checkoutUiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    init {
        getSayur()
    }

    fun getSayur() {
        viewModelScope.launch {
            homeUiState = HomeUiState.Loading
            try {
                val listSayur = repository.getSayur()
                homeUiState = HomeUiState.Success(listSayur)
            } catch (e: Exception) {
                homeUiState = HomeUiState.Error
            }
        }
    }

    fun addToCart(sayur: Sayur) {
        val existingItem = _cartItems.find { it.sayur.id_sayur == sayur.id_sayur }
        if (existingItem != null) {
            if (existingItem.qty < sayur.stok) {
                val index = _cartItems.indexOf(existingItem)
                _cartItems[index] = existingItem.copy(qty = existingItem.qty + 1)
            }
        } else {
            _cartItems.add(CartItem(sayur, 1))
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        if (cartItem.qty > 1) {
            val index = _cartItems.indexOf(cartItem)
            _cartItems[index] = cartItem.copy(qty = cartItem.qty - 1)
        } else {
            _cartItems.remove(cartItem)
        }
    }

    fun calculateTotal(): Int {
        return _cartItems.sumOf { it.sayur.harga * it.qty }
    }

    // Proses Checkout sesuai standar SRS
    fun processCheckout(idPembeli: Int, alamat: String, metodeKirim: String, metodeBayar: String) {
        viewModelScope.launch {
            checkoutUiState = LoginUiState.Loading
            try {
                val itemsList = _cartItems.map {
                    mapOf(
                        "id_sayur" to it.sayur.id_sayur,
                        "qty" to it.qty,
                        "subtotal" to (it.sayur.harga * it.qty)
                    )
                }

                val transactionData = mapOf(
                    "id_pembeli" to idPembeli,
                    "alamat_pengiriman" to alamat,
                    "total_bayar" to calculateTotal(),
                    "metode_kirim" to metodeKirim,
                    "metode_bayar" to metodeBayar,
                    "items" to itemsList
                )

                repository.checkout(transactionData)
                _cartItems.clear()
                // Berikan feedback sukses dengan User dummy
                checkoutUiState = LoginUiState.Success(User(0, "", "", "", "", ""))
            } catch (e: Exception) {
                checkoutUiState = LoginUiState.Error
            }
        }
    }

    fun resetCheckoutState() {
        checkoutUiState = LoginUiState.Idle
    }
}