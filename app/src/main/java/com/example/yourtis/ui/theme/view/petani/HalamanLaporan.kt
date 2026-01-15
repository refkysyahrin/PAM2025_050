package com.example.yourtis.ui.theme.view.petani

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yourtis.modeldata.Transaksi
import com.example.yourtis.ui.theme.viewmodel.DashboardUiState
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel
import com.example.yourtis.ui.theme.viewmodel.PetaniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanLaporan(
    onNavigateBack: () -> Unit,
    viewModel: PetaniViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    LaunchedEffect(Unit) { viewModel.loadDashboard() }

    var showDialog by remember { mutableStateOf(false) }
    var selectedTransaksi by remember { mutableStateOf<Transaksi?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = viewModel.dashboardUiState) {
            is DashboardUiState.Loading -> LoadingScreen(Modifier.padding(innerPadding))
            is DashboardUiState.Error -> ErrorScreen({ viewModel.loadDashboard() }, Modifier.padding(innerPadding))
            is DashboardUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.listTransaksi) { trx ->
                        ItemLaporan(trx) {
                            selectedTransaksi = trx
                            showDialog = true
                        }
                    }
                }
            }
        }

        if (showDialog && selectedTransaksi != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Update Status Pesanan") },
                text = { Text("ID: ${selectedTransaksi!!.id_transaksi}\nUbah status menjadi:") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.updateStatusTransaksi(selectedTransaksi!!.id_transaksi, "Selesai")
                        showDialog = false
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                        Text("Selesai")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        viewModel.updateStatusTransaksi(selectedTransaksi!!.id_transaksi, "Proses")
                        showDialog = false
                    }) { Text("Proses") }
                }
            )
        }
    }
}

@Composable
fun ItemLaporan(trx: Transaksi, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(trx.id_transaksi, fontWeight = FontWeight.Bold)
                Text("Total: Rp ${trx.total_bayar}", color = Color(0xFF2E7D32))
                Text("${trx.metode_kirim} | ${trx.metode_bayar}", style = MaterialTheme.typography.bodySmall)
            }
            val (bgColor, textColor) = when(trx.status) {
                "Selesai" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                "Proses" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
                else -> Color(0xFFFFFDE7) to Color(0xFFFBC02D)
            }
            Box(
                modifier = Modifier.background(bgColor, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(trx.status, color = textColor, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}