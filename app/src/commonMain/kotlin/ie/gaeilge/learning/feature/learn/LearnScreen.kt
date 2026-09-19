package ie.gaeilge.learning.feature.learn

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.gaeilge.learning.core.domain.TopicLesson
import ie.gaeilge.learning.core.domain.TopicLessonRepository
import kotlinx.coroutines.flow.first

@Composable
fun LearnScreen(
    repository: TopicLessonRepository,
    onLessonCompleted: (String) -> Unit
) {
    var lessons by remember { mutableStateOf(emptyList<TopicLesson>()) }
    var activeLesson by remember { mutableStateOf<TopicLesson?>(null) }
    var step by remember { mutableStateOf(0) } // 0 = intro/explanation, 1 = recognition, 2 = sentence builder, 3 = finished

    LaunchedEffect(repository) {
        repository.observeLessons().collect { lessons = it }
    }

    if (activeLesson == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Available Lessons", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Text("Short, practical data-driven topics", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 12.dp))
            }
            items(lessons) { lesson ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        activeLesson = lesson
                        step = 0
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(lesson.irishTitle, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Badge { Text(lesson.level) }
                        }
                        Text(lesson.title, style = MaterialTheme.typography.titleLarge)
                        Text(lesson.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    } else {
        val lesson = activeLesson!!
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(lesson.title, style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = { activeLesson = null }) { Text("Exit") }
            }

            LinearProgressIndicator(
                progress = { (step + 1) / 4f },
                modifier = Modifier.fillMaxWidth()
            )

            when (step) {
                0 -> {
                    Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Visible Grammar & Overview", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text(lesson.explanationCard, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.weight(1f))
                            Button(onClick = { step = 1 }, modifier = Modifier.fillMaxWidth()) {
                                Text("Start Practice")
                            }
                        }
                    }
                }
                1 -> {
                    var selectedAnswer by remember { mutableStateOf("") }
                    val challenge = lesson.recognitionChallenges.first()
                    Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Recognition Practice", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text("What does this mean?", style = MaterialTheme.typography.bodyMedium)
                            Text(challenge.first, style = MaterialTheme.typography.headlineMedium)

                            Spacer(Modifier.height(20.dp))
                            listOf(challenge.second, "I feel sad.", "Where is the phone?").forEach { option ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable { selectedAnswer = option }.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = (selectedAnswer == option), onClick = { selectedAnswer = option })
                                    Text(option, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
                                }
                            }

                            Spacer(Modifier.weight(1f))
                            Button(
                                onClick = { if (selectedAnswer == challenge.second) step = 2 },
                                enabled = selectedAnswer.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Verify & Continue")
                            }
                        }
                    }
                }
                2 -> {
                    val challenge = lesson.sentenceBuildingChallenges.first()
                    var currentSelection by remember { mutableStateOf(emptyList<String>()) }
                    Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Sentence-Building Practice", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text("Arrange tokens into natural order:", style = MaterialTheme.typography.bodyMedium)

                            Text(
                                text = currentSelection.joinToString(" ").ifEmpty { "[Tap tokens below]" },
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                challenge.first.split(" ").forEach { token ->
                                    if (token !in currentSelection) {
                                        SuggestionChip(
                                            onClick = { currentSelection = currentSelection + token },
                                            label = { Text(token) }
                                        )
                                    }
                                }
                            }

                            if (currentSelection.isNotEmpty()) {
                                TextButton(onClick = { currentSelection = emptyList() }) { Text("Clear Selection") }
                            }

                            Spacer(Modifier.weight(1f))
                            Button(
                                onClick = {
                                    if (currentSelection == challenge.second) {
                                        onLessonCompleted(lesson.id)
                                        step = 3
                                    }
                                },
                                enabled = currentSelection.size == challenge.second.size,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Check Answer")
                            }
                        }
                    }
                }
                3 -> {
                    Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Comhghairdeas!", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                            Text("Lesson successfully completed!", style = MaterialTheme.typography.titleLarge)
                            Text("Comprehension and vocabulary metrics recorded.", style = MaterialTheme.typography.bodyMedium)

                            Spacer(Modifier.height(30.dp))
                            Button(onClick = { activeLesson = null }) {
                                Text("Return to Tracks")
                            }
                        }
                    }
                }
            }
        }
    }
}
