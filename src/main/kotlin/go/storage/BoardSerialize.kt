package go.storage

import go.model.*


object BoardSerialize : Serializer<Board> {
    override fun serialize(data: Board): String =
        when (data) {
            is BoardPass -> "Pass\n" + getBoardCellsString(data.cells) + "\n" + data.player + "\n" + data.currPoints.white + " "+ data.currPoints.black
            is BoardRun -> "Run\n" + getBoardCellsString(data.cells) + "\n" + data.player + "\n" + data.currPoints.white + " "+ data.currPoints.black
            is BoardFinish -> "Finish\n" + getBoardCellsString(data.cells) + "\n" + data.score.white +" "+ data.score.black
        }


    private fun getBoardCellsString(cells: BoardCells): String {
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
                val scores = lines[2].split(" ").let { Points(it[0].toDouble(), it[1].toDouble()) }
                BoardFinish(cells, scores)
            }
            else -> throw IllegalArgumentException("Invalid board type")
        }
    }

    private fun deserializeBoardCells(cellsString: String): BoardCells {

        val cells = mutableListOf<Pair<Position, Stone>>()

        if(!cellsString.any{it == ':'}) return cells.toMap()

        val cellList = cellsString.split(" ")

        for (cell in cellList) {
            val (index, stoneStr) = cell.split(":")
            val position = Position(index.toInt())
            val stone = Stone.valueOf(stoneStr)
            cells.add(position to stone)
        }

        return cells.toMap()
    }


}
