package go.storage

import go.model.*

/**
 * Implements the Serializer interface
 */
object BoardSerialize : Serializer<Board> {

    /**
     * Serializes a Board object into a string representation.
     * @param data represents the Board object to be serialized.
     * @return String representation of the serialized Board object.
     */
    override fun serialize(data: Board): String =
        when (data) {
            is BoardPass -> "Pass\n" + getBoardCellsString(data.cells) + "\n" + getBoardCellsString(data.prevCells) + "\n" + data.player + "\n" + data.currPoints.white + " "+ data.currPoints.black
            is BoardRun -> "Run\n" + getBoardCellsString(data.cells) + "\n" + getBoardCellsString(data.prevCells) + "\n" + data.player + "\n" + data.currPoints.white + " "+ data.currPoints.black
            is BoardFinish -> "Finish\n" + getBoardCellsString(data.cells) + "\n" + data.score.white +" "+ data.score.black
        }

    /**
     * Serializes the BoardCells into a string.
     * @param cells represents the BoardCells to be serialized.
     * @return String representation of the serialized BoardCells.
     */

    private fun getBoardCellsString(cells: BoardCells): String {
        val result = StringBuilder()

        for ((pos, stone) in cells) {
            result.append("${pos.index}:$stone ")
        }

        return result.toString().trim()
    }

    /**
     * Deserializes a string into a Board object.
     * @param text represents the serialized string of the Board.
     * @return deserialized Board object.
     * @throws IllegalArgumentException if the board type is invalid
     */
    override fun deserialize(text: String): Board {
        val lines = text.split("\n")
        val type = lines[0].trim()
        val cellsString = lines[1].trim()
        val cells = deserializeBoardCells(cellsString)

        return when (type) {
            "Pass" -> {
                val prevCells = deserializeBoardCells(lines[2].trim())
                val turn = lines[3].trim().toStone()
                val points = lines[4].split(" ").let { Points(it[0].toInt(), it[1].toInt()) }
                BoardPass(cells, prevCells, turn, points)
            }
            "Run" -> {
                val prevCells = deserializeBoardCells(lines[2].trim())
                val turn = lines[3].trim().toStone()
                val points = lines[4].split(" ").let { Points(it[0].toInt(), it[1].toInt()) }
                BoardRun(cells, prevCells, turn, points)
            }
            "Finish" -> {
                val scores = lines[2].split(" ").let { Points(it[0].toDouble(), it[1].toDouble()) }
                BoardFinish(cells, scores)
            }
            else -> throw IllegalArgumentException("Invalid board type")
        }
    }

    /**
     * Deserializes a string representation of BoardCells into a BoardCells object.
     * @param cellsString represents the string of BoardCells
     * @return deserialized BoardCells object.
     */

    private fun deserializeBoardCells(cellsString: String): BoardCells {

        var cells = mapOf<Position, Stone>()

        if(!cellsString.any{it == ':'}) return cells

        val cellList = cellsString.split(" ")

        for (cell in cellList) {
            val (index, stoneStr) = cell.split(":")
            val position = Position(index.toInt())
            val stone = Stone.valueOf(stoneStr)
            cells = cells + (position to stone)
        }

        return cells
    }


}
