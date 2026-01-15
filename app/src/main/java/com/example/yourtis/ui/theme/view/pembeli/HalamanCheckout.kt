package com.example.yourtis.ui.theme.view.pembeli

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yourtis.ui.theme.viewmodel.LoginUiState
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanCheckout(
    onNavigateBack: () -> Unit,
    onCheckoutSuccess: () -> Unit,
    viewModel: PembeliViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    var alamat by remember { mutableStateOf("") }
    var metodeKirim by remember { mutableStateOf("Kurir") }
    var metodeBayar by remember { mutableStateOf("Transfer") }

    // Pantau status sukses
    LaunchedEffect(viewModel.checkoutUiState) {
        if (viewModel.checkoutUiState is LoginUiState.Success) {
            onCheckoutSuccess()
            viewModel.resetCheckoutState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout Pesanan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Ringkasan Belanja", style = MaterialTheme.typography.titleMedium)
            Text("Total Tagihan: Rp ${viewModel.calculateTotal()}", color = Color(0xFF2E7D32))

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = alamat,
                onValueChange = { alamat = it },
                label = { Text("Alamat Pengiriman Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Metode Pengiriman")
            Row {
                RadioButton(selected = metodeKirim == "Kurir", onClick = { metodeKirim = "Kurir" })
                Text("Kurir", modifier = Modifier.padding(top = 12.dp))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = metodeKirim == "Ambil Sendiri", onClick = { metodeKirim = "Ambil Sendiri" })
                Text("Ambil Sendiri", modifier = Modifier.padding(top = 12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Metode Pembayaran")
            Row {
                RadioButton(selected = metodeBayar == "Transfer", onClick = { metodeBayar = "Transfer" })
                Text("Transfer", modifier = Modifier.padding(top = 12.dp))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = metodeBayar == "COD", onClick = { metodeBayar = "COD" })
                Text("COD (Bayar di Tempat)", modifier = Modifier.padding(top = 12.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // Sementara ID Pembeli di hardcode 1, nanti bisa ambil dari session login
                    viewModel.processCheckout(1, alamat, metodeKirim, metodeBayar)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = alamat.isNotBlank() && viewModel.checkoutUiState !is LoginUiState.Loading
            ) {
                if (viewModel.checkoutUiState is LoginUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Konfirmasi Pesanan")
                }
            }
        }
    }
}