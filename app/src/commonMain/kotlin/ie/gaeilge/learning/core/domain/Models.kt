package ie.gaeilge.learning.core.domain

enum class PartOfSpeech {
    NOUN, VERB, ADJECTIVE, ADVERB, PREPOSITION, PRONOUN, CONJUNCTION, IDIOM, PHRASE
}

enum class GrammaticalGender {
    MASCULINE, FEMININE
}

enum class DifficultyLevel {
    BEGINNER, INTERMEDIATE_BEGINNER, INTERMEDIATE, ADVANCED
}

enum class Dialect {
    STANDARD, ULSTER, CONNACHT, MUNSTER
}

enum class LearningStatus {
    NEW, LEARNING, FAMILIAR, KNOWN
}

enum class AssistanceLevel {
    FULL_SUPPORT, GUIDED, IMMERSION
}

data class ExampleSentence(
    val irish: String,
    val literalEnglish: String,
    val naturalEnglish: String,
    val explanation: String? = null
)

data class DialectVariant(
    val dialect: Dialect,
    val form: String,
    val notes: String? = null
)

data class PronunciationInfo(
    val phonetic: String,
    val audioUrl: String? = null
)

data class VocabularyEntry(
    val id: String,
    val irish: String,
    val english: String,
    val partOfSpeech: PartOfSpeech,
    val pronunciation: PronunciationInfo? = null,
    val gender: GrammaticalGender? = null,
    val genitive: String? = null,
    val plural: String? = null,
    val verbalNoun: String? = null,
    val examples: List<ExampleSentence> = emptyList(),
    val dialectVariants: List<DialectVariant> = emptyList(),
    val tags: Set<String> = emptySet(),
    val difficulty: DifficultyLevel = DifficultyLevel.BEGINNER
)

data class ArticleSummary(
    val id: String,
    val title: String,
    val irishTitle: String,
    val description: String,
    val difficulty: DifficultyLevel,
    val estimatedReadTimeMinutes: Int
)

data class Article(
    val id: String,
    val title: String,
    val irishTitle: String,
    val content: String, // Full text with spaces/punctuation
    val paragraphs: List<String>,
    val sentenceTranslations: Map<String, String> = emptyMap(), // Maps Irish sentence to English translation
    val literalTranslations: Map<String, String> = emptyMap(),
    val vocabularyIds: List<String> = emptyList(),
    val difficulty: DifficultyLevel = DifficultyLevel.BEGINNER,
    val grammarNotes: List<String> = emptyList()
)

data class ReviewState(
    val itemId: String,
    val ease: Double = 2.5,
    val intervalDays: Int = 1,
    val repetitions: Int = 0,
    val nextReviewTimestamp: Long = 0L, // millisecond timestamp instead of Instant for easy cross-platform compatibility
    val lastReviewedTimestamp: Long? = null,
    val status: LearningStatus = LearningStatus.NEW
)

data class TopicLesson(
    val id: String,
    val title: String,
    val irishTitle: String,
    val level: String,
    val description: String,
    val vocabularyIds: List<String>,
    val grammarTopics: List<String>,
    val explanationCard: String,
    val recognitionChallenges: List<Pair<String, String>>, // Question -> Correct Answer
    val sentenceBuildingChallenges: List<Pair<String, List<String>>> // Scrambled tokens -> Correct text sequence
)

data class UserProgress(
    val wordsRecognizedCount: Int = 0,
    val wordsActivelyRecalledCount: Int = 0,
    val lessonsCompletedCount: Int = 0,
    val articlesCompletedCount: Int = 0,
    val reviewsCompletedCount: Int = 0,
    val grammarTopicsEncounteredCount: Int = 0
)
