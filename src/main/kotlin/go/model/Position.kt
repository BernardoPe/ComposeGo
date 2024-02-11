package go.model


/**
 * Represents a grid position in the Go board.
 * May be declared as Position(index) or Position(row, col)
 * @property index represents the identifier of the position on the grid
 * @property row represents the row of the position on the grid
 * @property col represents the column of the position on the grid
 * @property values represents the list of possible values for this class, dependent on [BOARD_SIZE]
 */
@JvmInline
value class Position private constructor(val index: Int) {
    val row: Int get() = index / BOARD_SIZE.size

    val col: Int get() = index % BOARD_SIZE.size

    val backSlash: Boolean get() = row == col

    val slash: Boolean get() = row + col == BOARD_SIZE.size - 1

    companion object {

        val values = List(BOARD_SIZE.size * BOARD_SIZE.size) { idx -> Position(idx) }

        operator fun invoke(index: Int): Position {
            require(index in 0 until BOARD_SIZE.size * BOARD_SIZE.size) { "Out of bounds" }
            return values[index]
        }

        operator fun invoke(row: Int, col: Int): Position {
            require(col in 0 until BOARD_SIZE.size && row in 0 until BOARD_SIZE.size) { "Out of bounds" }
            return values[getIndex(col, row)]
        }

        private fun getIndex(col: Int, row: Int): Int {
            return row * BOARD_SIZE.size + col
        }

    }

    /**
     *
     * Returns the valid adjacent positions to the provided [Position].
     *
     */
    fun getAdjacents(): List<Position> {
        var adjacents = listOf<Position>()
        Direction.entries.forEach {
            val adjPos = this + it
            if (adjPos != null) adjacents = adjacents + adjPos
        }
        return adjacents
    }

    /**
     * Returns a String that represents the grid position
     */
    fun String(): String = "${'A' + col}${BOARD_SIZE.size - row}"

    private fun computeGroup(position: Position, board: Map<Position, Stone>, player: Stone?, group: Set<Position>): Set<Position> {

        if (position in group || board[position] != player) return group

        var newGroup = group + position

        position.getAdjacents().forEach {
            newGroup += computeGroup(it, board, player, newGroup)
        }

        return newGroup
    }


    /**
     * Returns the group that the given [Position] belongs to.
     * The returned group will be of positions belonging to the [player]
     * in the [board].
     * @param player The player that this position belongs to
     * @param board The board that this position belongs to
     *
     */

    fun getPosGroup(board: BoardCells, player: Stone?) = Group(computeGroup(this, board, player, setOf()), player)


}


/**
 * Checks if the provided [ch] is a valid column.
 * @param ch A character representing a grid column.
 */
fun isValidCol(ch: Char): Boolean = ch in 'A' until 'A' + BOARD_SIZE.size


/**
 * Checks if the provided [num] is a valid column.
 * @param num An Integer representing a grid row.
 */
fun isValidRow(num: Int): Boolean = num - 1 in 0 until BOARD_SIZE.size


/**
 * Checks if the provided [col] and [row] values are from a valid position on the game board.
 * @param col The column value.
 * @param row The row value.
 */
fun isValidPos(col: Int, row: Int): Boolean = col in 0 until BOARD_SIZE.size && row in 0 until BOARD_SIZE.size


/**
 * Returns the column index represented by [ch]
 * @param ch The character representing a column position in the grid
 * @return index of the column in the game board.
 * @throws IllegalArgumentException if the provided column is not valid.
 */
fun getColIndex(ch: Char): Int {
    require(isValidCol(ch)) { "Column not valid" }
    return ch - 'A'
}

/**
 * Returns the col index represented by [num]
 * @param num The number representing a row position in the grid.
 * @return index of the row in the game board.
 * @throws IllegalArgumentException if the provided row is not valid.
 */
fun getRowIndex(num: Int): Int {
    require(isValidRow(num)) { "Row not valid" }
    return BOARD_SIZE.size - num
}


/**
 * Returns the position from a [str]
 * @param str A string of characters containing the grid position.
 * @return The position object corresponding to the input string.
 * @throws IllegalArgumentException if the input string is not a valid position.
 */
fun getPosition(str: String): Position {
    if (BOARD_SIZE.size <= 9) {
        require(str.length == 2 && str[0].isLetter() && str[1].isDigit()) { "Invalid Position" }
        return Position(getRowIndex(str[1].digitToInt()), getColIndex(str[0].uppercaseChar()))
    } else {
        require(str.length == 2 && str[0].isLetter() && str[1].isDigit() || str.length == 3 && str[0].isLetter() && str[1].isDigit() && str[2].isDigit()) { "Invalid Position" }
        return if (str.length == 2) Position(getRowIndex(str[1].digitToInt()), getColIndex(str[0].uppercaseChar()))
        else Position(getRowIndex("${str[1]}${str[2]}".toInt()), getColIndex(str[0].uppercaseChar()))
    }
}


/**
 * Adds a direction's x and y values to the position
 * @return Resulting position from the sum of the direction and position
 * @see Direction
 */
operator fun Position.plus(dir: Direction): Position? {
    val coordsSum = Pair(this.row + dir.dy, this.col + dir.dx)
    return if (isValidPos(coordsSum.first, coordsSum.second)) Position(coordsSum.first, coordsSum.second) else null
}



