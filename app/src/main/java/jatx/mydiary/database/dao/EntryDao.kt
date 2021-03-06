package jatx.mydiary.database.dao

import androidx.room.*
import jatx.mydiary.database.entity.EntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries ORDER BY time DESC")
    fun getAll(): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries ORDER BY time DESC")
    suspend fun getAllSuspend(): List<EntryEntity>

    @Insert
    fun insert(entryEntity: EntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplaceList(list: List<EntryEntity>)

    @Delete
    fun delete(entryEntity: EntryEntity)
}