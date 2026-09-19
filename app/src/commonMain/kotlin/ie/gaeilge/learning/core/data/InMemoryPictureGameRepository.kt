package ie.gaeilge.learning.core.data

import ie.gaeilge.learning.core.domain.Picture
import ie.gaeilge.learning.core.domain.PictureChallenge

class InMemoryPictureGameRepository {
    fun getChallenges(): List<PictureChallenge> = challenges

    companion object {
        private val challenges = listOf(
            PictureChallenge("Grian", "Sun", Picture.SUN, listOf(Picture.SUN, Picture.APPLE, Picture.HOUSE)),
            PictureChallenge("Úll", "Apple", Picture.APPLE, listOf(Picture.TREE, Picture.APPLE, Picture.CAT)),
            PictureChallenge("Teach", "House", Picture.HOUSE, listOf(Picture.BOOK, Picture.HOUSE, Picture.SUN)),
            PictureChallenge("Crann", "Tree", Picture.TREE, listOf(Picture.CAT, Picture.SUN, Picture.TREE)),
            PictureChallenge("Madra", "Dog", Picture.DOG, listOf(Picture.DOG, Picture.BOOK, Picture.CAT)),
            PictureChallenge("Iasc", "Fish", Picture.FISH, listOf(Picture.SUN, Picture.FISH, Picture.APPLE)),
        )
    }
}
