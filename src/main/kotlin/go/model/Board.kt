package go.model

import java.awt.Point


val BOARD_DIM = BoardDimension.SMALL

typealias BoardCells = Map<Position, Stone>

sealed class Board(val cells : BoardCells) {
    override fun equals(other: Any?): Boolean {
        return other is Board && cells == other.cells
    }



}

open class BoardRun(cells: BoardCells, val prevCells : BoardCells, val turn : Stone, val currPoints : Points<Int> ) : Board(cells) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is BoardRun && turn == other.turn && prevCells == other.prevCells && currPoints == other.currPoints
    }
}

class BoardPass(cells : BoardCells, prevCells: BoardCells, turn: Stone, currPoints: Points<Int>) : BoardRun(cells,prevCells,turn, currPoints)

class BoardFinish(cells : BoardCells, val score : Points<Float>) : Board(cells)


fun Board.play(pos: Position) : Board {
    return when (this) {
        is BoardPass -> {
            val newBoardCells = cells + (pos to this.turn)
            println(pos.getPosGroup(cells, turn))
            BoardRun(newBoardCells, cells, turn.other, currPoints)
        }
        is BoardRun -> {
            val newBoardCells = cells + (pos to this.turn)
            println(pos.getPosGroup(cells, turn))
            BoardRun(newBoardCells, cells, turn.other, currPoints)
        }
        is BoardFinish -> error("Game Over")
    }
}

fun newBoard() = BoardRun(emptyMap(), emptyMap(), Stone.Black, Points<Int>(0,0))

fun Board.pass() : Board {
    return when(this) {

        is BoardFinish -> throw IllegalStateException("Game Over")

        is BoardPass -> BoardFinish(this.cells, calculateFinalScore(this.cells, this.currPoints))

        is BoardRun -> BoardPass(cells, prevCells, turn, currPoints)

    }
}

fun Position.getPosGroup(board: BoardCells, player: Stone): Set<Position> {
    val visitedPositions = mutableSetOf<Position>()
    val positionsToCheck = mutableSetOf(this)
    val group = mutableSetOf<Position>()

    while (positionsToCheck.isNotEmpty()) {
        val currentPos = positionsToCheck.first()
        positionsToCheck.remove(currentPos)
        visitedPositions.add(currentPos)
        group.add(currentPos)

        // Check adjacent positions for stones and add them to the positions to be checked
        currentPos.getAdjacents().forEach { adjPos ->
            if (board[Position(adjPos.index)] != null && adjPos !in visitedPositions && board[Position(adjPos.index)] == player) {
                positionsToCheck.add(adjPos)
            }
        }
    }

    return group
}

fun calculateFinalScore(cells: BoardCells, currPoints: Points<Int>): Points<Float> {
    TODO()
}
