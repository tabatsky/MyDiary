package jatx.mydiary.domain.usecase

import jatx.mydiary.database.dao.EntryDao
import jatx.mydiary.database.entity.toEntry
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllUseCase @Inject constructor(
    private val entryDao: EntryDao
) {
    fun execute() = entryDao
        .getAll()
        .map { list ->
            list.map { it.toEntry() }
        }
}