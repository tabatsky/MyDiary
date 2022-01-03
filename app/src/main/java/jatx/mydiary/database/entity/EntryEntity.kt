package jatx.mydiary.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class EntryEntity(
    @PrimaryKey
    val id: Long? = null,
    val type: Int,
    val time: Long
)