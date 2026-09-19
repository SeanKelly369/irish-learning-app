package ie.gaeilge.learning.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ie.gaeilge.learning.app.navigation.AppDestination
import ie.gaeilge.learning.core.data.*
import ie.gaeilge.learning.core.designsystem.GaeilgeTheme
import ie.gaeilge.learning.core.domain.Dialect
import ie.gaeilge.learning.feature.home.HomeScreen
import ie.gaeilge.learning.feature.learn.LearnScreen
import ie.gaeilge.learning.feature.practice.PracticeScreen
import ie.gaeilge.learning.feature.progress.ProgressScreen
import ie.gaeilge.learning.feature.reader.ReaderScreen
import ie.gaeilge.learning.feature.saved.SavedScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()

    // Instantiate central repositories
    val vocabularyRepository = remember { VocabularyRepositoryImpl() }
    val articleRepository = remember { ArticleRepositoryImpl() }
    val progressRepository = remember { ProgressRepositoryImpl() }
    val topicLessonRepository = remember { TopicLessonRepositoryImpl() }

    var currentDestination by remember { mutableStateOf(AppDestination.Home) }
    var currentDialect by remember { mutableStateOf(Dialect.STANDARD) }
    var showDialectMenu by remember { mutableStateOf(false) }

    LaunchedEffect(progressRepository) {
        progressRepository.observeDialectPreference().collect { currentDialect = it }
    }

    GaeilgeTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gaeilge Learning MVP") },
                    actions = {
                        Box {
                            TextButton(onClick = { showDialectMenu = true }) {
                                Text("Dialect: ${currentDialect.name}")
                            }
                            DropdownMenu(expanded = showDialectMenu, onDismissRequest = { showDialectMenu = false }) {
                                Dialect.entries.forEach { dialect ->
                                    DropdownMenuItem(
                                        text = { Text(dialect.name) },
                                        onClick = {
                                            coroutineScope.launch {
                                                progressRepository.setDialectPreference(dialect)
                                            }
                                            showDialectMenu = false
                                        }
                                    )
                                }
                            }
                        }
                        IconButton(onClick = { currentDestination = AppDestination.Progress }) {
                            Text("📊")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = (currentDestination == AppDestination.Home),
                        onClick = { currentDestination = AppDestination.Home },
                        icon = { Text("🏠") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = (currentDestination == AppDestination.Learn),
                        onClick = { currentDestination = AppDestination.Learn },
                        icon = { Text("📚") },
                        label = { Text("Learn") }
                    )
                    NavigationBarItem(
                        selected = (currentDestination == AppDestination.Reader),
                        onClick = { currentDestination = AppDestination.Reader },
                        icon = { Text("📖") },
                        label = { Text("Reader") }
                    )
                    NavigationBarItem(
                        selected = (currentDestination == AppDestination.Practice),
                        onClick = { currentDestination = AppDestination.Practice },
                        icon = { Text("🎯") },
                        label = { Text("Practice") }
                    )
                    NavigationBarItem(
                        selected = (currentDestination == AppDestination.Saved),
                        onClick = { currentDestination = AppDestination.Saved },
                        icon = { Text("⭐") },
                        label = { Text("Saved") }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                when (currentDestination) {
                    AppDestination.Home -> HomeScreen(
                        progressRepository = progressRepository,
                        topicLessonRepository = topicLessonRepository,
                        vocabularyRepository = vocabularyRepository,
                        onNavigateToLearn = { currentDestination = AppDestination.Learn }
                    )
                    AppDestination.Learn -> LearnScreen(
                        repository = topicLessonRepository,
                        onLessonCompleted = { lessonId ->
                            coroutineScope.launch {
                                progressRepository.recordLessonCompletion(lessonId)
                            }
                        }
                    )
                    AppDestination.Reader -> ReaderScreen(
                        articleRepository = articleRepository,
                        vocabularyRepository = vocabularyRepository
                    )
                    AppDestination.Practice -> PracticeScreen(
                        progressRepository = progressRepository,
                        vocabularyRepository = vocabularyRepository
                    )
                    AppDestination.Saved -> SavedScreen(
                        repository = vocabularyRepository
                    )
                    AppDestination.Progress -> ProgressScreen(
                        repository = progressRepository
                    )
                }
            }
        }
    }
}
