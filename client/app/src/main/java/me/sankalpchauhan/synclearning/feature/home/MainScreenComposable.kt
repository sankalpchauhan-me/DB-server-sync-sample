package me.sankalpchauhan.synclearning.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import me.sankalpchauhan.synclearning.data.db.SyncStatus
import me.sankalpchauhan.synclearning.feature.note.CreateNoteScreen
import me.sankalpchauhan.synclearning.feature.note.MODE
import me.sankalpchauhan.synclearning.ui.theme.SyncLearningTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Serializable
object MainScreen

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainScreenViewModel
){
    viewModel.collectSideEffect { sideEffect ->
        when(sideEffect){
            MainScreenSideEffects.NavigateToCreateNote ->  navController.navigate(CreateNoteScreen())
            is MainScreenSideEffects.NavigateToSelectedNote -> navController.navigate(CreateNoteScreen(MODE.UPDATE, id = sideEffect.id))
        }
    }
    val pageState by viewModel.collectAsState()
    viewModel.handleEvent(MainScreenEvent.LoadData)
    SyncLearningTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)){
                LazyColumn {
                    items(count = pageState.notes.size, key = {it}){
                        NoteItem(id = pageState.notes[it].id!!, title = pageState.notes[it].title, syncStatus = pageState.notes[it].syncStatus, description = pageState.notes[it].description){id->
                            viewModel.handleEvent(MainScreenEvent.NavigateToSelectedNote(id))
                        }
                      //  Spacer(modifier = Modifier.height(2.dp).fillMaxWidth())
                    }
                }
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    onClick = {
                        viewModel.handleEvent(MainScreenEvent.NavigateToCreateNote)
                    }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun NoteItem(id: Int, title: String, description: String, syncStatus: SyncStatus, onClick: (Int)-> Unit){
    Card(modifier = Modifier.padding(2.dp)
        .fillMaxSize()
        .clickable {
            onClick.invoke(id)
        },
        shape = RectangleShape,
        border = BorderStroke(1.dp, Color.Gray),
        colors = CardDefaults.cardColors().copy(
            containerColor = getColorFromSyncStatus(syncStatus)
        )
    ) {
        Column(modifier = Modifier.padding(4.dp)){
            Text(text = title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = description, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun getColorFromSyncStatus(syncStatus: SyncStatus): Color{
    return when(syncStatus){
        SyncStatus.PENDING -> Color.Yellow.copy(alpha = 0.2f)
        SyncStatus.SUCCESS -> Color.Green.copy(alpha = 0.2f)
        SyncStatus.FAILED -> Color.Red.copy(alpha = 0.2f)
        SyncStatus.UNKNOWN -> Color.Transparent
    }
}


//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    MainScreen(rememberNavController(), MainScreenViewModel())
//}