package me.sankalpchauhan.synclearning.feature.note

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import me.sankalpchauhan.synclearning.ui.theme.SyncLearningTheme
import org.orbitmvi.orbit.compose.collectAsState

enum class MODE{
    CREATE,
    UPDATE
}

@Serializable
data class CreateNoteScreen(
    val mode: MODE = MODE.CREATE,
    val id: Int? = null
)

@Composable
fun CreateNoteScreen(
    navController: NavController,
    viewModel: CreateNoteScreenViewModel,
    navBackStackEntry: NavBackStackEntry
){
    var titleTextState by remember { mutableStateOf("") }
    var titleTextError by remember { mutableStateOf(false) }
    var contentTextState by remember { mutableStateOf("") }
    var contentTextError by remember { mutableStateOf(false) }
    val state by viewModel.collectAsState()
    titleTextState = state.title
    contentTextState = state.description
    val args = navBackStackEntry.toRoute<CreateNoteScreen>()
    viewModel.handleEvent(CreateNoteScreenEvent.InitializeScreen(args))

    val keyboardController = LocalSoftwareKeyboardController.current
    SyncLearningTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)) {
                LaunchedEffect(Unit) {
                }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = {
                        Text(text = "Title")
                    },
                    isError = titleTextError,
                    maxLines = 1,
                    value = titleTextState, onValueChange = {
                        titleTextError = false
                        titleTextState = it
                })
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .defaultMinSize(minHeight = 200.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Default
                    ),
                    isError = contentTextError,
                    label = {
                        Text(text = "Description")
                    },
                    maxLines = Int.MAX_VALUE,
                    value = contentTextState, onValueChange = {
                        contentTextError = false
                        contentTextState = it
                    })

                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if(titleTextState.isNotEmpty() && contentTextState.isNotEmpty()){
                                viewModel.handleEvent(
                                    CreateNoteScreenEvent.SaveNote(
                                        titleTextState,
                                        contentTextState
                                    )
                                )
                                keyboardController?.hide()
                                navController.popBackStack()
                            } else{
                                if(titleTextState.isEmpty()){
                                    titleTextError = true
                                }

                                if(contentTextState.isEmpty()){
                                    contentTextError = true
                                }

                            }
                        }) {
                        Text(text = if(state.mode==MODE.CREATE)"Save Note" else "Update Note")
                    }
                    if(state.mode==MODE.UPDATE){
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            onClick = {
                                viewModel.handleEvent(CreateNoteScreenEvent.DeleteNote)
                                navController.popBackStack()
                            }) {
                            Text(text = "Delete Note")
                        }
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun CreateNoteScreenPreview() {
//    CreateNoteScreen(rememberNavController(), CreateNoteScreenViewModel())
//}