package jatx.mydiary.domain.usecase

import jatx.mydiary.database.dao.EntryDao
import jatx.mydiary.database.entity.toEntryEntity
import jatx.mydiary.domain.models.Entry
import javax.inject.Inject

class DeleteUseCase @Inject constructor(
    private val entryDao: EntryDao
) {
    fun execute(entry: Entry) = entryDao
        .delete(entry.toEntryEntity())
}