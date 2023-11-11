import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import go.model.*
import go.storage.BoardSerialize.serialize

class BoardTest {

    @Test
    fun `test Serializer`() {
        val cells = mapOf(
            Position(0) to Stone.BLACK,
            Position(1) to Stone.WHITE,
            Position(2) to Stone.BLACK
        )

        val boardPass = BoardPass(cells, emptyMap(), Stone.BLACK, Points(0, 3))
        val boardRun = BoardRun(cells, emptyMap(), Stone.WHITE, Points(5, 0))
        val boardFinish = BoardFinish(cells, Points(10.0, 20.0))

        val expectedPassString = "Pass\n0:BLACK 1:WHITE 2:BLACK\nBLACK\n0 3"
        val expectedRunString = "Run\n0:BLACK 1:WHITE 2:BLACK\nWHITE\n5 0"
        val expectedFinishString = "Finish\n0:BLACK 1:WHITE 2:BLACK\n10.0 20.0"

        assertEquals(expectedPassString, serialize(boardPass))
        assertEquals(expectedRunString, serialize(boardRun))
        assertEquals(expectedFinishString, serialize(boardFinish))
    }

}