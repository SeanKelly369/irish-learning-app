package ie.gaeilge.learning.core.data

import ie.gaeilge.learning.core.domain.Lesson
import ie.gaeilge.learning.core.domain.LessonRepository

class InMemoryLessonRepository(
    private val lessons: List<Lesson> = sampleLessons,
) : LessonRepository {
    override fun getLessons(): List<Lesson> = lessons

    companion object {
        val sampleLessons = listOf(
            Lesson("greetings", "Greetings", "Beannachtaí", "Say hello and introduce yourself.", 8, true),
            Lesson("family", "My family", "Mo theaghlach", "Learn the words for the people closest to you.", 12),
            Lesson("home", "Around the home", "Sa bhaile", "Build a useful everyday vocabulary.", 10),
        )
    }
}
