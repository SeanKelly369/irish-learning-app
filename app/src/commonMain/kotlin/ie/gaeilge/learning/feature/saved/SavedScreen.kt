package ie.gaeilge.learning.feature.saved

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.gaeilge.learning.core.domain.VocabularyEntry
import ie.gaeilge.learning.core.domain.VocabularyRepository
import kotlinx.coroutines.launch

@Composable
fun SavedScreen(repository: VocabularyRepository) {
    val coroutineScope = rememberCoroutineScope()
    var savedWords by remember { mutableStateOf(emptyList<VocabularyEntry>()) }

    LaunchedEffect(repository) {
        repository.observeSavedWords().collect { savedWords = it }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Saved Expressions & Words", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text("Your custom search or reader logs, fully available offline.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
        }

        if (savedWords.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text("No saved expressions found. Long-press words in the Immersive Reader to populate lists here.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
                }
            }
        } else {
            items(savedWords) { word ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(word.irish, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                            Text(word.english, style = MaterialTheme.typography.bodyMedium)
                            SuggestionChip(onClick = {}, label = { Text(word.partOfSpeech.name) })
                        }
                        IconButton(onClick = {
                            coroutineScope.launch { repository.removeSavedWord(word.id) }
                        }) {
                            Text("✕", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
