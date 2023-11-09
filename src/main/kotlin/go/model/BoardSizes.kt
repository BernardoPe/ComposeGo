package go.model

enum class BoardDimension (val size: Int, val komi: Double) {
    SMALL(9, 3.5),
    MEDIUM(13, 4.5),
    LARGE(19, 5.5)
}