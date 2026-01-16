package com.example.yourtis.ui.theme.view.pembeli

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yourtis.ui.theme.view.petani.ItemTransaksiLengkap
import com.example.yourtis.ui.theme.viewmodel.PembeliViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanPesananPembeli(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: PembeliViewModel
) {
    val listTransaksi = viewModel.listTransaksi
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    // Fungsi refresh data
    val refreshData = {
        isRefreshing = true
        viewModel.getTransactions()
        isRefreshing = false
    }

    LaunchedEffect(Unit) {
        viewModel.getTransactions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesanan Saya", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = { refreshData() },
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            if (listTransaksi.isEmpty() && !isRefreshing) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Belum ada riwayat pesanan.", color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.getTransactions() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text("Cek Pesanan")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listTransaksi) { trx ->
                        ItemTransaksiLengkap(
                            trx = trx,
                            onClick = { onNavigateToDetail(trx.id_transaksi) }
                        )
                    }
                }
            }
        }
    }
}
