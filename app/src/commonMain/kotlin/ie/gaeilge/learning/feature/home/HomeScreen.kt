package ie.gaeilge.learning.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.gaeilge.learning.core.domain.*

@Composable
fun HomeScreen(
    progressRepository: ProgressRepository,
    topicLessonRepository: TopicLessonRepository,
    vocabularyRepository: VocabularyRepository,
    onNavigateToLearn: () -> Unit
) {
    var progress by remember { mutableStateOf(UserProgress()) }
    var activeDialect by remember { mutableStateOf(Dialect.STANDARD) }
    var dueReviewsCount by remember { mutableStateOf(0) }

    LaunchedEffect(progressRepository) {
        progressRepository.observeProgress().collect { progress = it }
    }
    LaunchedEffect(progressRepository) {
        progressRepository.observeDialectPreference().collect { activeDialect = it }
    }
    LaunchedEffect(progressRepository) {
        progressRepository.observeReviewQueue().collect { dueReviewsCount = it.size }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("Dia Duit! Welcome back", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text("Gaeilge Learning", style = MaterialTheme.typography.displaySmall)
                Text("Active Dialect Tracking: ${activeDialect.name}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Today's Workload Overview", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Text("• $dueReviewsCount items scheduled in your Spaced-Repetition queue.", style = MaterialTheme.typography.bodyMedium)
                    Text("• ${progress.lessonsCompletedCount} complete modules logged.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Irish of the Day Phrase", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text("Ní neart go cur le chéile.", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                    Text("Literal: No strength until putting together.", style = MaterialTheme.typography.labelSmall)
                    Text("Natural translation: There is strength in unity.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Continue Learning Tracks", style = MaterialTheme.typography.titleMedium)
                    Text("Pick up right where you left off or choose fresh real everyday contexts.", style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = onNavigateToLearn, modifier = Modifier.fillMaxWidth()) {
                        Text("Open Learning Board")
                    }
                }
            }
        }
    }
}
