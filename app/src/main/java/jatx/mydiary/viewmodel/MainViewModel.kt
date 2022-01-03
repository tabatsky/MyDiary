package jatx.mydiary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jatx.mydiary.domain.models.Entry
import jatx.mydiary.domain.models.formatTimeList
import jatx.mydiary.domain.usecase.DeleteUseCase
import jatx.mydiary.domain.usecase.GetAllUseCase
import jatx.mydiary.domain.usecase.InsertUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val getAllUseCase: GetAllUseCase,
    val insertUseCase: InsertUseCase,
    val deleteUseCase: DeleteUseCase
): ViewModel() {
    private val _entries = MutableStateFlow(listOf<Entry>())
    val entries = _entries.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog = _showDeleteDialog.asStateFlow()

    private val _entryToDelete = MutableStateFlow<Entry?>(null)
    val entryToDelete = _entryToDelete.asStateFlow()

    private val _invalidateCounter = MutableStateFlow(0)
    val invalidateCounter = _invalidateCounter.asStateFlow()

    fun init() {
        getAllUseCase
            .execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _entries.value = it
                _invalidateCounter.value += 1
            }
    }

    fun insertEntry(entry: Entry) {
        insertUseCase.execute(entry)
    }

    fun deleteEntry() {
        Log.e("delete", entryToDelete.value?.formatTimeList() ?: "null")
        entryToDelete.value?.apply {
            deleteUseCase.execute(this)
        }
    }

    fun setEntryToDelete(entry: Entry?) {
        _entryToDelete.value = entry
    }

    fun setShowDeleteDialog(value: Boolean, ) {
        _showDeleteDialog.value = value
    }
}