package com.example.yourtis.ui.theme.view.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
    // State Form Input
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var noHp by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }

    // State Pilihan Role (Petani vs Pembeli)
    val roles = listOf("Pembeli", "Petani")
    val (selectedRole, onOptionSelected) = remember { mutableStateOf(roles[0]) }

    var passwordVisible by remember { mutableStateOf(false) }
    val registerState = viewModel.loginUiState

    // Efek Samping: Jika sukses, kembali ke login
    LaunchedEffect(registerState) {
        if (registerState is LoginUiState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            // Judul Halaman Sederhana
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.register_title)) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // Agar bisa discroll jika keyboard muncul
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Input Username
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.username)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Input Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Input Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
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
                value = noHp,
                onValueChange = { noHp = it },
                label = { Text(stringResource(R.string.no_hp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Input Alamat
            OutlinedTextField(
                value = alamat,
                onValueChange = { alamat = it },
                label = { Text(stringResource(R.string.alamat)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Pilihan Role (Radio Buttons)
            Text(
                text = stringResource(R.string.pilih_peran),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                roles.forEach { text ->
                    Row(
                        Modifier
                            .height(56.dp)
                            .selectable(
                                selected = (text == selectedRole),
                                onClick = { onOptionSelected(text) },
                                role = androidx.compose.ui.semantics.Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedRole),
                            onClick = null // null karena onclick ditangani oleh Row
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 7. Pesan Error
            if (registerState is LoginUiState.Error) {
                Text(
                    text = viewModel.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // 8. Tombol Daftar
            Button(
                onClick = {
                    viewModel.register(
                        username, email, password, selectedRole, noHp, alamat
                    )
                },
                enabled = registerState !is LoginUiState.Loading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (registerState is LoginUiState.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(stringResource(R.string.btn_register))
                }
            }

            // Tombol Kembali
            TextButton(onClick = onNavigateBack) {
                Text(stringResource(R.string.btn_batal))
            }
        }
    }
}