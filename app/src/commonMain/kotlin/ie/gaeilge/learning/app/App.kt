package ie.gaeilge.learning.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ie.gaeilge.learning.core.designsystem.GaeilgeTheme
import ie.gaeilge.learning.feature.home.HomeScreen

@Composable
fun App() {
    GaeilgeTheme {
        MaterialTheme {
            HomeScreen()
        }
    }
}
