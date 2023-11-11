package go.view

import go.model.*

fun getColLetters() : List<Char> =  buildList { (0 until BOARD_SIZE.size).forEach{ add('A' + it) } }


/**
 * Outputs the board to stdout
 */
fun Board.show() {
    println("   " + getColLetters().joinToString(separator = " "))
    Position.values.forEach { pos ->
        when (pos.col) {
            0 -> {
                if(BOARD_SIZE.size - (pos.row) <= 9) print("${BOARD_SIZE.size - pos.row}  ${cells[pos]?.char ?: '.'}")
                else print("${BOARD_SIZE.size - pos.row} ${cells[pos]?.char ?: '.'}")
            }
            BOARD_SIZE.size-1 -> println(" ${cells[pos]?.char ?: '.'}")
            else -> print(" ${cells[pos]?.char ?: '.'}")
        }

    }
    println(when(this) {
        is BoardPass -> "Player ${player.other.char} Passes. Turn: ${player.char} (${player.name})"
        is BoardFinish -> "GAME OVER   SCORE: #=${score.black} - O=${score.white}"
        is BoardRun -> "Turn: ${player.char} (${player.name}) Captures: #=${currPoints.black} - O=${currPoints.white}"
    })

}