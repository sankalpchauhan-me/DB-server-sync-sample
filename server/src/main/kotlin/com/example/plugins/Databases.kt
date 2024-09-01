package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    )
    val noteService = NoteService(database)
    routing {
        post("/notes") {
            val deviceId = call.request.headers["deviceId"] ?: error("Missing deviceId header")
            val user = call.receive<ExposedNote>().copy(deviceId = deviceId)
            val existingNote = user.uuid?.let {
                noteService.read(it)
            }?: kotlin.run {
                user.uid?.let {
                    noteService.read(user.deviceId, user.uid)
                }
            }
            val id = if(existingNote?.uuid == null) {
                noteService.create(user)
            } else {
                noteService.update(existingNote.uuid, user)
            }
            call.respond(HttpStatusCode.Created, id)
        }

        put("/notes/batch") {
            val notes = call.receive<List<ExposedNote>>()
            noteService.createBatch(notes)
            call.respond(HttpStatusCode.OK)
        }

        get("/notes") {
            val id = noteService.readAll()
            call.respond(HttpStatusCode.OK, id)
        }
        
        // Read user
        get("/notes/{uuid}") {
            val id = call.parameters["uuid"] ?: throw IllegalArgumentException("Invalid ID")
            val user = noteService.read(id)
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // Update user
        put("/notes/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("Invalid ID")
            val user = call.receive<ExposedNote>()
            val deletedNote = noteService.update(id, user)
            call.respond(HttpStatusCode.OK, deletedNote)
        }
        
        // Delete user
        delete("/notes/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("Invalid ID")
            val deletedId = noteService.delete(id)
            call.respond(HttpStatusCode.OK, deletedId)
        }
    }
}
