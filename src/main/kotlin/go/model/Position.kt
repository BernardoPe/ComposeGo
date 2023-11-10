package go.model
@JvmInline
value class Position private constructor(val index: Int) {

    val row : Int get() = index / BOARD_SIZE.size

    val col : Int get() = index % BOARD_SIZE.size

    val backSlash : Boolean get() = row == col

    val slash : Boolean get() = row + col == BOARD_SIZE.size - 1

    companion object {

        val values = List(BOARD_SIZE.size * BOARD_SIZE.size) { idx -> Position(idx) }

        operator fun invoke(index : Int ) : Position {
            require(index in 0 until BOARD_SIZE.size * BOARD_SIZE.size) {"Out of bounds"}
            return values[index]
        }

        operator fun invoke(row : Int, col : Int) : Position {
            require(col in 0 until BOARD_SIZE.size && row in 0 until BOARD_SIZE.size) {"Out of bounds"}
            return values[getIndex(col,row)]
        }

        private fun getIndex(col: Int, row: Int): Int {
            return row * BOARD_SIZE.size + col
        }

    }

    fun getAdjacents() : List<Position> {
        val adjacents = mutableListOf<Position>()
        Direction.values().forEach {
            val adjPos = this + it
            if(adjPos != null) adjacents += adjPos
        }
        return adjacents
    }

    fun getPosGroup(board: BoardCells, player: Stone): Group {

        val visitedPositions = mutableSetOf<Position>()
        val positionsToCheck = mutableSetOf(this)
        val group = mutableSetOf<Position>()

        while (positionsToCheck.isNotEmpty()) {
            val currentPos = positionsToCheck.first()
            positionsToCheck.remove(currentPos)
            visitedPositions.add(currentPos)
            group.add(currentPos)

            currentPos.getAdjacents().forEach { adjPos ->
                if (board[Position(adjPos.index)] != null && adjPos !in visitedPositions && board[Position(adjPos.index)] == player) {
                    positionsToCheck.add(adjPos)
                }
            }
        }
        return Group(group, player)
    }
    fun String() : String = "${ 'A' + col }${ BOARD_SIZE.size - row }"

}


fun isValidCol(ch : Char) : Boolean = ch in 'A' until 'A' + BOARD_SIZE.size

fun isValidRow(num : Int) : Boolean = num - 1 in 0 until BOARD_SIZE.size

fun isValidPos(col : Int, row: Int) : Boolean = col in 0 until BOARD_SIZE.size && row in 0 until BOARD_SIZE.size

fun getColIndex(ch : Char) : Int {
    require(isValidCol(ch)) {"Column not valid"}
    return ch - 'A'
}

fun getRowIndex(num : Int) : Int {
    require(isValidRow(num)){"Row not valid"}
    return BOARD_SIZE.size - num
}

fun getPosition(str : String) : Position {
    if(BOARD_SIZE.size <= 9) {
        require(str.length == 2 && str[0].isLetter() && str[1].isDigit()) {"Invalid Position"}
        return Position(getRowIndex(str[1].digitToInt()),  getColIndex(str[0].uppercaseChar()))
    }
    else {
        require(str.length == 2 && str[0].isLetter() && str[1].isDigit() || str.length == 3 && str[0].isLetter() && str[1].isDigit() && str[2].isDigit() ) {"Invalid Position"}
        return if(str.length == 2) Position(getRowIndex(str[1].digitToInt()), getColIndex(str[0].uppercaseChar()))
        else Position(getRowIndex("${str[1]}${str[2]}".toInt()),getColIndex(str[0].uppercaseChar()) )
    }
}

operator fun Position.plus(dir: Direction) : Position? {
    val coordsSum = Pair(this.row + dir.dy, this.col + dir.dx)
    return if(isValidPos(coordsSum.first, coordsSum.second)) Position(coordsSum.first, coordsSum.second) else null
}



