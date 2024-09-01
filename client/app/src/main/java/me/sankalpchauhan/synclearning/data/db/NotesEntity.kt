package me.sankalpchauhan.synclearning.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.sankalpchauhan.synclearning.data.NoteModel

enum class SyncStatus{
    PENDING,
    SUCCESS,
    FAILED,
    UNKNOWN
}

@Entity(tableName = "notes")
data class NotesEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo val title: String,
    @ColumnInfo val description: String,
    @ColumnInfo val syncStatus: SyncStatus = SyncStatus.PENDING,
    @ColumnInfo val lastModified: Long = System.currentTimeMillis(),
    @ColumnInfo val version: Int = 1,
    @ColumnInfo val isDeleted: Boolean = false,
    @ColumnInfo val serverId: String? = null
){
    fun toNoteModel(): NoteModel {
        return NoteModel(
            id = uid,
            title = title,
            description = description,
            syncStatus = syncStatus,
            uuid = serverId
        )
    }
}