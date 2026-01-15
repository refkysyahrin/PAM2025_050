package com.example.yourtis.ui.theme.view.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yourtis.R
import com.example.yourtis.ui.theme.viewmodel.AuthViewModel
import com.example.yourtis.ui.theme.viewmodel.LoginUiState
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanRegister(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }

    // Memantau status dari ViewModel
    val registerState = viewModel.uiState // PERBAIKAN: Gunakan 'uiState'

    // State Pilihan Role
    val roles = listOf("Pembeli", "Petani")

    // Efek Samping: Jika sukses
    LaunchedEffect(registerState) {
        if (registerState is LoginUiState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Registrasi Akun Baru") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Input Username (Langsung bind ke ViewModel)
            OutlinedTextField(
                value = viewModel.username, // PERBAIKAN: Binding ke ViewModel
                onValueChange = { viewModel.username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Input Email
            OutlinedTextField(
                value = viewModel.email, // PERBAIKAN: Binding ke ViewModel
                onValueChange = { viewModel.email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Input Password
            OutlinedTextField(
                value = viewModel.password, // PERBAIKAN: Binding ke ViewModel
                onValueChange = { viewModel.password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Input No HP
            OutlinedTextField(
                value = viewModel.noHp, // PERBAIKAN: Binding ke ViewModel
                onValueChange = { viewModel.noHp = it },
                label = { Text("No. Handphone") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Input Alamat
            OutlinedTextField(
                value = viewModel.alamat, // PERBAIKAN: Binding ke ViewModel
                onValueChange = { viewModel.alamat = it },
                label = { Text("Alamat Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Pilihan Role (Radio Buttons)
            Text(
                text = "Daftar Sebagai:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                roles.forEach { text ->
                    Row(
                        Modifier
                            .weight(1f)
                            .selectable(
                                selected = (text == viewModel.role),
                                onClick = { viewModel.role = text },
                                role = androidx.compose.ui.semantics.Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == viewModel.role),
                            onClick = null
                        )
                        Text(text = text, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 7. Pesan Error
            if (registerState is LoginUiState.Error) {
                Text(
                    text = "Gagal Daftar: Email mungkin sudah dipakai",
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 8. Tombol Daftar
            Button(
                onClick = { viewModel.register(onRegisterSuccess) }, // PERBAIKAN: Parameter hanya callback
                enabled = registerState !is LoginUiState.Loading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (registerState is LoginUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Daftar Akun Baru")
                }
            }

            TextButton(onClick = onNavigateBack) {
                Text("Batal")
            }
        }
    }
}