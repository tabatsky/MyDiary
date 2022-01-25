package jatx.mydiary

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import jatx.mydiary.backup.BackupData
import jatx.mydiary.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.PrintWriter
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private val loadLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                mainViewModel.onLoadPermissionGranted()
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

        setContent {
            MainScreen()
        }

        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                mainViewModel.loadFlow.collect {
                    withContext(Dispatchers.Main) {
                        loadData()
                    }
                }
            }
        }

        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                mainViewModel.saveFlow.collect {
                    withContext(Dispatchers.Main) {
                        Log.e("flow", "saveData")
                        saveData()
                    }
                }
            }
        }

        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                mainViewModel.dateTimePickerFlow.collect {
                    withContext(Dispatchers.Main) {
                        selectDate()
                    }
                }
            }
        }
    }

    private fun loadData() = loadLauncher.launch(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private fun saveData() = saveLauncher.launch(
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    private fun selectDate() {
        calendar = Calendar.getInstance()
        calendar?.timeInMillis = System.currentTimeMillis()

        val year = calendar?.get(Calendar.YEAR) ?: 0
        val month = calendar?.get(Calendar.MONTH) ?: 0
        val day = calendar?.get(Calendar.DAY_OF_MONTH) ?: 0

        val dpd = DatePickerDialog(this, { _, year, month, day ->
            calendar?.set(Calendar.YEAR, year)
            calendar?.set(Calendar.MONTH, month)
            calendar?.set(Calendar.DAY_OF_MONTH, day)

            selectTime()
        }, year, month, day)

        dpd.show()
    }

    private fun selectTime() {
        val hour = calendar?.get(Calendar.HOUR_OF_DAY) ?: 0
        val minute = calendar?.get(Calendar.MINUTE) ?: 0

        val tpd = TimePickerDialog(this, { _: TimePicker, hour: Int, minute: Int ->
            calendar?.set(Calendar.HOUR_OF_DAY, hour)
            calendar?.set(Calendar.MINUTE, minute)

            val time = calendar?.timeInMillis ?: System.currentTimeMillis()
            mainViewModel.createEntry(time)
        }, hour, minute, true)

        tpd.show()
    }

}
