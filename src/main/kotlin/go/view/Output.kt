package go.view

import go.model.*

fun getColLetters() : List<Char> =  buildList { (0 until BOARD_DIM.size).forEach{ add('A' + it) } }


fun Board.show() {
    println("   " + getColLetters().joinToString(separator = " "))
    Position.values.forEach { pos ->
        when (pos.col) {
            0 -> {
                if(BOARD_DIM.size - (pos.row) <= 9) print("${BOARD_DIM.size - pos.row}  ${cells[pos]?.char ?: '.'}")
                else print("${BOARD_DIM.size - pos.row} ${cells[pos]?.char ?: '.'}")
            }
            BOARD_DIM.size-1 -> println(" ${cells[pos]?.char ?: '.'}")
            else -> print(" ${cells[pos]?.char ?: '.'}")
        }

    }
    println(when(this) {
        is BoardPass -> "Player ${turn.other.char} Passes. Turn: ${turn.char} (${turn.name})"
        is BoardFinish -> "GAME OVER   SCORE: #=${score.black} - O=${score.white}"
        is BoardRun -> "Turn: ${turn.char} (${turn.name}) Captures: #=${currPoints.black} - O=${currPoints.white}"
    })
}