package ie.gaeilge.learning.feature.vocabulary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.gaeilge.learning.core.domain.VocabularyEntry
import ie.gaeilge.learning.core.domain.VocabularyRepository

@Composable
fun VocabularyScreen(repository: VocabularyRepository) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<VocabularyEntry>()) }
    var selectedWord by remember { mutableStateOf<VocabularyEntry?>(null) }

    LaunchedEffect(query, repository) {
        results = repository.search(query)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Foclóir / Vocabulary", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Irish or English (e.g. eireann, eagla)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(results) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedWord = entry }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(entry.irish, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                            Text(entry.english, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            selectedWord?.let { word ->
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            Text(word.irish, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                            Text(word.english, style = MaterialTheme.typography.titleMedium)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                        item {
                            Text("Part of Speech: ${word.partOfSpeech.name}", style = MaterialTheme.typography.bodyMedium)
                            word.gender?.let { Text("Gender: ${it.name}", style = MaterialTheme.typography.bodySmall) }
                            word.genitive?.let { Text("Genitive Form: $it", style = MaterialTheme.typography.bodySmall) }
                            word.plural?.let { Text("Plural Form: $it", style = MaterialTheme.typography.bodySmall) }
                        }
                        if (word.examples.isNotEmpty()) {
                            item {
                                Text("Example Phrases:", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                            }
                            items(word.examples) { ex ->
                                Column(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text(ex.irish, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                                    Text("Lit: ${ex.literalEnglish}", style = MaterialTheme.typography.labelSmall)
                                    Text("Nat: ${ex.naturalEnglish}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
