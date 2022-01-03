package jatx.mydiary.domain.usecase

import jatx.mydiary.database.dao.EntryDao
import jatx.mydiary.domain.models.Entry
import jatx.mydiary.domain.models.toEntryEntity
import javax.inject.Inject

class InsertUseCase @Inject constructor(
    private val entryDao: EntryDao
) {
    fun execute(entry: Entry) = entryDao
        .insert(entry.toEntryEntity())
}