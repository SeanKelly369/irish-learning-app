package ie.gaeilge.learning.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.gaeilge.learning.core.data.InMemoryLessonRepository
import ie.gaeilge.learning.core.designsystem.AppStrings
import ie.gaeilge.learning.core.domain.Lesson

@Composable
fun HomeScreen(
    onStartGame: () -> Unit = {},
    repository: InMemoryLessonRepository = remember { InMemoryLessonRepository() },
) {
    val lessons = remember(repository) { repository.getLessons() }
    val completed = lessons.count(Lesson::completed)
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(AppStrings.welcome, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(AppStrings.appName, style = MaterialTheme.typography.displaySmall)
        Text(AppStrings.appTagline, style = MaterialTheme.typography.bodyLarge)
        ProgressCard(completed = completed, total = lessons.size)
        lessons.firstOrNull { !it.completed }?.let { lesson ->
            LessonCard(lesson, onStartGame)
        }
        Spacer(Modifier.height(4.dp))
        OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text(AppStrings.viewLessons)
        }
    }
}

@Composable
private fun ProgressCard(completed: Int, total: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(AppStrings.todaysProgress, style = MaterialTheme.typography.titleMedium)
            Text("$completed/$total ${AppStrings.lessonsComplete}", style = MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator(
                progress = { if (total == 0) 0f else completed.toFloat() / total },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun LessonCard(lesson: Lesson, onStartGame: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(AppStrings.continueLearning, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(lesson.irishTitle, style = MaterialTheme.typography.headlineSmall)
            Text(lesson.title, style = MaterialTheme.typography.titleMedium)
            Text(lesson.description, style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${lesson.durationMinutes} ${AppStrings.minutes}", style = MaterialTheme.typography.labelMedium)
                Button(onClick = onStartGame) {
                    Text(AppStrings.playGame)
                }
            }
        }
    }
}
