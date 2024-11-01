package jatx.mydiary.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen(authViewModel: AuthViewModel = viewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Авторизация",
                        color = Color.White
                    )
                },
                backgroundColor = Color.Black
            )
            OutlinedTextField(
                value = authViewModel.email,
                onValueChange = { authViewModel.email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = TextFieldDefaults
                    .outlinedTextFieldColors(
                        backgroundColor = Color.LightGray,
                        textColor = Color.Black
                    )
            )
            OutlinedTextField(
                value = authViewModel.password,
                onValueChange = { authViewModel.password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = TextFieldDefaults
                    .outlinedTextFieldColors(
                        backgroundColor = Color.LightGray,
                        textColor = Color.Black
                    )
            )
            Button(
                onClick = {
                    authViewModel.signIn()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text("Войти")
            }
            Button(
                onClick = {
                    authViewModel.signUp()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text("Зарегистрироваться")
            }
        }
    }
}