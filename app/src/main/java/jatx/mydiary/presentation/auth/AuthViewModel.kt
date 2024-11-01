package jatx.mydiary.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.mydiary.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
): ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    fun init() {
        appAuth.loadAuth { theEmail, thePassword ->
            email = theEmail
            password = thePassword

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn()
            }
        }
    }

    fun signIn() = appAuth.signIn(email, password)

    fun signUp() = appAuth.signUp(email, password)
}