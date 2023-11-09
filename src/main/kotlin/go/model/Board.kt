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
            BoardRun(newBoardCells, cells, turn.other, currPoints)
        }
        is BoardRun -> {
            val newBoardCells = cells + (pos to this.turn)
            BoardRun(newBoardCells, cells, turn.other, currPoints)
        }
        is BoardFinish -> error("Game Over")
    }
}

fun newBoard() = BoardRun(emptyMap(), emptyMap(), Stone.BLACK, Points<Int>(0,0))

fun Board.pass() : Board {
    return when(this) {

        is BoardFinish -> throw IllegalStateException("Game Over")

        is BoardPass -> BoardFinish(this.cells, calculateFinalScore(this.cells, this.currPoints))

        is BoardRun -> BoardPass(cells, prevCells, turn, currPoints)

    }
}

fun calculateFinalScore(cells: BoardCells, currPoints: Points<Int>): Points<Float> {
    TODO()
}
