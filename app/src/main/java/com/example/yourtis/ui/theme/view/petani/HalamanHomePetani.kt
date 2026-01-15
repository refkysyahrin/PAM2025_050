package com.example.yourtis.ui.theme.view.petani

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yourtis.R
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.modeldata.Transaksi
import com.example.yourtis.ui.theme.viewmodel.DashboardUiState
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel
import com.example.yourtis.ui.theme.viewmodel.PetaniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanHomePetani(
    onLogout: () -> Unit,
    onNavigateToKelolaProduk: () -> Unit,
    onNavigateToLaporan: () -> Unit, // Parameter Navigasi Baru
    viewModel: PetaniViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // Refresh data saat halaman dibuka
    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard Admin", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1B5E20) // Hijau Tua
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->

        when (val state = viewModel.dashboardUiState) {
            is DashboardUiState.Loading -> LoadingScreen(modifier = Modifier.padding(innerPadding))
            is DashboardUiState.Error -> ErrorScreen({ viewModel.loadDashboard() }, Modifier.padding(innerPadding))
            is DashboardUiState.Success -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // 1. KARTU PENDAPATAN
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("Total Pendapatan", color = Color.White, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Rp ${state.totalPendapatan}",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${state.jumlahPesanan} pesanan bulan ini", color = Color.LightGray, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 2. LIST PESANAN TERBARU
                    Text("Pesanan Terbaru", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.listTransaksi) { trx ->
                            ItemTransaksi(trx)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. TOMBOL NAVIGASI
                    // Tombol Kelola Produk
                    Button(
                        onClick = onNavigateToKelolaProduk,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("Kelola Produk Sayuran")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tombol Lihat Laporan
                    OutlinedButton(
                        onClick = onNavigateToLaporan, // Navigasi ke Halaman Laporan
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Lihat Laporan Lengkap", color = Color(0xFF2E7D32))
                    }
                }
            }
        }
    }
}

@Composable
fun ItemTransaksi(trx: Transaksi) {
    Card(
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
                Text("Pembeli ID: ${trx.id_pembeli}", style = MaterialTheme.typography.bodySmall)
                Text("Rp ${trx.total_bayar}", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }

            // Warna status
            val bgColor = if (trx.status == "Selesai") Color(0xFFE8F5E9) else Color(0xFFFFFDE7)
            val textColor = if (trx.status == "Selesai") Color(0xFF2E7D32) else Color(0xFFFBC02D)

            Box(
                modifier = Modifier
                    .background(color = bgColor, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(trx.status, color = textColor, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

// Komponen Helper (Agar tidak error Unresolved Reference)
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Gagal memuat data")
        Button(onClick = retryAction) { Text("Coba Lagi") }
    }
}