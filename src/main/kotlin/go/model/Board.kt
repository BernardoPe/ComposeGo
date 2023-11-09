package go.model


val BOARD_DIM = BoardDimension.SMALL

typealias BoardCells = Map<Position, Stone>

sealed class Board(val cells : BoardCells) {
    override fun equals(other: Any?): Boolean {
        return other is Board && cells == other.cells
    }

    override fun hashCode(): Int {
        return cells.hashCode()
    }
}

open class BoardRun(cells: BoardCells, val prevCells : BoardCells, val turn : Stone, var currPoints : Points<Int> ) : Board(cells) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is BoardRun && turn == other.turn && prevCells == other.prevCells && currPoints == other.currPoints
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + prevCells.hashCode()
        result = 31 * result + turn.hashCode()
        result = 31 * result + currPoints.hashCode()
        return result
    }

}

class BoardPass(cells : BoardCells, prevCells: BoardCells, turn: Stone, currPoints: Points<Int>) : BoardRun(cells,prevCells,turn, currPoints)

class BoardFinish(cells : BoardCells, val score : Points<Float>) : Board(cells)


fun Board.play(pos: Position) : Board {
    return when (this) {
        is BoardPass -> BoardFinish(cells, calculateFinalScore(cells, currPoints))
        is BoardRun -> validatePlay(pos)
        is BoardFinish -> error("Game Over")
    }
}


fun BoardRun.validatePlay(pos: Position): Board {

    var newCells = cells + (pos to turn)
    val group = pos.getPosGroup(cells, turn)

    group.getAdjacentGroups(newCells).forEach{adjGroup ->

        if (!adjGroup.hasLiberties(newCells)) {

            newCells = newCells - adjGroup

            if(turn == Stone.White)
                currPoints = Points(currPoints.white + adjGroup.size, currPoints.black)
            else
                currPoints = Points(currPoints.white, currPoints.black + adjGroup.size)

        }

    }

    require(group.hasLiberties(newCells)) {"Position ${pos.String()} is not valid (liberty rule)"}
    require(newCells != prevCells) {"Position ${pos.String()} is not valid (ko rule)"}

    return if(newCells.size == BOARD_DIM.size * BOARD_DIM.size) BoardFinish(newCells, calculateFinalScore(cells, currPoints))
    else BoardRun(newCells, cells, turn.other, currPoints)

}



operator fun BoardCells.minus(group: Group): BoardCells = this.filterKeys { position -> position !in group.positions }.toMap()


fun newBoard() = BoardRun(emptyMap(), emptyMap(), Stone.Black, Points<Int>(0,0))

fun Board.pass() : Board {

    return when(this) {
        is BoardFinish -> throw IllegalStateException("Game Over")

        is BoardPass -> BoardFinish(this.cells, calculateFinalScore(this.cells, this.currPoints))

        is BoardRun -> BoardPass(cells, prevCells, turn.other, currPoints)
    }

}

fun Position.getPosGroup(board: BoardCells, player: Stone): Group {

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

fun Group.hasLiberties(board: BoardCells): Boolean {
    for (pos in positions) {
        pos.getAdjacents().forEach { adjPos ->
            if (board[Position(adjPos.index)] == null) {
                return true
            }
        }
    }
    return false
}

fun Group.getAdjacentGroups(board: BoardCells): Set<Group> {

    val adjacentGroups = mutableSetOf<Group>()
    val checkedPositions = mutableSetOf<Position>()

    for (pos in positions) {
        pos.getAdjacents().forEach { adjPos ->
            if (board[Position(adjPos.index)] != null && board[Position(adjPos.index)] != player) {
                val adjGroup = adjPos.getPosGroup(board, player.other)
                if (adjGroup.positions.isNotEmpty()) {
                    if (adjGroup.positions.all { checkedPositions.add(it) }) {
                        adjacentGroups.add(adjGroup)
                    }
                }
            }
        }
    }

    return adjacentGroups
}

fun getEmptyAreas(board: BoardCells): Set<Set<Position>> {
    val emptyAreas = mutableSetOf<Set<Position>>()
    val emptyCells = Position.values.minus(board.keys).toMutableSet()
    while(emptyCells.isNotEmpty()) {
        val area = mutableSetOf<Position>()
        findEmptyArea(emptyCells, emptyCells.first(), area)
        emptyAreas.add(area)
    }
    return emptyAreas
}
private fun findEmptyArea(emptyCells: MutableSet<Position>, position: Position, area: MutableSet<Position>) {
    emptyCells.remove(position)
    area.add(position)
    position.getAdjacents().forEach { pos ->
        if(emptyCells.contains(pos)) findEmptyArea(emptyCells, pos, area)
    }
}
fun getPlayerFromArea(area: Set<Position>, boardCells: BoardCells): Stone? {
    val adjacentPlayers = mutableSetOf<Stone>()

    for (pos in area) {
        pos.getAdjacents().forEach { adjPos ->
            val cell = boardCells[Position(adjPos.index)]
            if (cell != null) {
                adjacentPlayers.add(cell)
                if(adjacentPlayers.size == 2) return  null
            }
        }
    }
    return if (adjacentPlayers.size == 1) adjacentPlayers.first()
    else null
}

fun calculateFinalScore(cells: BoardCells, currPoints: Points<Int>): Points<Float> {

    val emptyAreas = getEmptyAreas(cells)

    var blackTerritory = 0
    var whiteTerritory = 0

    for (area in emptyAreas) {
        val player = getPlayerFromArea(area, cells)
        when (player) {
            Stone.Black -> blackTerritory += area.size
            Stone.White -> whiteTerritory += area.size
            else -> continue
        }
    }

    val blackScore = blackTerritory + currPoints.black - BOARD_DIM.komi
    val whiteScore = whiteTerritory + currPoints.white

    return Points(whiteScore.toFloat(), blackScore.toFloat())

}