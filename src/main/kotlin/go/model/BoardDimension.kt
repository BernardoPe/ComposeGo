package go.model

import java.lang.IllegalArgumentException


/**
 * Represents a Board Size for the Go game
 * @property size the size of the board
 * @property komi the Komi value dependent on the board size
 */
enum class BoardDimension (val size: Int, val komi: Double) {
    SMALL(9, 3.5),
    MEDIUM(13, 4.5),
    LARGE(19, 5.5)
}

fun Int.toBoardDim() : BoardDimension =
    when(this) {
        9 -> BoardDimension.SMALL
        13-> BoardDimension.MEDIUM
        19 -> BoardDimension.LARGE
        else -> throw IllegalArgumentException("{\"Invalid Board Size\"}")
    }
