package go.storage

import go.model.*


object BoardSerialize : Serializer<Board> {
    override fun serialize(data: Board): String =
        when (data) {
            is BoardPass -> "Pass\n" + printBoardCells(data.cells) + "\n" + data.turn + "\n" + data.currPoints.black + " "+ data.currPoints.white
            is BoardRun -> "Run\n" + printBoardCells(data.cells) + "\n" + data.turn + "\n" + data.currPoints.black + " "+ data.currPoints.white
            is BoardFinish -> "Finish \n" + printBoardCells(data.cells) + "\n" + data.score.black +" "+ data.score.white

        }


    private fun printBoardCells(cells: BoardCells): String {
        val result = StringBuilder()

        for ((pos, stone) in cells) {
            result.append("${pos.index}:$stone ")
        }

        return result.toString().trim()
    }

    override fun deserialize(text: String): Board {
        val lines = text.split("\n")
        val type = lines[0].trim()

        val cellsString = lines[1].trim()
        val cells = deserializeBoardCells(cellsString)

        return when (type) {
            "Pass" -> {
                val turn = lines[2].trim().toStone()
                val points = lines[3].split(" ").let { Points(it[0].toInt(), it[1].toInt()) }
                BoardPass(cells, emptyMap(), turn, points)
            }
            "Run" -> {
                val turn = lines[2].trim().toStone()
                val points = lines[3].split(" ").let { Points(it[0].toInt(), it[1].toInt()) }
                BoardRun(cells, emptyMap(), turn, points)
            }
            "Finish" -> {
                val scores = lines[2].split(" ").let { Points(it[0].toFloat(), it[1].toFloat()) }
                BoardFinish(cells, scores)
            }
            else -> throw IllegalArgumentException("Invalid board type")
        }
    }

    private fun deserializeBoardCells(cellsString: String): BoardCells {
        val cellList = cellsString.split(" ")
        val cells = mutableListOf<Pair<Position, Stone>>()

        for (cell in cellList) {
            val (index, stoneStr) = cell.split(":")
            val position = Position(index.toInt())
            val stone = Stone.valueOf(stoneStr)
            cells.add(position to stone)
        }

        return cells.toMap()
    }


}


fun main() {
    val cells = mapOf(
        Position(0, 0) to Stone.Black,
        Position(1, 1) to Stone.White,
        Position(2, 2) to Stone.Black
        // Add more entries as needed
    )

    val boardPass = BoardPass(cells, emptyMap(), Stone.Black, Points(0, 0))
    val boardRun = BoardRun(cells, emptyMap(), Stone.White, Points(0, 0))
    val boardFinish = BoardFinish(cells, Points(10f, 20f))

    val boardPassSerialized = BoardSerialize.serialize(boardPass)
    val boardRunSerialized = BoardSerialize.serialize(boardRun)
    val boardFinishSerialized = BoardSerialize.serialize(boardFinish)

    println( boardPassSerialized)
    println("\n")
    println(boardRunSerialized)
    println("\n")
    println(boardFinishSerialized)

    val board1 = BoardSerialize.deserialize(boardPassSerialized)
    val board2 = BoardSerialize.deserialize(boardRunSerialized)
    val board3 = BoardSerialize.deserialize(boardFinishSerialized)
    println(board1)
    println("\n")
    println(board2)
    println("\n")
    println(board3)



}