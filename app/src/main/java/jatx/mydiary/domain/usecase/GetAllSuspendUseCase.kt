package jatx.mydiary.domain.usecase

import jatx.mydiary.database.dao.EntryDao
import jatx.mydiary.database.entity.toEntry
import javax.inject.Inject

class GetAllSuspendUseCase @Inject constructor(
    private val entryDao: EntryDao
) {
    suspend fun execute() = entryDao
        .getAllSuspend()
        .map { it.toEntry() }
}