package ie.gaeilge.learning.core.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryLessonRepositoryTest {
    private val repository = InMemoryLessonRepository()

    @Test
    fun sampleLessonsContainACompletedGreetingLesson() {
        val greetings = repository.getLessons().first { it.id == "greetings" }

        assertEquals("Beannachtaí", greetings.irishTitle)
        assertTrue(greetings.completed)
    }

    @Test
    fun repositoryReturnsProvidedLessonsWithoutUiKnowledge() {
        val custom = InMemoryLessonRepository(listOf(InMemoryLessonRepository.sampleLessons.last()))

        assertEquals(listOf("home"), custom.getLessons().map { it.id })
    }

    @Test
    fun pictureChallengesAlwaysIncludeTheirCorrectPicture() {
        InMemoryPictureGameRepository().getChallenges().forEach { challenge ->
            assertTrue(challenge.picture in challenge.options)
        }
    }

    @Test
    fun pictureGameIncludesAnAnimalRound() {
        assertTrue(InMemoryPictureGameRepository().getChallenges().any { it.word == "Madra" })
    }
}
