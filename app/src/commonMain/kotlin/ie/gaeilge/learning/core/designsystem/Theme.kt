package ie.gaeilge.learning.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Green = Color(0xFF176B4D)
private val LightColors = lightColorScheme(
    primary = Green,
    onPrimary = Color.White,
    secondary = Color(0xFFD9893D),
    background = Color(0xFFF8FBF7),
    surface = Color.White,
)

@Composable
fun GaeilgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content,
    )
}
