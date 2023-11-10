package go.model


val BOARD_SIZE = BoardDimension.SMALL

typealias BoardCells = Map<Position, Stone>


/**
 * Represents the board in the Go Game
 * @property cells The moves made by players in the game
 */
sealed class Board(val cells : BoardCells) {
    override fun equals(other: Any?): Boolean {
        return other is Board && cells == other.cells
    }

    override fun hashCode(): Int {
        return cells.hashCode()
    }

    /**
     * Updates the game state. If the game has ended and this function is called, there will be an error.
     * @return A new board with the updated game state
     *
     */
    fun play(pos: Position) : Board {
        return when (this) {
            is BoardPass -> validatePlay(pos)
            is BoardRun -> validatePlay(pos)
            is BoardFinish -> error("Game Over")
        }
    }

    /**
     * Passes a turn.
     * @return The same board if the previous turn wasn't a pass, or a Board representing the finished game state if this
     */
    fun pass() : Board {
        return when(this) {
            is BoardFinish -> throw IllegalStateException("Game Over")
            is BoardPass -> BoardFinish(this.cells, calculateFinalScore(this.cells, this.currPoints))
            is BoardRun -> BoardPass(cells, prevCells, turn.other, currPoints)
        }
    }

}

open class BoardRun(cells: BoardCells, val prevCells : BoardCells, val turn : Stone, var currPoints : Points<Int> ) : Board(cells) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is BoardRun && turn == other.turn && prevCells == other.prevCells && currPoints == other.currPoints
    }

    fun validatePlay(pos: Position): Board {

        var newCells = cells + (pos to turn)
        val group = pos.getPosGroup(newCells, turn)

        group.getAdjacentGroups(newCells, turn.other).forEach{adjGroup ->

            if (!adjGroup.hasLiberties(newCells)) {

                newCells = newCells - adjGroup

                currPoints = if(turn == Stone.White)
                    Points(currPoints.white + adjGroup.size, currPoints.black)
                else
                    Points(currPoints.white, currPoints.black + adjGroup.size)

            }

        }

        require(group.hasLiberties(newCells)) {"Position ${pos.String()} is not valid (liberty rule)"}
        require(newCells != prevCells) {"Position ${pos.String()} is not valid (ko rule)"}

        return if(newCells.size == BOARD_SIZE.size * BOARD_SIZE.size) BoardFinish(newCells, calculateFinalScore(cells, currPoints))
        else BoardRun(newCells, cells, turn.other, currPoints)

    }

}

operator fun BoardCells.minus(group: Group): BoardCells = this.filterKeys { position -> position !in group.positions }.toMap()

class BoardPass(cells : BoardCells, prevCells: BoardCells, turn: Stone, currPoints: Points<Int>) : BoardRun(cells,prevCells,turn, currPoints)

class BoardFinish(cells : BoardCells, val score : Points<Float>) : Board(cells)

fun newBoard() = BoardRun(emptyMap(), emptyMap(), Stone.Black, Points(0,0))

fun getEmptyAreas(board: BoardCells): Set<Group> {

    var emptyAreas = setOf<Group>()
    var emptyCells = Position.values.minus(board.keys)

    while(emptyCells.isNotEmpty()) {
        val area = emptyCells.first().getPosGroup(board, null)
        emptyAreas = emptyAreas + area
        emptyCells = emptyCells - area.positions
    }

    return emptyAreas

}

fun getPlayerFromArea(area: Group, boardCells: BoardCells): Stone? {

    val whiteGroups = area.getAdjacentGroups(boardCells, Stone.White)
    val blackGroups = area.getAdjacentGroups(boardCells, Stone.Black)

    return if(whiteGroups.isNotEmpty() && blackGroups.isEmpty()) Stone.White
    else if (blackGroups.isNotEmpty() && whiteGroups.isEmpty()) Stone.Black
    else null

}

fun calculateFinalScore(cells: BoardCells, currPoints: Points<Int>): Points<Float> {

    val emptyAreas = getEmptyAreas(cells)

    var blackTerritory = 0
    var whiteTerritory = 0

    for (area in emptyAreas) {
        when (getPlayerFromArea(area, cells)) {
            Stone.Black -> blackTerritory += area.size
            Stone.White -> whiteTerritory += area.size
            else -> continue
        }
    }

    val blackScore = blackTerritory + currPoints.black - BOARD_SIZE.komi
    val whiteScore = whiteTerritory + currPoints.white

    return Points(whiteScore.toFloat(), blackScore.toFloat())

}