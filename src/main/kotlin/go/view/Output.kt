package go.view

import go.model.*

fun main() {
    var board : Board = newBoard()
    board.show()
    board = board.pass()
    board.show()
    board = board.pass()
    board.show()
}


fun getColLetters() : List<Char> =  buildList { (0 until BOARD_DIM.size).forEach{ add('A' + it) } }


fun Board.show() {
    println("  " + getColLetters().joinToString(separator = " "))
    Position.values.forEach { pos ->
        if (pos.col == 0) print("${BOARD_DIM.size - pos.row} ${cells[pos]?.char ?: '.'}")
        else if(pos.col == BOARD_DIM.size - 1) println(" ${cells[pos]?.char ?: '.'}")
        else print(" ${cells[pos]?.char ?: '.'}")
    }
    println(when(this) {
        is BoardPass -> "Player ${turn.other.char} Passes. Turn: ${turn.char} (${turn.name})"
        is BoardFinish -> "GAME OVER   SCORE: ${this.score}"
        is BoardRun -> "Turn: ${turn.char} (${turn.name}) Captures: #=${currPoints.black} - O=${currPoints.white}"
    })
}