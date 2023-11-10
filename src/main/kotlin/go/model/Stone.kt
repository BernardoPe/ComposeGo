package go.model


/**
 * Represents a Stone in the Go Board.
 * @property char the character by which the Stone is represented in the board.
 */
enum class Stone(val char: Char) {
    Black('#'), White('O');

    val other get() = if (this == Black) White else Black
}


fun String.toStone() = Stone.valueOf(this )
