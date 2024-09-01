package me.sankalpchauhan.synclearning

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Observer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import me.sankalpchauhan.synclearning.feature.note.CreateNoteScreen
import me.sankalpchauhan.synclearning.feature.note.CreateNoteScreenViewModel
import me.sankalpchauhan.synclearning.feature.home.MainScreen
import me.sankalpchauhan.synclearning.feature.home.MainScreenViewModel

@AndroidEntryPoint
class NavigationControllerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = MainScreen,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(700)) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(700)) },
                popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(700)) },
                popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(700)) }
            ){
                composable<MainScreen> {
                    val viewModel = hiltViewModel<MainScreenViewModel>()
                    MainScreen(navController, viewModel)
                }
                composable<CreateNoteScreen> {
                    val viewModel = hiltViewModel<CreateNoteScreenViewModel>()
                    CreateNoteScreen(navController, viewModel, it)
                }
            }
        }
    }
}
