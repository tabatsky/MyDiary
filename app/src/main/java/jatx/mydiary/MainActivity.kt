package jatx.mydiary

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import dagger.hilt.android.AndroidEntryPoint
import jatx.mydiary.consumer.EventConsumer
import jatx.mydiary.navigation.Router
import jatx.mydiary.navigation.ScreenVariant
import jatx.mydiary.presentation.auth.AuthScreen
import jatx.mydiary.presentation.auth.AuthViewModel
import jatx.mydiary.presentation.main.MainScreen
import jatx.mydiary.presentation.main.MainViewModel
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val loadLauncher =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) {
            it?.let { uri ->
                mainViewModel.onLoadFromUri(uri)
            }
        }

    private val saveLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { map ->
            if (map.all { it.value }) {
                mainViewModel.onSavePermissionGranted()
            }
        }

    private var calendar: Calendar? = null

    @ExperimentalFoundationApi
    @ExperimentalGraphicsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.init()
        authViewModel.init()

        setContent {
            BackHandler {
                if (Router.currentScreenVariant == ScreenVariant.MainScreenVariant) {
                    finish()
                } else {
                    Router.pop()
                }
            }
            EventConsumer(mainViewModel.loadChannel) {
                loadData()
            }
            EventConsumer(mainViewModel.saveChannel) {
                saveData()
            }
            EventConsumer(mainViewModel.showDateTimePickerChannel) {
                selectDateAndTime {
                    val time = calendar?.timeInMillis ?: System.currentTimeMillis()
                    mainViewModel.createEntry(time)
                }
            }
            when(Router.currentScreenVariant) {
                is ScreenVariant.MainScreenVariant ->
                    MainScreen()
                is ScreenVariant.AuthScreenVariant ->
                    AuthScreen()
            }
        }
    }

    private fun loadData() = loadLauncher.launch(arrayOf("*/*"))

    private fun saveData() {
        if (Build.VERSION.SDK_INT <= 29) {
            saveLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        } else if (Build.VERSION.SDK_INT <= 32) {
            saveLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        } else {
            mainViewModel.onSavePermissionGranted()
        }
    }

    private fun selectDateAndTime(onSuccess: () -> Unit) {
        calendar = Calendar.getInstance()
        calendar?.timeInMillis = System.currentTimeMillis()

        val year = calendar?.get(Calendar.YEAR) ?: 0
        val month = calendar?.get(Calendar.MONTH) ?: 0
        val day = calendar?.get(Calendar.DAY_OF_MONTH) ?: 0

        val dpd = DatePickerDialog(this, { _, year, month, day ->
            calendar?.set(Calendar.YEAR, year)
            calendar?.set(Calendar.MONTH, month)
            calendar?.set(Calendar.DAY_OF_MONTH, day)

            selectTime(onSuccess)
        }, year, month, day)

        dpd.show()
    }

    private fun selectTime(onSuccess: () -> Unit) {
        val hour = calendar?.get(Calendar.HOUR_OF_DAY) ?: 0
        val minute = calendar?.get(Calendar.MINUTE) ?: 0

        val tpd = TimePickerDialog(this, { _: TimePicker, hour: Int, minute: Int ->
            calendar?.set(Calendar.HOUR_OF_DAY, hour)
            calendar?.set(Calendar.MINUTE, minute)

            onSuccess()
        }, hour, minute, true)

        tpd.show()
    }

}
