package jatx.mydiary.domain.usecase

import jatx.mydiary.database.dao.EntryDao
import javax.inject.Inject

class DeleteAllUseCase @Inject constructor(
    private val entryDao: EntryDao
) {
    suspend fun execute() = entryDao
        .deleteAll()
}