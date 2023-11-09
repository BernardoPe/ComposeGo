package go.model


fun main() {
    val pos = Position(80)
    println("${pos.col}, ${pos.row}")
    pos.getAdjacents().forEach{
        println("${it.col}, ${it.row}")
    }
    val pos2 = getPosition("a1")
    println("${pos2.col}, ${pos2.row}")
}

@JvmInline
value class Position private constructor(val index: Int) {

    val row : Int get() = index / BOARD_DIM.size

    val col : Int get() = index % BOARD_DIM.size

    val backSlash : Boolean get() = row == col

    val slash : Boolean get() = row + col == BOARD_DIM.size - 1

    companion object {

        val values = List(BOARD_DIM.size * BOARD_DIM.size) { idx -> Position(idx) }

        operator fun invoke(index : Int ) : Position {
            require(index in 0 until BOARD_DIM.size * BOARD_DIM.size) {"Out of bounds"}
            return values[index]
        }

        operator fun invoke(row : Int, col : Int) : Position {
            require(col in 0 until BOARD_DIM.size && row in 0 until BOARD_DIM.size) {"Out of bounds"}
            return values[getIndex(row,col)]
        }

        private fun getIndex(col: Int, row: Int): Int {
            return row * BOARD_DIM.size + col
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


}

fun isValidCol(ch : Char) : Boolean = ch in 'A' until 'A' + BOARD_DIM.size

fun isValidRow(num : Int) : Boolean = num - 1 in 0 until BOARD_DIM.size

fun isValidPos(col : Int, row: Int) : Boolean = col in 0 until BOARD_DIM.size && row in 0 until BOARD_DIM.size

fun getColIndex(ch : Char) : Int {
    require(isValidCol(ch)) {"Column not valid"}
    return ch - 'A'
}

fun getRowIndex(num : Int) : Int {
    require(isValidRow(num)){"Row not valid"}
    return BOARD_DIM.size - num
}

fun getPosition(str : String) : Position {
    if(BOARD_DIM.size <= 9) {
        require(str.length == 2 && str[0].isLetter() && str[1].isDigit()) {"Invalid Position"}
        return Position(getColIndex(str[0].uppercaseChar()), getRowIndex(str[1].digitToInt()))
    }
    else {
        require(str.length == 2 && str[0].isLetter() && str[1].isDigit() || str.length == 3 && str[0].isLetter() && str[1].isDigit() && str[2].isDigit() ) {"Invalid Position"}
        return if(str.length == 2) Position(getColIndex(str[0].uppercaseChar()), getRowIndex(str[1].digitToInt()))
        else Position(getColIndex(str[0].uppercaseChar()), getRowIndex("${str[1]}${str[2]}".toInt()))
    }
}

operator fun Position.plus(dir: Direction) : Position? {
    val coordsSum = Pair(this.col + dir.dx, this.row + dir.dy)
    return if(isValidPos(coordsSum.first, coordsSum.second)) Position(coordsSum.first, coordsSum.second) else null
}

