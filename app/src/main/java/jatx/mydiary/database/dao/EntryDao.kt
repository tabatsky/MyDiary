package jatx.mydiary.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Flowable
import jatx.mydiary.database.entity.EntryEntity

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries ORDER BY time DESC")
    fun getAll(): Flowable<List<EntryEntity>>

    @Insert
    fun insert(entryEntity: EntryEntity)

    @Delete
    fun delete(entryEntity: EntryEntity)
}