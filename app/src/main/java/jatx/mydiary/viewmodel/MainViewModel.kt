package jatx.mydiary.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jatx.mydiary.R
import jatx.mydiary.backup.BackupData
import jatx.mydiary.domain.models.Entry
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
    private val deleteByTypeUseCase: DeleteByTypeUseCase,
    private val deleteAllUseCase: DeleteAllUseCase,
    private val insertReplaceListUseCase: InsertReplaceListUseCase,
    private val toasts: Toasts,
    @ApplicationContext private val appContext: Context
): ViewModel() {
    private val _entries = MutableStateFlow(listOf<Entry>())
    val entries = _entries.asStateFlow()

    private val _currentType = MutableStateFlow(-1)
    val currentType = _currentType.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog = _showDeleteDialog.asStateFlow()

    private val _entryToDelete = MutableStateFlow<Entry?>(null)
    val entryToDelete = _entryToDelete.asStateFlow()

    private val _showDeleteByTypeDialog = MutableStateFlow(false)
    val showDeleteByTypeDialog = _showDeleteByTypeDialog.asStateFlow()

    private val _typeToDelete = MutableStateFlow(-1)
    val typeToDelete = _typeToDelete.asStateFlow()

    private var _typeForEntry = MutableStateFlow(-1)
    private val typeForEntry = _typeForEntry.asStateFlow()

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
                    .combine(currentType) { list, type ->
                        if (type == -1) {
                            list
                        } else {
                            list.filter { it.type == type }
                        }
                    }
                    .collect {
                        withContext(Dispatchers.Main) {
                            _entries.value = it
                        }
                    }
            }
        }
    }

    fun setCurrentType(type: Int) {
        if (currentType.value != type) {
            _currentType.value = type
        } else {
            _currentType.value = -1
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
        viewModelScope.launch {
            insertUseCase.execute(entry)
        }
    }

    fun createEntry(time: Long) {
        val entry = Entry(
            type = typeForEntry.value,
            time = time
        )
        viewModelScope.launch {
            insertUseCase.execute(entry)
        }
    }

    fun deleteEntry() {
        entryToDelete.value?.let {
            viewModelScope.launch {
                deleteUseCase.execute(it)
            }
        }
    }

    fun deleteByType() {
        viewModelScope.launch {
            deleteByTypeUseCase.execute(typeToDelete.value)
        }
    }

    fun setEntryToDelete(entry: Entry?) {
        _entryToDelete.value = entry
    }

    fun setShowDeleteDialog(value: Boolean) {
        _showDeleteDialog.value = value
    }

    fun setTypeToDelete(type: Int) {
        _typeToDelete.value = type
    }

    fun setShowDeleteByTypeDialog(value: Boolean) {
        _showDeleteByTypeDialog.value = value
    }


    fun onLoadFromUri(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val inFile = File(
                        Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "MyDiary.json"
                    )
                    val sc = Scanner(appContext.contentResolver.openInputStream(uri))
                    val backupDataStr = sc.nextLine()
                    sc.close()
                    val backupData = Gson().fromJson(backupDataStr, BackupData::class.java)
                    Log.e("backup", backupData.toString())
                    deleteAllUseCase.execute()
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