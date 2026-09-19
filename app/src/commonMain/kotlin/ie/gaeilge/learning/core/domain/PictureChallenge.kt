package ie.gaeilge.learning.core.domain

data class PictureChallenge(
    val word: String,
    val translation: String,
    val picture: Picture,
    val options: List<Picture>,
)

enum class Picture {
    SUN,
    APPLE,
    HOUSE,
    TREE,
    CAT,
    BOOK,
}
