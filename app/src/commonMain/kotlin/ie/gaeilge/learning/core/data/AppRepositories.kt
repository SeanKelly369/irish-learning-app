package ie.gaeilge.learning.core.data

import ie.gaeilge.learning.core.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class VocabularyRepositoryImpl : VocabularyRepository {
    private val allVocabulary = MutableStateFlow(sampleVocabulary)
    private val savedWordIds = MutableStateFlow(setOf("eagla", "athas"))

    override suspend fun getWord(id: String): VocabularyEntry? {
        return allVocabulary.value.firstOrNull { it.id == id }
    }

    override suspend fun search(query: String): List<VocabularyEntry> {
        if (query.isBlank()) return allVocabulary.value
        val normalizedQuery = query.lowercase().normalizeFadas()
        return allVocabulary.value.filter {
            it.irish.lowercase().normalizeFadas().contains(normalizedQuery) ||
                    it.english.lowercase().contains(normalizedQuery)
        }
    }

    override suspend fun saveWord(id: String) {
        savedWordIds.value = savedWordIds.value + id
    }

    override suspend fun removeSavedWord(id: String) {
        savedWordIds.value = savedWordIds.value - id
    }

    override fun observeSavedWords(): Flow<List<VocabularyEntry>> {
        return savedWordIds.map { ids ->
            allVocabulary.value.filter { it.id in ids }
        }
    }

    override fun observeAllVocabulary(): Flow<List<VocabularyEntry>> {
        return allVocabulary
    }

    private fun String.normalizeFadas(): String {
        return this.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
            .replace("Á", "a").replace("É", "e").replace("Í", "i").replace("Ó", "o").replace("Ú", "u")
    }

    companion object {
        val sampleVocabulary = listOf(
            VocabularyEntry(
                id = "eagla",
                irish = "eagla",
                english = "fear",
                partOfSpeech = PartOfSpeech.NOUN,
                gender = GrammaticalGender.FEMININE,
                genitive = "eagla",
                plural = "eaglaí",
                pronunciation = PronunciationInfo("ag-lah"),
                examples = listOf(
                    ExampleSentence("Tá eagla orm.", "Fear is on me.", "I am afraid.", "Feelings use noun + ar preposition"),
                    ExampleSentence("Ná bíodh eagla ort.", "Let there not be fear on you.", "Don't be afraid.")
                ),
                dialectVariants = listOf(
                    DialectVariant(Dialect.ULSTER, "eagla", "Standard spelling, standard pronunciation used locally.")
                ),
                tags = setOf("emotions", "basics"),
                difficulty = DifficultyLevel.BEGINNER
            ),
            VocabularyEntry(
                id = "athas",
                irish = "áthas",
                english = "happiness / joy",
                partOfSpeech = PartOfSpeech.NOUN,
                gender = GrammaticalGender.MASCULINE,
                genitive = "áthais",
                pronunciation = PronunciationInfo("aw-hahas"),
                examples = listOf(
                    ExampleSentence("Tá áthas orm.", "Joy is on me.", "I am happy.")
                ),
                tags = setOf("emotions"),
                difficulty = DifficultyLevel.BEGINNER
            ),
            VocabularyEntry(
                id = "fearg",
                irish = "fearg",
                english = "anger",
                partOfSpeech = PartOfSpeech.NOUN,
                gender = GrammaticalGender.FEMININE,
                genitive = "feirge",
                pronunciation = PronunciationInfo("far-ug"),
                examples = listOf(
                    ExampleSentence("Tá fearg ar Sheán.", "Anger is on Sean.", "Sean is angry.")
                ),
                tags = setOf("emotions"),
                difficulty = DifficultyLevel.INTERMEDIATE_BEGINNER
            ),
            VocabularyEntry(
                id = "fón",
                irish = "fón",
                english = "phone",
                partOfSpeech = PartOfSpeech.NOUN,
                gender = GrammaticalGender.MASCULINE,
                genitive = "fóin",
                plural = "fóin",
                pronunciation = PronunciationInfo("fohn"),
                examples = listOf(
                    ExampleSentence("Tá mé ag téacsáil ar mo fhón.", "I am texting on my phone.", "I am texting on my phone.")
                ),
                tags = setOf("technology"),
                difficulty = DifficultyLevel.BEGINNER
            ),
            VocabularyEntry(
                id = "teicneolaíocht",
                irish = "teicneolaíocht",
                english = "technology",
                partOfSpeech = PartOfSpeech.NOUN,
                gender = GrammaticalGender.FEMININE,
                genitive = "teicneolaíochta",
                pronunciation = PronunciationInfo("tyek-nuh-lee-uxt"),
                tags = setOf("technology", "modern"),
                difficulty = DifficultyLevel.INTERMEDIATE
            )
        )
    }
}

class ArticleRepositoryImpl : ArticleRepository {
    private val articles = MutableStateFlow(listOf(sampleArticle))

    override suspend fun getArticle(id: String): Article? {
        return articles.value.firstOrNull { it.id == id }
    }

    override fun observeArticles(): Flow<List<ArticleSummary>> {
        return articles.map { list ->
            list.map {
                ArticleSummary(it.id, it.title, it.irishTitle, it.content.take(60) + "...", it.difficulty, 4)
            }
        }
    }

    companion object {
        val sampleArticle = Article(
            id = "nuacht-teic",
            title = "Modern Technology in Ireland",
            irishTitle = "Teicneolaíocht Nua-Aimsireach in Éirinn",
            content = "Tá daoine ag baint úsáide as fón póca gach lá. Tá an teicneolaíocht ag athrú go tapa sa saol nua.",
            paragraphs = listOf(
                "Tá daoine ag baint úsáide as fón póca gach lá.",
                "Tá an teicneolaíocht ag athrú go tapa sa saol nua."
            ),
            sentenceTranslations = mapOf(
                "Tá daoine ag baint úsáide as fón póca gach lá." to "People are using a mobile phone every day.",
                "Tá an teicneolaíocht ag athrú go tapa sa saol nua." to "Technology is changing quickly in the modern life."
            ),
            literalTranslations = mapOf(
                "Tá daoine ag baint úsáide as fón póca gach lá." to "Is people striking use out of phone pocket every day."
            ),
            vocabularyIds = listOf("fón", "teicneolaíocht"),
            difficulty = DifficultyLevel.INTERMEDIATE_BEGINNER,
            grammarNotes = listOf("Notice 'ag baint úsáide as' means 'using' (striking use out of).")
        )
    }
}

class ProgressRepositoryImpl : ProgressRepository {
    private val progress = MutableStateFlow(UserProgress(wordsRecognizedCount = 5, wordsActivelyRecalledCount = 3, lessonsCompletedCount = 1))
    private val dialectPreference = MutableStateFlow(Dialect.STANDARD)
    private val reviews = MutableStateFlow(
        listOf(
            ReviewState("eagla", intervalDays = 1, status = LearningStatus.LEARNING),
            ReviewState("athas", intervalDays = 4, status = LearningStatus.FAMILIAR)
        )
    )

    override fun observeProgress(): Flow<UserProgress> = progress
    override fun observeReviewQueue(): Flow<List<ReviewState>> = reviews

    override suspend fun recordLessonCompletion(lessonId: String) {
        progress.value = progress.value.copy(
            lessonsCompletedCount = progress.value.lessonsCompletedCount + 1
        )
    }

    override suspend fun recordReview(itemId: String, isCorrect: Boolean) {
        val updated = reviews.value.map { item ->
            if (item.itemId == itemId) {
                if (isCorrect) {
                    val nextInterval = item.intervalDays * 2
                    item.copy(
                        intervalDays = nextInterval,
                        repetitions = item.repetitions + 1,
                        status = if (nextInterval > 3) LearningStatus.KNOWN else LearningStatus.FAMILIAR
                    )
                } else {
                    item.copy(
                        intervalDays = 1,
                        status = LearningStatus.LEARNING
                    )
                }
            } else item
        }
        reviews.value = updated
        progress.value = progress.value.copy(
            reviewsCompletedCount = progress.value.reviewsCompletedCount + 1,
            wordsActivelyRecalledCount = progress.value.wordsActivelyRecalledCount + (if (isCorrect) 1 else 0)
        )
    }

    override fun observeDialectPreference(): Flow<Dialect> = dialectPreference
    override suspend fun setDialectPreference(dialect: Dialect) {
        dialectPreference.value = dialect
    }
}

class TopicLessonRepositoryImpl : TopicLessonRepository {
    private val lessons = MutableStateFlow(listOf(sampleLesson))

    override fun observeLessons(): Flow<List<TopicLesson>> = lessons

    override suspend fun getLesson(id: String): TopicLesson? {
        return lessons.value.firstOrNull { it.id == id }
    }

    companion object {
        val sampleLesson = TopicLesson(
            id = "emotions-01",
            title = "Emotions & Feelings",
            irishTitle = "Mothúcháin",
            level = "A2",
            description = "Learn how to speak naturally about how you feel using common idiom structures.",
            vocabularyIds = listOf("eagla", "athas", "fearg"),
            grammarTopics = listOf("prepositional-pronouns-ar"),
            explanationCard = "Irish expresses emotions using Noun + Preposition 'ar' (on). For example: 'Tá eagla orm' literally means 'Fear is on me'.",
            recognitionChallenges = listOf(
                "Tá eagla orm." to "I am afraid.",
                "Tá áthas orm." to "I am happy."
            ),
            sentenceBuildingChallenges = listOf(
                "Tá orm áthas" to listOf("Tá", "áthas", "orm"),
                "eagla Tá orm" to listOf("Tá", "eagla", "orm")
            )
        )
    }
}
