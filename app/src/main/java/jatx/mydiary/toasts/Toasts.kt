package jatx.mydiary.toasts

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Toasts @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun showToast(toastText: String) {
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
    }

    fun showToast(@StringRes resId: Int) {
        val toastText = context.getString(resId)
        showToast(toastText)
    }
}