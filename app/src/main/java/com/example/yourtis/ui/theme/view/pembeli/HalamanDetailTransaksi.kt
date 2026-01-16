package com.example.yourtis.ui.theme.view.pembeli

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yourtis.modeldata.DetailTransaksi
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailTransaksi(
    idTransaksi: Int,
    onNavigateBack: () -> Unit,
    viewModel: PembeliViewModel
) {
    // Cari data transaksi utama dari list di ViewModel
    val transaksi = viewModel.listTransaksi.find { it.id_transaksi == idTransaksi }
    
    // State untuk menampung item detail (sayur apa saja)
    var detailItems by remember { mutableStateOf<List<DetailTransaksi>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(idTransaksi) {
        isLoading = true
        try {
            // 1. Coba ambil dari API spesifik detail
            val items = viewModel.getTransactionDetails(idTransaksi)
            
            if (items.isNotEmpty()) {
                detailItems = items
                Log.d("DETAIL_TRX", "Items loaded from API: ${items.size}")
            } else if (transaksi?.items != null) {
                // 2. Fallback: Gunakan data nested jika ada
                detailItems = transaksi.items
                Log.d("DETAIL_TRX", "Items loaded from nested data")
            }
        } catch (e: Exception) {
            Log.e("DETAIL_TRX", "Error loading details", e)
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pesanan #${idTransaksi}", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        }
    ) { innerPadding ->
        if (transaksi == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Data transaksi tidak ditemukan")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Info Status & Ringkasan
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Status Pesanan", fontWeight = FontWeight.Bold)
                            Text(
                                transaksi.status,
                                color = if(transaksi.status == "Selesai") Color(0xFF2E7D32) else Color(0xFFF9A825),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))
                        Text("Metode Pembayaran: ${transaksi.metode_bayar}", fontSize = 14.sp)
                        Text("Metode Pengiriman: ${transaksi.metode_kirim}", fontSize = 14.sp)
                        Text("Alamat: ${transaksi.alamat_pengiriman}", fontSize = 14.sp)
                        Text("Tanggal: ${transaksi.tgl_transaksi}", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Daftar Sayur yang Dibeli", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2E7D32))
                    }
                } else if (detailItems.isEmpty()) {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text("Tidak ada detail item sayur.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(detailItems) { item ->
                            ItemDetailCard(item)
                        }
                    }
                }

                HorizontalDivider(Modifier.padding(vertical = 16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Pembayaran", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        "Rp ${transaksi.total_bayar}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}

@Composable
fun ItemDetailCard(item: DetailTransaksi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nama_sayur ?: "Sayur (ID: ${item.id_sayur})",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(text = "Jumlah: ${item.qty} kg", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Text(
                text = "Rp ${item.subtotal}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF2E7D32)
            )
        }
    }
}
