package me.sankalpchauhan.synclearning.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.sankalpchauhan.synclearning.data.db.SyncStatus

@Serializable
data class NoteModel(val id: Int? = null, val title: String, val description: String, val syncStatus: SyncStatus = SyncStatus.PENDING, val uuid: String? = null ){
    fun toNoteRequest(): NoteRequest{
        return NoteRequest(id, title, description)
    }
}

@Serializable
data class NoteRequest(@SerialName("uid") val id: Int? = null, val title: String, val description: String){
    fun fromNoteModel(noteModel: NoteModel): NoteRequest{
        return NoteRequest(noteModel.id, noteModel.title, noteModel.description)
    }
}

@Serializable
data class NoteApiResponse(
    @SerialName("uuid")
    val serverId: String,
    @SerialName("uid")
    val clientId: Int,
    val title: String,
    val description: String,
    val version: Int,
    val lastModified: Long
){
    fun toNoteModel(): NoteModel{
        return NoteModel(clientId, title, description, syncStatus = SyncStatus.SUCCESS, uuid = serverId)
    }
}