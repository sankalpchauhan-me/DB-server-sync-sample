package me.sankalpchauhan.synclearning.data.network


import me.sankalpchauhan.synclearning.data.NoteApiResponse
import me.sankalpchauhan.synclearning.data.NoteRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface NotesService {
    @POST("/notes")
    suspend fun insertNote(@Body note: NoteRequest): NoteApiResponse
    @GET("/notes")
    suspend fun getAllNotes(): List<NoteApiResponse>
    @GET("/notes/{id}")
    suspend fun getNoteById(@Path("id") id: String): NoteApiResponse
    @PUT("/notes/{id}")
    suspend fun updateNote(@Path("id") id: String, @Body note: NoteRequest): NoteApiResponse
    @DELETE("/notes/{id}")
    suspend fun deleteNote(@Path("id") id: String): NoteApiResponse
}