import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import go.model.*
import go.storage.*
import go.storage.BoardSerialize.serialize
import go.view.*

class BoardTest {

    @Test
    fun testSerialize() {
        val cells = mapOf(
            Position(0, 0) to Stone.Black,
            Position(1, 1) to Stone.White,
            Position(2, 2) to Stone.Black
        )

        val boardPass = BoardPass(cells, emptyMap(), Stone.Black, Points(0, 0))
        val boardRun = BoardRun(cells, emptyMap(), Stone.White, Points(0, 0))
        val boardFinish = BoardFinish(cells, Points(10f, 20f))

        val expectedPassString = "Pass\n0:BLACK 1:WHITE 2:BLACK\nBLACK\n0 0"
        val expectedRunString = "Run\n0:BLACK 1:WHITE 2:BLACK\nWHITE\n0 0"
        val expectedFinishString = "Finish\n0:BLACK 1:WHITE 2:BLACK\n10.0 20.0"

        assertEquals(expectedPassString, serialize(boardPass))
        assertEquals(expectedRunString, serialize(boardRun))
        assertEquals(expectedFinishString, serialize(boardFinish))
    }
}