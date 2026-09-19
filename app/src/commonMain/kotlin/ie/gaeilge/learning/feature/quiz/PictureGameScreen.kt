package ie.gaeilge.learning.feature.quiz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ie.gaeilge.learning.core.data.InMemoryPictureGameRepository
import ie.gaeilge.learning.core.designsystem.AppStrings
import ie.gaeilge.learning.core.domain.Picture
import ie.gaeilge.learning.core.domain.PictureChallenge

@Composable
fun PictureGameScreen(
    onExit: () -> Unit,
    repository: InMemoryPictureGameRepository = remember { InMemoryPictureGameRepository() },
) {
    val challenges = remember(repository) { repository.getChallenges() }
    var round by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var streak by remember { mutableStateOf(0) }
    var selected by remember { mutableStateOf<Picture?>(null) }
    var answerShown by remember { mutableStateOf(false) }

    if (round >= challenges.size) {
        GameComplete(score = score, onPlayAgain = {
            round = 0
            score = 0
            streak = 0
            selected = null
            answerShown = false
        }, onExit = onExit)
        return
    }

    val challenge = challenges[round]
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = onExit) { Text("×  ${AppStrings.viewLessons}") }
            Text("${round + 1}/${challenges.size}  •  $streak 🔥", style = MaterialTheme.typography.labelLarge)
        }
        Text(AppStrings.picturePrompt, style = MaterialTheme.typography.titleMedium)
        Text(challenge.word, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Text(challenge.translation, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            challenge.options.forEach { picture ->
                PictureCard(
                    picture = picture,
                    selected = selected == picture,
                    correct = answerShown && picture == challenge.picture,
                    incorrect = answerShown && selected == picture && picture != challenge.picture,
                    onClick = {
                        if (!answerShown) {
                            selected = picture
                            answerShown = true
                            if (picture == challenge.picture) {
                                score += 10 + streak * 2
                                streak += 1
                            } else {
                                streak = 0
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        if (answerShown) {
            Text(
                if (selected == challenge.picture) AppStrings.correct else AppStrings.tryAgain,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected == challenge.picture) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
            Button(
                onClick = {
                    round += 1
                    selected = null
                    answerShown = false
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (round == challenges.lastIndex) AppStrings.finish else AppStrings.next)
            }
        } else {
            Text("Choose carefully — you can do it!", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun PictureCard(
    picture: Picture,
    selected: Boolean,
    correct: Boolean,
    incorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = when {
        correct -> Color(0xFFDDF4E8)
        incorrect -> Color(0xFFFFE2E0)
        selected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Card(
        modifier = modifier
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .semantics { contentDescription = picture.name },
        colors = CardDefaults.cardColors(containerColor = background),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            PictureIllustration(picture)
        }
    }
}

@Composable
private fun PictureIllustration(picture: Picture) {
    Canvas(modifier = Modifier.size(92.dp)) {
        when (picture) {
            Picture.SUN -> drawSun()
            Picture.APPLE -> drawApple()
            Picture.HOUSE -> drawHouse()
            Picture.TREE -> drawTree()
            Picture.CAT -> drawCat()
            Picture.BOOK -> drawBook()
            Picture.DOG -> drawDog()
            Picture.FISH -> drawFish()
        }
    }
}

private fun DrawScope.drawSun() {
    drawCircle(Color(0xFFFFC857), radius = size.minDimension * .28f, center = center)
    repeat(8) { index ->
        val angle = index * 0.785f
        val start = center + androidx.compose.ui.geometry.Offset(kotlin.math.cos(angle) * 28f, kotlin.math.sin(angle) * 28f)
        val end = center + androidx.compose.ui.geometry.Offset(kotlin.math.cos(angle) * 40f, kotlin.math.sin(angle) * 40f)
        drawLine(Color(0xFFE98A15), start, end, strokeWidth = 5f)
    }
}

private fun DrawScope.drawApple() {
    drawCircle(Color(0xFFE95D5D), radius = 28f, center = center.copy(x = center.x - 8))
    drawCircle(Color(0xFFE95D5D), radius = 28f, center = center.copy(x = center.x + 10))
    drawLine(Color(0xFF654321), center.copy(y = center.y - 25), center.copy(y = center.y - 43), strokeWidth = 6f)
    drawOval(
        Color(0xFF4FAF68),
        topLeft = Offset(center.x + 5, center.y - 44),
        size = Size(23f, 15f),
    )
}

private fun DrawScope.drawHouse() {
    val roof = Path().apply {
        moveTo(10f, 44f); lineTo(size.width / 2, 8f); lineTo(size.width - 10f, 44f); close()
    }
    drawPath(roof, Color(0xFFE98962), style = Fill)
    drawRect(
        Color(0xFFF4C98B),
        topLeft = Offset(18f, 40f),
        size = Size(size.width - 36f, size.height - 48f),
    )
    drawRect(
        Color(0xFF5A7999),
        topLeft = Offset(size.width / 2 - 10, size.height - 38),
        size = Size(20f, 30f),
    )
}

private fun DrawScope.drawTree() {
    drawRect(
        Color(0xFF7C5738),
        topLeft = Offset(center.x - 9, center.y + 5),
        size = Size(18f, size.height - center.y - 5),
    )
    drawCircle(Color(0xFF57A66C), 30f, center.copy(y = center.y - 8))
    drawCircle(Color(0xFF78BE70), 22f, center.copy(x = center.x - 18, y = center.y + 3))
}

private fun DrawScope.drawCat() {
    drawCircle(Color(0xFF9C7B68), 30f, center)
    drawPath(Path().apply { moveTo(20f, 28f); lineTo(28f, 2f); lineTo(42f, 25f); close() }, Color(0xFF9C7B68))
    drawPath(Path().apply { moveTo(50f, 25f); lineTo(66f, 2f); lineTo(72f, 30f); close() }, Color(0xFF9C7B68))
    drawCircle(Color.White, 4f, center.copy(x = center.x - 10, y = center.y - 4))
    drawCircle(Color.White, 4f, center.copy(x = center.x + 10, y = center.y - 4))
}

private fun DrawScope.drawBook() {
    drawRoundRect(
        Color(0xFF5579A8),
        topLeft = Offset(12f, 18f),
        size = Size(size.width / 2 - 12f, size.height - 30f),
        cornerRadius = CornerRadius(8f),
    )
    drawRoundRect(
        Color(0xFFDF806C),
        topLeft = Offset(size.width / 2, 18f),
        size = Size(size.width / 2 - 12f, size.height - 30f),
        cornerRadius = CornerRadius(8f),
    )
    drawLine(Color.White, center.copy(x = center.x, y = 18f), center.copy(x = center.x, y = size.height - 12f), 3f)
}

private fun DrawScope.drawDog() {
    drawCircle(Color(0xFFB47A4D), 29f, center)
    drawCircle(Color(0xFF8B5A3C), 12f, center.copy(x = center.x - 28, y = center.y - 20))
    drawCircle(Color(0xFF8B5A3C), 12f, center.copy(x = center.x + 28, y = center.y - 20))
    drawCircle(Color.White, 4f, center.copy(x = center.x - 10, y = center.y - 4))
    drawCircle(Color.White, 4f, center.copy(x = center.x + 10, y = center.y - 4))
    drawCircle(Color(0xFF3F3028), 5f, center.copy(y = center.y + 12))
}

private fun DrawScope.drawFish() {
    drawOval(
        Color(0xFF5A9CC8),
        topLeft = Offset(18f, center.y - 22),
        size = Size(52f, 44f),
    )
    drawPath(
        Path().apply {
            moveTo(18f, center.y)
            lineTo(0f, center.y - 20)
            lineTo(0f, center.y + 20)
            close()
        },
        Color(0xFF3E7FAE),
    )
    drawCircle(Color.White, 6f, center.copy(x = center.x + 20, y = center.y - 8))
    drawCircle(Color(0xFF2D3D4A), 3f, center.copy(x = center.x + 21, y = center.y - 8))
}

@Composable
private fun GameComplete(score: Int, onPlayAgain: () -> Unit, onExit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("🎉", style = MaterialTheme.typography.displayLarge)
        Text(AppStrings.gameComplete, style = MaterialTheme.typography.headlineMedium)
        Text("$score ${AppStrings.pointsEarned}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onPlayAgain, modifier = Modifier.fillMaxWidth()) { Text(AppStrings.playAgain) }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onExit, modifier = Modifier.fillMaxWidth()) { Text(AppStrings.viewLessons) }
    }
}
