package com.example.yourtis.ui.theme.view.petani

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.yourtis.R
import com.example.yourtis.modeldata.Sayur
import com.example.yourtis.ui.theme.viewmodel.HomeUiState
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel
import com.example.yourtis.ui.theme.viewmodel.PetaniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanHomePetani(
    onLogout: () -> Unit,
    onNavigateToEntry: () -> Unit, // Navigasi ke Halaman Tambah
    viewModel: PetaniViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard Petani") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Text("Logout")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Sayur")
            }
        }
    ) { innerPadding ->

        // Cek Status UI
        when (val state = viewModel.homeUiState) {
            is HomeUiState.Loading -> LoadingScreen(modifier = Modifier.padding(innerPadding))
            is HomeUiState.Success -> ListSayurScreen(
                listSayur = state.sayur,
                modifier = Modifier.padding(innerPadding),
                onDelete = { id -> viewModel.deleteSayur(id) }
            )
            is HomeUiState.Error -> ErrorScreen(
                retryAction = { viewModel.getSayur() },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

// 1. Tampilan LIST SAYUR
@Composable
fun ListSayurScreen(
    listSayur: List<Sayur>,
    modifier: Modifier = Modifier,
    onDelete: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(listSayur) { sayur ->
            CardSayur(sayur = sayur, onDelete = onDelete)
        }
    }
}

// 2. Kartu Per Item Sayur
@Composable
fun CardSayur(sayur: Sayur, onDelete: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Gambar Produk (Menggunakan Coil)
            // Replace localhost dengan 10.0.2.2 agar bisa diakses emulator
            val imageUrl = sayur.gambar_url?.replace("localhost", "10.0.2.2")

            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                error = painterResource(R.drawable.ic_launcher_foreground), // Pastikan ada default icon/error icon
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = sayur.nama_sayur,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = sayur.nama_sayur,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rp ${sayur.harga} / kg",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Stok: ${sayur.stok}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Tombol Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { onDelete(sayur.id_sayur) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

// 3. Tampilan Loading & Error
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
        Text(stringResource(R.string.network_error))
        Button(onClick = retryAction) {
            Text("Coba Lagi")
        }
    }
}