package storage


import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import go.model.*
import go.storage.BoardSerialize.deserialize
import go.storage.BoardSerialize.serialize

class SerializeTest {

    @Test
    fun testSerialize() {
        val cells = mapOf(
            Position(0) to Stone.BLACK,
            Position(1) to Stone.WHITE,
            Position(2) to Stone.BLACK
        )

        val boardPass = BoardPass(cells, emptyMap(), Stone.BLACK, Points(0, 3))
        val boardRun = BoardRun(cells, emptyMap(), Stone.WHITE, Points(5, 0))
        val boardFinish = BoardFinish(cells, Points(10.0, 20.0))

        val expectedPassString = "Pass\n0:BLACK 1:WHITE 2:BLACK\n\nBLACK\n0 3"
        val expectedRunString = "Run\n0:BLACK 1:WHITE 2:BLACK\n\nWHITE\n5 0"
        val expectedFinishString = "Finish\n0:BLACK 1:WHITE 2:BLACK\n10.0 20.0"

        assertEquals(expectedPassString, serialize(boardPass))
        assertEquals(expectedRunString, serialize(boardRun))
        assertEquals(expectedFinishString, serialize(boardFinish))
    }
    @Test
    fun testDeserialize(){

        val board = mapOf(
            Position(17) to Stone.BLACK,
            Position(16) to Stone.BLACK,
            Position(25) to Stone.BLACK,
            Position(72) to Stone.WHITE,
            Position(34) to Stone.BLACK,
            Position(18) to Stone.WHITE,
            Position(43) to Stone.BLACK,
            Position(0) to Stone.WHITE,
            Position(44) to Stone.BLACK
        )

        val boardRunSerialized = "Run\n17:BLACK 16:BLACK 25:BLACK 72:WHITE 34:BLACK 18:WHITE 43:BLACK 0:WHITE 44:BLACK\n\nWHITE\n0 2"
        val deserialized = deserialize(boardRunSerialized)
        val expectedBoardRun =  BoardRun(board, emptyMap(), Stone.WHITE, Points(0, 2))
        assertEquals(expectedBoardRun, deserialized)

    }

}
