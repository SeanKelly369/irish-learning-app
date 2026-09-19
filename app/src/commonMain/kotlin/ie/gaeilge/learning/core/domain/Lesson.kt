package ie.gaeilge.learning.core.domain

data class Lesson(
    val id: String,
    val title: String,
    val irishTitle: String,
    val description: String,
    val durationMinutes: Int,
    val completed: Boolean = false,
)

interface LessonRepository {
    fun getLessons(): List<Lesson>
}
