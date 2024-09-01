package me.sankalpchauhan.synclearning.feature.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.sankalpchauhan.synclearning.data.NoteModel
import me.sankalpchauhan.synclearning.data.NotesRepository
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class CreateNoteScreenState(
    val mode: MODE = MODE.CREATE,
    val title: String = "",
    val description: String = "",
    val id: Int? = null
)

sealed class CreateNoteScreenEvent{
    data class InitializeScreen(val args: CreateNoteScreen): CreateNoteScreenEvent()
    data class SaveNote(val title: String, val description: String): CreateNoteScreenEvent()
    data object DeleteNote: CreateNoteScreenEvent()
}

@HiltViewModel
class CreateNoteScreenViewModel @Inject constructor(
    private val notesRepository: NotesRepository
): ContainerHost<CreateNoteScreenState, Nothing>, ViewModel() {
    override val container: Container<CreateNoteScreenState, Nothing> = container(
        CreateNoteScreenState()
    )

    fun handleEvent(event: CreateNoteScreenEvent){
        when(event){
            is CreateNoteScreenEvent.SaveNote -> saveNote(event.title, event.description)
            is CreateNoteScreenEvent.InitializeScreen -> initialize(event.args)
            CreateNoteScreenEvent.DeleteNote -> deleteNote()
        }
    }

    private fun deleteNote() = intent {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.deleteNote(id = state.id!!)
        }
    }

    private fun initialize(args: CreateNoteScreen) = intent {
        reduce {
            state.copy(mode = args.mode, id =  args.id)
        }
        if(args.mode == MODE.UPDATE){
            loadNote(args.id!!)
        }
    }

    private fun updateNote(id: Int, title: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.updateNote(NoteModel(id = id, title = title, description = description))
        }
    }

    private fun loadNote(id: Int) = intent {
        viewModelScope.launch(Dispatchers.IO) {
            val note = notesRepository.getNoteById(id)
            note?.let {
                reduce {
                    state.copy(title = note.title, description = note.description)
                }
            }
        }
    }

    private fun saveNote(title: String, description: String) = intent {
        when(state.mode){
            MODE.CREATE ->  createNote(title, description)
            MODE.UPDATE ->  updateNote(state.id!!, title, description)
        }
    }

    private fun createNote(title: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.insertNote(NoteModel(title = title, description = description))
        }
    }
}