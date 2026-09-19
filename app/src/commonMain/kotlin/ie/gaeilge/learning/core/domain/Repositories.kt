package ie.gaeilge.learning.core.domain

import kotlinx.coroutines.flow.Flow

interface VocabularyRepository {
    suspend fun getWord(id: String): VocabularyEntry?
    suspend fun search(query: String): List<VocabularyEntry>
    suspend fun saveWord(id: String)
    suspend fun removeSavedWord(id: String)
    fun observeSavedWords(): Flow<List<VocabularyEntry>>
    fun observeAllVocabulary(): Flow<List<VocabularyEntry>>
}

interface ArticleRepository {
    suspend fun getArticle(id: String): Article?
    fun observeArticles(): Flow<List<ArticleSummary>>
}

interface ProgressRepository {
    fun observeProgress(): Flow<UserProgress>
    fun observeReviewQueue(): Flow<List<ReviewState>>
    suspend fun recordLessonCompletion(lessonId: String)
    suspend fun recordReview(itemId: String, isCorrect: Boolean)
    fun observeDialectPreference(): Flow<Dialect>
    suspend fun setDialectPreference(dialect: Dialect)
}

interface TopicLessonRepository {
    fun observeLessons(): Flow<List<TopicLesson>>
    suspend fun getLesson(id: String): TopicLesson?
}
