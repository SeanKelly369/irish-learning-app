package ie.gaeilge.learning.core.data

import ie.gaeilge.learning.core.domain.LearningStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppRepositoriesTest {

    @Test
    fun testFadaInsensitiveSearchLookups() = runBlocking {
        val repo = VocabularyRepositoryImpl()
        
        // Searching with matching fada
        val exactMatches = repo.search("áthas")
        assertTrue(exactMatches.any { it.id == "athas" })

        // Searching without fada accent should still match perfectly
        val insensitiveMatches = repo.search("athas")
        assertTrue(insensitiveMatches.any { it.id == "athas" })
    }

    @Test
    fun testSpacedRepetitionIntervalCalculations() = runBlocking {
        val progressRepo = ProgressRepositoryImpl()

        // Get initial due item state
        val initialQueue = progressRepo.observeReviewQueue().first()
        val initialItem = initialQueue.first { it.itemId == "eagla" }
        assertEquals(1, initialItem.intervalDays)

        // Record a correct review response
        progressRepo.recordReview("eagla", true)
        val updatedQueue = progressRepo.observeReviewQueue().first()
        val updatedItem = updatedQueue.first { it.itemId == "eagla" }
        
        // Interval should scale or double
        assertEquals(2, updatedItem.intervalDays)
        assertEquals(LearningStatus.FAMILIAR, updatedItem.status)

        // Record an incorrect response should reset interval to 1
        progressRepo.recordReview("eagla", false)
        val resetQueue = progressRepo.observeReviewQueue().first()
        val resetItem = resetQueue.first { it.itemId == "eagla" }
        assertEquals(1, resetItem.intervalDays)
        assertEquals(LearningStatus.LEARNING, resetItem.status)
    }
}
