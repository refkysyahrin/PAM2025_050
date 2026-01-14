package com.example.yourtis.ui.theme.view.pembeli

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // PENTING: Import ini wajib ada
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yourtis.R
import com.example.yourtis.ui.theme.viewmodel.CartItem
import com.example.yourtis.ui.theme.viewmodel.LoginUiState
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanCart(
    onNavigateBack: () -> Unit,
    viewModel: PembeliViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val cartItems = viewModel.cartItems
    val checkoutState = viewModel.checkoutUiState

    // State Form Checkout
    var alamat by remember { mutableStateOf("") }
    var metodeKirim by remember { mutableStateOf("Diantar") }
    var metodeBayar by remember { mutableStateOf("Transfer") }

    val ongkir = if (metodeKirim == "Diantar") 5000 else 0
    val subtotal = viewModel.calculateTotal()
    val totalBayar = subtotal + ongkir

    // Efek Samping Checkout (Toast Sukses/Gagal)
    LaunchedEffect(checkoutState) {
        if (checkoutState is LoginUiState.Success) {
            Toast.makeText(context, "Pesanan Berhasil Dibuat!", Toast.LENGTH_LONG).show()
            viewModel.resetCheckoutState()
            onNavigateBack() // Kembali ke Katalog
        } else if (checkoutState is LoginUiState.Error) {
            Toast.makeText(context, "Gagal membuat pesanan", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keranjang & Checkout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // PERBAIKAN: Menggunakan AutoMirrored agar support RTL
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Button(
                    onClick = {
                        if (alamat.isBlank()) {
                            Toast.makeText(context, "Alamat wajib diisi!", Toast.LENGTH_SHORT).show()
                        } else {
                            // Hardcode ID User 2 sementara
                            viewModel.processCheckout(2, alamat, metodeKirim, metodeBayar)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    enabled = checkoutState !is LoginUiState.Loading
                ) {
                    if (checkoutState is LoginUiState.Loading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("Buat Pesanan - Rp $totalBayar")
                    }
                }
            }
        }
    ) { innerPadding ->

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Keranjang belanja kosong", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. DAFTAR BARANG
                // PERBAIKAN: Gunakan 'items(items = List)' bukan 'count'
                items(items = cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onRemove = { viewModel.removeFromCart(item) }
                    )
                }

                // 2. RINGKASAN BIAYA
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Ringkasan Belanja", fontWeight = FontWeight.Bold)
                            // PERBAIKAN: Divider -> HorizontalDivider
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            RowSummary("Subtotal", subtotal)
                            RowSummary("Ongkir", ongkir)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            RowSummary("Total", totalBayar, isTotal = true)
                        }
                    }
                }

                // 3. FORM PENGIRIMAN
                item {
                    Text("Detail Pengiriman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                item {
                    OutlinedTextField(
                        value = alamat,
                        onValueChange = { alamat = it },
                        label = { Text("Alamat Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }

                // Pilihan Metode Kirim
                item {
                    Text("Metode Pengiriman", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    Row(Modifier.fillMaxWidth()) {
                        OptionRadio("Diantar", metodeKirim == "Diantar") { metodeKirim = "Diantar" }
                        Spacer(modifier = Modifier.width(16.dp))
                        OptionRadio("Pickup", metodeKirim == "Pickup") { metodeKirim = "Pickup" }
                    }
                }

                // Pilihan Metode Bayar
                item {
                    Text("Metode Pembayaran", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    Row(Modifier.fillMaxWidth()) {
                        OptionRadio("Transfer", metodeBayar == "Transfer") { metodeBayar = "Transfer" }
                        Spacer(modifier = Modifier.width(16.dp))
                        OptionRadio("COD", metodeBayar == "COD") { metodeBayar = "COD" }
                    }
                }

                if (metodeBayar == "Transfer") {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Transfer ke:", fontWeight = FontWeight.Bold)
                                Text("BCA - 1234567890")
                                Text("a.n. Petani Sayur")
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            val imageUrl = item.sayur.gambar_url?.replace("localhost", "10.0.2.2")
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).build(),
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_launcher_foreground)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.sayur.nama_sayur, fontWeight = FontWeight.Bold)
                Text("Rp ${item.sayur.harga} x ${item.qty} kg")
                Text("Rp ${item.sayur.harga * item.qty}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun RowSummary(label: String, amount: Int, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        // PERBAIKAN: Typo 'colorSchme' diperbaiki jadi 'colorScheme'
        Text(
            text = "Rp $amount",
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.primary else Color.Unspecified
        )
    }
}

@Composable
fun OptionRadio(text: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .selectable(selected = selected, onClick = onSelect)
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = if (selected) MaterialTheme.colorScheme.primary else Color.Black,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}