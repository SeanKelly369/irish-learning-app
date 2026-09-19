package ie.gaeilge.learning.feature.practice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.gaeilge.learning.core.domain.ProgressRepository
import ie.gaeilge.learning.core.domain.ReviewState
import ie.gaeilge.learning.core.domain.VocabularyRepository
import ie.gaeilge.learning.core.domain.VocabularyEntry
import kotlinx.coroutines.launch

@Composable
fun PracticeScreen(
    progressRepository: ProgressRepository,
    vocabularyRepository: VocabularyRepository
) {
    val coroutineScope = rememberCoroutineScope()
    var queue by remember { mutableStateOf(emptyList<ReviewState>()) }
    var currentItemIndex by remember { mutableStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }
    var currentVocabularyEntry by remember { mutableStateOf<VocabularyEntry?>(null) }

    LaunchedEffect(progressRepository) {
        progressRepository.observeReviewQueue().collect { queue = it }
    }

    val currentReviewItem = queue.getOrNull(currentItemIndex)

    LaunchedEffect(currentReviewItem, vocabularyRepository) {
        if (currentReviewItem != null) {
            currentVocabularyEntry = vocabularyRepository.getWord(currentReviewItem.itemId)
        } else {
            currentVocabularyEntry = null
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Spaced Repetition Practice", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text("Useful saved material returned automatically based on difficulty rules.", style = MaterialTheme.typography.bodyMedium)

        if (currentReviewItem == null || currentVocabularyEntry == null) {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Review Queue Empty!", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text("All terms and phrases are successfully scheduled. Great work!", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                }
            }
        } else {
            val entry = currentVocabularyEntry!!
            Text("Item ${currentItemIndex + 1} of ${queue.size}", style = MaterialTheme.typography.labelLarge)

            Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Irish Term / Phrase", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text(entry.irish, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)

                    Spacer(Modifier.height(32.dp))

                    if (!showAnswer) {
                        Button(onClick = { showAnswer = true }) {
                            Text("Reveal Recall Meaning")
                        }
                    } else {
                        Text("English Recall Meaning", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                        Text(entry.english, style = MaterialTheme.typography.headlineMedium)

                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                onClick = {
                                    coroutineScope.launch {
                                        progressRepository.recordReview(entry.id, false)
                                        showAnswer = false
                                        if (currentItemIndex + 1 >= queue.size) currentItemIndex = 0 else currentItemIndex++
                                    }
                                }
                            ) {
                                Text("Forgot / Hard")
                            }

                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                onClick = {
                                    coroutineScope.launch {
                                        progressRepository.recordReview(entry.id, true)
                                        showAnswer = false
                                        if (currentItemIndex + 1 >= queue.size) currentItemIndex = 0 else currentItemIndex++
                                    }
                                }
                            ) {
                                Text("Easy / Recalled")
                            }
                        }
                    }
                }
            }
        }
    }
}
