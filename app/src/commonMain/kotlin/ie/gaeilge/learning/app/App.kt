package ie.gaeilge.learning.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ie.gaeilge.learning.core.designsystem.GaeilgeTheme
import ie.gaeilge.learning.feature.home.HomeScreen
import ie.gaeilge.learning.feature.quiz.PictureGameScreen

@Composable
fun App() {
    GaeilgeTheme {
        MaterialTheme {
            var showGame by remember { mutableStateOf(false) }
            if (showGame) {
                PictureGameScreen(onExit = { showGame = false })
            } else {
                HomeScreen(onStartGame = { showGame = true })
            }
        }
    }
}
