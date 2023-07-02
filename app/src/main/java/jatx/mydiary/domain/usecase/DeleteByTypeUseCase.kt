package jatx.mydiary.domain.usecase

import jatx.mydiary.database.dao.EntryDao
import javax.inject.Inject

class DeleteByTypeUseCase @Inject constructor(
    private val entryDao: EntryDao
) {
    suspend fun execute(type: Int) = entryDao
        .deleteByType(type)
}