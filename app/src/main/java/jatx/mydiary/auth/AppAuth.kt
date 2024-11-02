package jatx.mydiary.auth

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import jatx.mydiary.R
import jatx.mydiary.toasts.Toasts
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext private val context: Context,
    private val toasts: Toasts
) {
    private val auth by lazy {
        Firebase.auth
    }
    var theUser: FirebaseUser? = null
        private set

    private val executor = Executor { command ->
        try {
            command?.run()
        } catch (e: Exception) {
            Log.e("executor", "error", e)
            toasts.showToast(R.string.toast_some_error)
        }
    }

    fun loadAuth(onSuccess: (String, String) -> Unit) {
        val sp = context.getSharedPreferences("MyDict", 0)
        val login = sp.getString("email", "")!!
        val password = sp.getString("password", "")!!
        onSuccess(login, password)
    }

    private fun saveAuth(email: String, password: String) {
        val sp = context.getSharedPreferences("MyDict", 0)
        val editor = sp.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            toasts.showToast(R.string.toast_fill_email_and_password)
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(executor) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Log.e("user", user?.uid.toString())
                    saveAuth(email, password)
                    toasts.showToast(R.string.toast_sign_in_success)
                    theUser = user
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e("sign in", "signInWithEmail:failure", task.exception)
                    toasts.showToast(R.string.toast_sign_in_error)
                }
            }
    }

    fun signUp(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            toasts.showToast(R.string.toast_fill_email_and_password)
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(executor) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Log.e("user", user?.uid.toString())
                    toasts.showToast(R.string.toast_sign_up_success)
                    saveAuth(email, password)
                    theUser = user
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e("sign up", "createUserWithEmail:failure", task.exception)
                    toasts.showToast(R.string.toast_sign_up_error)
                }
            }
    }
}