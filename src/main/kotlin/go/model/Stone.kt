package go.model


/**
 * Represents a Stone in the Go Board.
 * @property char the character by which the Stone is represented in the board.
 */
enum class Stone(val char: Char) {
    BLACK('#'), WHITE('O');

    val other get() = if (this == BLACK) WHITE else BLACK
}


fun String.toStone() = Stone.valueOf(this )
