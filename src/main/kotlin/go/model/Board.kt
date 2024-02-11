package go.model


val BOARD_SIZE = BoardDimension.SMALL

/**
 * Represents the Board, with player moves.
 */
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
     * Updates the game state.
     *
     * @return A new board with the updated game state
     * @throws IllegalArgumentException if the game has already ended
     */
    fun play(pos: Position) : Board {
        return when (this) {
            is BoardPass -> validatePlay(pos)
            is BoardRun -> validatePlay(pos)
            is BoardFinish -> throw IllegalStateException("Game Over")
        }
    }

    /**
     * Passes a turn.
     * @return The same board if the previous turn wasn't a pass, or a Board representing the finished game state
     * if the previous turn was a pass.
     * @throws IllegalStateException if the game has already ended
     */
    fun pass() : Board {
        return when(this) {
            is BoardFinish -> throw IllegalStateException("Game Over")
            is BoardPass -> BoardFinish(cells, calculateFinalScore(cells, currPoints))
            is BoardRun -> BoardPass(cells, prevCells, player.other, currPoints)
        }
    }

}

/**
 * Represents a "playing" state board, for when there are plays being made by players
 * @property cells The moves made by players in the game
 * @property prevCells The moves belonging to this board in the previous turn. Used for validating ko rule moves
 * @property player This turn's player
 * @property currPoints The current points in the game
 */

open class BoardRun(cells: BoardCells, val prevCells : BoardCells, val player : Stone, val currPoints : Points<Int> ) : Board(cells) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is BoardRun && player == other.player && prevCells == other.prevCells && currPoints == other.currPoints
    }


    /**
     * Validates a play based on the Go rules.
     * @param pos The played position
     * @return A new board with the updated game state if the play was valid
     * @throws IllegalArgumentException if the play doesn't obey the Ko or Liberty rules.
     */
    fun validatePlay(pos: Position): Board {

        require(cells[pos] == null) { "Position used" }

        val newCells = cells + (pos to player)
        val group = pos.getPosGroup(newCells, player)

        var cellsAfterCaptures = newCells
        var pointsAfterCaptures = currPoints

        group.getAdjacentGroups(newCells, player.other).forEach { adjGroup ->

            if (!adjGroup.hasLiberties(newCells)) {

                cellsAfterCaptures = cellsAfterCaptures - adjGroup

                pointsAfterCaptures = if(player == Stone.WHITE)
                    Points(pointsAfterCaptures.white + adjGroup.size, pointsAfterCaptures.black)
                else
                    Points(pointsAfterCaptures.white, pointsAfterCaptures.black + adjGroup.size)

            }

        }

        require(cellsAfterCaptures != prevCells) {"Position ${pos.String()} is not valid (ko rule)"}
        require(group.hasLiberties(cellsAfterCaptures)) {"Position ${pos.String()} is not valid (liberty rule)"}

        return if(cellsAfterCaptures.size == BOARD_SIZE.size * BOARD_SIZE.size) BoardFinish(cellsAfterCaptures, calculateFinalScore(cells, pointsAfterCaptures))
        else BoardRun(cellsAfterCaptures, cells, player.other, pointsAfterCaptures)

    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + prevCells.hashCode()
        result = 31 * result + player.hashCode()
        result = 31 * result + currPoints.hashCode()
        return result
    }

}

/**
 * Removes a group from the board
 * @param group the group to remove
 */
operator fun BoardCells.minus(group: Group): BoardCells = this.filterKeys { position -> position !in group.positions }.toMap()

/**
 * Represents a Board for the "passed turn" state of the game
 * @property cells The moves made by players in the game
 * @property prevCells The moves belonging to this board in the previous turn. Used for validating ko moves
 * @property player This turn's player
 * @property currPoints The current points in the game
*/
class BoardPass(cells : BoardCells, prevCells: BoardCells, turn: Stone, currPoints: Points<Int>) : BoardRun(cells,prevCells,turn, currPoints)

/**
 * Represents a board for the "finished" state of the game
 * @property cells The moves made in the game
 * @property score The final game score
 */
class BoardFinish(cells : BoardCells, val score : Points<Double>) : Board(cells) {
    val winner get() = if (score.black > score.white) Stone.BLACK else if (score.black < score.white) Stone.WHITE else null
}

/**
 * Creates a new Go Board.
 */
fun newBoard() = BoardRun(emptyMap(), emptyMap(), Stone.BLACK, Points(0,0))


/**
 * Returns the empty areas belonging to the board provided by [board]
 * @param board The board to search for empty areas.
 */
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

/**
 * Returns the player that the [area] is surrounded by.
 *
 * If the area is not surrounded by
 * a single player, returns null
 *
 * @param area The group of positions to check
 * @param boardCells The board to search through
 */
fun playerFromEmptyArea(area: Group, boardCells: BoardCells): Stone? {

    var adjacentPlayers = setOf<Stone>()

    for (pos in area.positions) {
        pos.getAdjacents().forEach { adjPos ->
            val cell = boardCells[adjPos]
            if (cell != null) {
                adjacentPlayers = adjacentPlayers + cell
                if(adjacentPlayers.size == 2) return  null
            }
        }
    }

    return if (adjacentPlayers.size == 1) adjacentPlayers.first()
    else null

}


/**
 * Returns the final score of the game
 * @param cells The board to check
 * @param currPoints The current game points accumulated by captures
 */
fun calculateFinalScore(cells: BoardCells, currPoints: Points<Int>): Points<Double> {

    val emptyAreas = getEmptyAreas(cells)

    var blackTerritory = 0
    var whiteTerritory = 0

    for (area in emptyAreas) {
        when (playerFromEmptyArea(area, cells)) {
            Stone.BLACK -> blackTerritory += area.size
            Stone.WHITE -> whiteTerritory += area.size
            else -> continue
        }
    }

    val blackScore = blackTerritory + currPoints.black - BOARD_SIZE.komi
    val whiteScore = whiteTerritory + currPoints.white

    return Points(whiteScore.toDouble(), blackScore)

}