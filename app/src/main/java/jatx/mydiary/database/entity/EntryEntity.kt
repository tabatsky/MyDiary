package jatx.mydiary.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import jatx.mydiary.domain.models.Entry

@Entity(tableName = "entries")
data class EntryEntity(
    @PrimaryKey
    val id: Long? = null,
    val type: Int,
    val time: Long
)

fun Entry.toEntryEntity() = EntryEntity(
    id = id,
    type = type,
    time = time
)

fun EntryEntity.toEntry() = Entry(
    id = id,
    type = type,
    time = time
)