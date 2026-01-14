package com.example.yourtis.ui.theme.view.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yourtis.R
import com.example.yourtis.modeldata.User
import com.example.yourtis.ui.theme.viewmodel.AuthViewModel
import com.example.yourtis.ui.theme.viewmodel.LoginUiState
import com.example.yourtis.ui.theme.viewmodel.PenyediaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanLogin(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory),
    onLoginSuccess: (User) -> Unit, // Callback navigasi jika login sukses
    onNavigateToRegister: () -> Unit // Callback navigasi ke halaman register
) {
    // State lokal untuk input form
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Memantau status login dari ViewModel
    val loginState = viewModel.loginUiState

    // Efek Samping: Jika login sukses, panggil callback navigasi
    LaunchedEffect(loginState) {
        if (loginState is LoginUiState.Success && viewModel.currentUser != null) {
            onLoginSuccess(viewModel.currentUser!!)
            viewModel.resetState() // Reset agar tidak auto-login jika kembali ke layar ini
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Judul & Slogan Aplikasi
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(id = R.string.app_slogan),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_extra_large))
        )

        // 2. Form Input Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.email)) },
            placeholder = { Text(stringResource(id = R.string.hint_email)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

        // 3. Form Input Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.password)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

        // 4. Pesan Error (Jika ada)
        if (loginState is LoginUiState.Error) {
            Text(
                text = viewModel.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_small))
            )
        }

        // 5. Tombol Login & Loading Indicator
        if (loginState is LoginUiState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.input_field_height)),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = stringResource(id = R.string.btn_login))
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

        // 6. Tombol Navigasi ke Register
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(text = stringResource(id = R.string.text_belum_punya_akun))
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = stringResource(id = R.string.btn_register),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}