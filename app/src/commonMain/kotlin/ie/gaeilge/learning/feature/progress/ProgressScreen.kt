package ie.gaeilge.learning.feature.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.gaeilge.learning.core.domain.ProgressRepository
import ie.gaeilge.learning.core.domain.UserProgress

@Composable
fun ProgressScreen(repository: ProgressRepository) {
    var progress by remember { mutableStateOf(UserProgress()) }

    LaunchedEffect(repository) {
        repository.observeProgress().collect { progress = it }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Your Genuine Comprehension metrics", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text("We focus on active recall milestones rather than arbitrary timing streaks.", style = MaterialTheme.typography.bodyMedium)

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            item { ProgressMetricCard("Words Recognized", progress.wordsRecognizedCount.toString()) }
            item { ProgressMetricCard("Words Recalled", progress.wordsActivelyRecalledCount.toString()) }
            item { ProgressMetricCard("Lessons Finished", progress.lessonsCompletedCount.toString()) }
            item { ProgressMetricCard("Articles Read", progress.articlesCompletedCount.toString()) }
            item { ProgressMetricCard("Reviews Performed", progress.reviewsCompletedCount.toString()) }
            item { ProgressMetricCard("Grammar Topics", progress.grammarTopicsEncounteredCount.toString()) }
        }
    }
}

@Composable
private fun ProgressMetricCard(label: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}
