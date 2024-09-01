package me.sankalpchauhan.synclearning.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.sankalpchauhan.synclearning.data.NotesRepository
import me.sankalpchauhan.synclearning.data.db.SyncStatus
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class NoteData(val id: Int? = null, val title: String, val description: String, val syncStatus: SyncStatus = SyncStatus.UNKNOWN)

data class MainScreenState(
    val notes: List<NoteData> = emptyList()
)

sealed class MainScreenEvent{
    data object NavigateToCreateNote: MainScreenEvent()
    data object LoadData: MainScreenEvent()
    data class NavigateToSelectedNote(val id: Int): MainScreenEvent()
}

sealed class MainScreenSideEffects{
    data object NavigateToCreateNote: MainScreenSideEffects()
    data class NavigateToSelectedNote(val id: Int): MainScreenSideEffects()
}

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val notesRepository: NotesRepository
): ContainerHost<MainScreenState, MainScreenSideEffects>, ViewModel() {
    override val container = container<MainScreenState, MainScreenSideEffects>(MainScreenState())

    fun handleEvent(event: MainScreenEvent){
        when(event){
            MainScreenEvent.NavigateToCreateNote -> navigateToCreateNote()
            MainScreenEvent.LoadData -> getAllNotes()
            is MainScreenEvent.NavigateToSelectedNote -> navigateToSelectedNote(event.id)
        }
    }

    private fun navigateToSelectedNote(id: Int) = intent {
        postSideEffect(MainScreenSideEffects.NavigateToSelectedNote(id))
    }

    private fun navigateToCreateNote() = intent {
        postSideEffect(MainScreenSideEffects.NavigateToCreateNote)
    }

    private fun getAllNotes() = intent{
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.getAllNotes().collect{ entities->
                val noteUiList = entities.map { NoteData(it.id,it.title, it.description, syncStatus = it.syncStatus) }
                reduce {
                    state.copy(notes = noteUiList)
                }
            }
        }

    }

}