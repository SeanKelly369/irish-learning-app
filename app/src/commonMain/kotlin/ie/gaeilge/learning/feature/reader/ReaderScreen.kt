package ie.gaeilge.learning.feature.reader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.gaeilge.learning.core.domain.*
import kotlinx.coroutines.launch

@Composable
fun ReaderScreen(
    articleRepository: ArticleRepository,
    vocabularyRepository: VocabularyRepository
) {
    val coroutineScope = rememberCoroutineScope()
    var articles by remember { mutableStateOf(emptyList<ArticleSummary>()) }
    var activeArticle by remember { mutableStateOf<Article?>(null) }
    var assistanceLevel by remember { mutableStateOf(AssistanceLevel.GUIDED) }
    var selectedWordId by remember { mutableStateOf<String?>(null) }
    var selectedWordEntry by remember { mutableStateOf<VocabularyEntry?>(null) }
    var savedIds by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(articleRepository) {
        articleRepository.observeArticles().collect { articles = it }
    }

    LaunchedEffect(vocabularyRepository) {
        vocabularyRepository.observeSavedWords().collect { words ->
            savedIds = words.map { it.id }.toSet()
        }
    }

    LaunchedEffect(selectedWordId) {
        if (selectedWordId != null) {
            selectedWordEntry = vocabularyRepository.getWord(selectedWordId!!)
        } else {
            selectedWordEntry = null
        }
    }

    if (activeArticle == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Immersive Reader", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Text("Real adapted material with on-demand support", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(articles) { baseline ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        coroutineScope.launch {
                            activeArticle = articleRepository.getArticle(baseline.id)
                        }
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(baseline.irishTitle, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Text(baseline.title, style = MaterialTheme.typography.titleMedium)
                        Text(baseline.description, style = MaterialTheme.typography.bodyMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                            SuggestionChip(onClick = {}, label = { Text("${baseline.estimatedReadTimeMinutes} min read") })
                            SuggestionChip(onClick = {}, label = { Text(baseline.difficulty.name) })
                        }
                    }
                }
            }
        }
    } else {
        val article = activeArticle!!
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { activeArticle = null }) { Text("← Back to Feed") }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        AssistanceLevel.entries.forEach { level ->
                            FilterChip(
                                selected = (assistanceLevel == level),
                                onClick = { assistanceLevel = level },
                                label = { Text(level.name.replace("_", " ")) }
                            )
                        }
                    }
                }
            }

            item {
                Text(article.irishTitle, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Text(article.title, style = MaterialTheme.typography.titleLarge)
            }

            items(article.paragraphs) { paragraph ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // split words to allow tapping individual vocabulary items
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                            paragraph.split(" ").forEach { fullWord ->
                                val cleaned = fullWord.lowercase().replace(".", "").replace(",", "").replace("?", "")
                                val matchingVocabId = article.vocabularyIds.firstOrNull { it == cleaned }
                                Text(
                                    text = fullWord,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (matchingVocabId != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.clickable {
                                        if (matchingVocabId != null) {
                                            selectedWordId = matchingVocabId
                                        }
                                    }
                                )
                            }
                        }

                        if (assistanceLevel == AssistanceLevel.FULL_SUPPORT) {
                            Text(
                                text = article.sentenceTranslations[paragraph] ?: "Translation unavailable.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            article.literalTranslations[paragraph]?.let { literal ->
                                Text("Literal: $literal", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            if (selectedWordEntry != null) {
                item {
                    val entry = selectedWordEntry!!
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(entry.irish, style = MaterialTheme.typography.headlineSmall)
                                Button(
                                    onClick = {
                                        // launch directly using a mutable state check or callback triggering save
                                        savedIds = savedIds + entry.id
                                    }
                                ) {
                                    Text(if (entry.id in savedIds) "Saved" else "Save Word")
                                }
                            }
                            Text("Meaning: ${entry.english}", style = MaterialTheme.typography.bodyLarge)
                            Text("Part of Speech: ${entry.partOfSpeech.name}", style = MaterialTheme.typography.labelMedium)
                            if (entry.gender != null) {
                                Text("Gender: ${entry.gender.name}", style = MaterialTheme.typography.labelSmall)
                            }
                            TextButton(onClick = { selectedWordId = null }) { Text("Dismiss Overlay") }
                        }
                    }
                }
            }

            if (article.grammarNotes.isNotEmpty() && assistanceLevel != AssistanceLevel.IMMERSION) {
                item {
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Grammar Insights", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            article.grammarNotes.forEach { note ->
                                Text("• $note", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
