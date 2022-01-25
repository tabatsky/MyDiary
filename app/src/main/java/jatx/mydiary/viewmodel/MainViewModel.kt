package jatx.mydiary.viewmodel

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.mydiary.R
import jatx.mydiary.backup.BackupData
import jatx.mydiary.domain.models.Entry
import jatx.mydiary.domain.models.formatTimeList
import jatx.mydiary.domain.usecase.*
import jatx.mydiary.toasts.Toasts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.PrintWriter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllUseCase: GetAllUseCase,
    private val getAllSuspendUseCase: GetAllSuspendUseCase,
    private val insertUseCase: InsertUseCase,
    private val deleteUseCase: DeleteUseCase,
    private val insertReplaceListUseCase: InsertReplaceListUseCase,
    private val toasts: Toasts
): ViewModel() {
    private val _entries = MutableStateFlow(listOf<Entry>())
    val entries = _entries.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog = _showDeleteDialog.asStateFlow()

    private val _entryToDelete = MutableStateFlow<Entry?>(null)
    val entryToDelete = _entryToDelete.asStateFlow()

    private val _invalidateCounter = MutableStateFlow(0)
    val invalidateCounter = _invalidateCounter.asStateFlow()

    private var _typeForEntry = MutableStateFlow(-1)
    val typeForEntry = _typeForEntry.asStateFlow()

    private var onLoadPermissionRequest: () -> Unit = {}
    private var onSavePermissionRequest: () -> Unit = {}
    private var onShowDateTimePicker: () -> Unit = {}

    private var getAllJob: Job? = null

    val loadFlow: Flow<Any?> = callbackFlow {
        onLoadPermissionRequest = {
            trySend(null)
        }
        awaitClose {
            onLoadPermissionRequest = {}
        }
    }

    val saveFlow: Flow<Any?> = callbackFlow {
        onSavePermissionRequest = {
            trySend(null)
        }
        awaitClose {
            onSavePermissionRequest = {}
        }
    }

    val dateTimePickerFlow: Flow<Any?> = callbackFlow {
        onShowDateTimePicker = {
            trySend(null)
        }
        awaitClose {
            onShowDateTimePicker = {}
        }
    }

    fun init() {
        getAllJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        getAllJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getAllUseCase
                    .execute()
                    .collect {
                        withContext(Dispatchers.Main) {
                            _entries.value = it
                            _invalidateCounter.value += 1
                        }
                    }
            }
        }
    }

    fun setTypeForEntry(type: Int) {
        _typeForEntry.value = type
    }

    fun createEntry() {
        val entry = Entry(
            type = typeForEntry.value,
            time = System.currentTimeMillis()
        )
        insertUseCase.execute(entry)
    }

    fun createEntry(time: Long) {
        val entry = Entry(
            type = typeForEntry.value,
            time = time
        )
        insertUseCase.execute(entry)
    }

    fun deleteEntry() {
        Log.e("delete", entryToDelete.value?.formatTimeList() ?: "null")
        entryToDelete.value?.let {
            deleteUseCase.execute(it)
        }
    }

    fun setEntryToDelete(entry: Entry?) {
        _entryToDelete.value = entry
    }

    fun setShowDeleteDialog(value: Boolean, ) {
        _showDeleteDialog.value = value
    }

    fun onLoadPermissionGranted() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val inFile = File(
                        Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "MyDiary.json"
                    )
                    val sc = Scanner(inFile)
                    val backupDataStr = sc.nextLine()
                    sc.close()
                    val backupData = Gson().fromJson(backupDataStr, BackupData::class.java)
                    Log.e("backup", backupData.toString())
                    insertReplaceListUseCase.execute(backupData.list)
                    withContext(Dispatchers.Main) {
                        toasts.showToast(R.string.toast_load_data_success)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        toasts.showToast(R.string.toast_some_error)
                    }
                }
            }
        }
    }

    fun onSavePermissionGranted() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val list = getAllSuspendUseCase.execute()
                val backupData = BackupData(list)
                val backupDataStr = Gson().toJson(backupData)
                Log.e("backup", backupDataStr)
                try {
                    val dir = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    dir.mkdirs()
                    val outFile = File(dir, "MyDiary.json")
                    val pw = PrintWriter(outFile)
                    pw.println(backupDataStr)
                    pw.flush()
                    pw.close()
                    withContext(Dispatchers.Main) {
                        toasts.showToast(R.string.toast_save_data_success)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        toasts.showToast(R.string.toast_some_error)
                    }
                }
            }
        }
    }

    fun loadData() {
        onLoadPermissionRequest()
    }

    fun saveData() {
        onSavePermissionRequest()
    }

    fun showDateTimePicker() {
        onShowDateTimePicker()
    }
}