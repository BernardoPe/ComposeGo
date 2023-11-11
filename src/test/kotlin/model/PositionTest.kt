package model

import go.model.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import kotlin.test.*

class PositionTest {

    @Test
    fun testPositionCreationByIndex() {
        when(BOARD_SIZE) {
            BoardDimension.SMALL -> {
                val position = Position(0)
                assertEquals(0, position.index)
                assertEquals(0, position.row)
                assertEquals(0, position.col)
                assertFailsWith<IllegalArgumentException> { Position(90) }
            }
            BoardDimension.MEDIUM -> {
                val position = Position(168)
                assertEquals(168, position.index)
                assertEquals(12, position.row)
                assertEquals(12, position.col)
                assertFailsWith<IllegalArgumentException> { Position(180) }
            }
            BoardDimension.LARGE -> {
                val position = Position(360)
                assertEquals(360, position.index)
                assertEquals(18, position.row)
                assertEquals(18, position.col)
                assertFailsWith<IllegalArgumentException> { Position(380) }
            }
        }

    }

    @Test
    fun testPositionCreationByRowAndCol() {
        when(BOARD_SIZE) {
            BoardDimension.SMALL -> {
                val position = Position(1,  2)
                assertEquals(11, position.index)
                assertEquals(1, position.row)
                assertEquals(2, position.col)
                assertFailsWith<IllegalArgumentException> { Position(3,13) }
                assertFailsWith<IllegalArgumentException> { Position(10,3) }
            }
            BoardDimension.MEDIUM -> {
                val position = Position(9,9)
                assertEquals(126, position.index)
                assertEquals(9, position.row)
                assertEquals(9, position.col)
                assertFailsWith<IllegalArgumentException> { Position(10,13) }
                assertFailsWith<IllegalArgumentException> { Position(15,10) }
            }
            BoardDimension.LARGE -> {
                val position = Position(15,12)
                assertEquals(297, position.index)
                assertEquals(15, position.row)
                assertEquals(12, position.col)
                assertFailsWith<IllegalArgumentException> { Position(19,13) }
                assertFailsWith<IllegalArgumentException> { Position(15,21) }
            }
        }

    }

    @Test
    fun testGetAdjacents() {
        val position = Position(1, 1)
        val adjacents = position.getAdjacents()
        assertTrue { adjacents.size == 4 }
        assertTrue(Position(0, 1) in adjacents)
        assertTrue(Position(2, 1) in adjacents)
        assertTrue(Position(1, 0) in adjacents)
        assertTrue(Position(1, 2) in adjacents)
    }

    @Test
    fun testGetAdjacentsCorner() {
        val position = Position(0, 0)
        val adjacents = position.getAdjacents()
        assertTrue { adjacents.size == 2 }
        assertTrue(Position(0, 1) in adjacents)
        assertTrue(Position(1, 0) in adjacents)
    }

    @Test
    fun testGetAdjacentsEdge() {
        val position = Position(0, 4)
        val adjacents = position.getAdjacents()
        assertTrue { adjacents.size == 3 }
        assertTrue(Position(1, 4) in adjacents)
        assertTrue(Position(0, 3) in adjacents)
        assertTrue(Position(0, 5) in adjacents)
    }

    @Test
    fun testComputeGroup() {
        val board = mutableMapOf(
            Position(0,0) to Stone.BLACK,
            Position(0,1) to Stone.BLACK,
            Position(1,1) to Stone.BLACK,
            Position(2,0) to Stone.BLACK,
            Position(0,2) to Stone.WHITE,
            Position(1,2) to Stone.WHITE,
            Position(2,1) to Stone.WHITE
        )

        var group = Position(0,0).getPosGroup(board, Stone.BLACK)

        assertTrue { group.size == 3 }
        assertTrue(Position(0,0) in group.positions)
        assertTrue(Position(0,1) in group.positions)
        assertTrue(Position(1,1) in group.positions)

        group = Position(2,0).getPosGroup(board, Stone.BLACK)
        assertTrue { group.size == 1 }
        assertTrue(Position(2,0) in group.positions)


        group = Position(2,1).getPosGroup(board, Stone.WHITE)
        assertTrue { group.size == 1 }
        assertTrue(Position(2,1) in group.positions)


        group = Position(0,2).getPosGroup(board, Stone.WHITE)
        assertTrue { group.size == 2 }
        assertTrue(Position(0,2) in group.positions)
        assertTrue(Position(1,2) in group.positions)

        group = Position(1,0).getPosGroup(board, null)
        assertTrue { group.size == 1 }
        assertTrue(Position(1,0) in group.positions)

        group = Position(0,3).getPosGroup(board, null)
        assertTrue { group.size == BOARD_SIZE.size * BOARD_SIZE.size  - 8 }

        group = Position(0,3).getPosGroup(board, Stone.WHITE)
        assertTrue { group.size == 0 }

        group = Position(2,1).getPosGroup(board, null)
        assertTrue { group.size == 0 }

        group = Position(2,0).getPosGroup(board, Stone.WHITE)
        assertTrue { group.size == 0 }

    }

    @Test
    fun testStringRepresentation() {
        when(BOARD_SIZE) {
            BoardDimension.SMALL -> {
                var position = Position(2, 3)
                assertEquals("D7", position.String())
                position = Position(0, 0)
                assertEquals("A9", position.String())
            }
            BoardDimension.MEDIUM -> {
                var position = Position(11, 8)
                assertEquals("I2", position.String())
                position = Position(9, 12)
                assertEquals("M4", position.String())
            }
            BoardDimension.LARGE -> {
                var position = Position(13, 15)
                assertEquals("P6", position.String())
                position = Position(16, 13)
                assertEquals("N3", position.String())
            }
        }

    }

    @Test
    fun testIsValidCol() {
        when(BOARD_SIZE) {
            BoardDimension.SMALL -> {
                assertTrue(isValidCol('A'))
                assertTrue(isValidCol('C'))
                assertFalse(isValidCol('J'))
            }
            BoardDimension.MEDIUM -> {
                assertTrue(isValidCol('A'))
                assertTrue(isValidCol('E'))
                assertTrue(isValidCol('H'))
                assertFalse(isValidCol('N'))
            }
            BoardDimension.LARGE -> {
                assertTrue(isValidCol('A'))
                assertTrue(isValidCol('E'))
                assertTrue(isValidCol('H'))
                assertTrue(isValidCol('O'))
                assertFalse(isValidCol('T'))
            }
        }

    }

    @Test
    fun testIsValidRow() {
        when(BOARD_SIZE) {
            BoardDimension.SMALL -> {
                assertTrue(isValidRow(1))
                assertTrue(isValidRow(5))
                assertFalse(isValidRow(10))
            }
            BoardDimension.MEDIUM -> {
                assertTrue(isValidRow(2))
                assertTrue(isValidRow(9))
                assertTrue(isValidRow(13))
                assertFalse(isValidRow(14))
            }
            BoardDimension.LARGE -> {
                assertTrue(isValidRow(3))
                assertTrue(isValidRow(5))
                assertTrue(isValidRow(15))
                assertTrue(isValidRow(19))
                assertFalse(isValidRow(20))
            }
        }
    }

    @Test
    fun testIsValidPos() {
        when(BOARD_SIZE) {
            BoardDimension.SMALL -> {
                assertTrue(isValidPos(1,0))
                assertTrue(isValidPos(5,3))
                assertFalse(isValidPos(10,5))
                assertFalse(isValidPos(9,9))
            }
            BoardDimension.MEDIUM -> {
                assertTrue(isValidPos(2,3))
                assertTrue(isValidPos(9,12))
                assertFalse(isValidPos(12,13))
                assertFalse(isValidPos(14,15))
            }
            BoardDimension.LARGE -> {
                assertTrue(isValidPos(3,8))
                assertTrue(isValidPos(5,10))
                assertTrue(isValidPos(15,13))
                assertFalse(isValidPos(19,19))
                assertFalse(isValidPos(20,16))
                assertFalse(isValidPos(15,25))
            }
        }
    }

    @Test
    fun testGetColIndex() {
        assertEquals(0, getColIndex('A'))
        assertEquals(2, getColIndex('C'))
    }

    @Test
    fun testGetRowIndex() {
        assertEquals(BOARD_SIZE.size - 2, getRowIndex(2))
        assertEquals(0, getRowIndex(BOARD_SIZE.size))
    }

    @Test
    fun testGetPosition() {
        assertEquals(Position(0), getPosition("A${BOARD_SIZE.size}"))
        assertEquals(Position(BOARD_SIZE.size - 2, 2), getPosition("C2"))
    }

    @Test
    fun testPositionPlusDirection() {
        var position = Position(1, 1)

        var newPosition = position + Direction.UP
        assertEquals(Position(0, 1), newPosition)

        newPosition = position + Direction.DOWN
        assertEquals(Position(2, 1), newPosition)

        newPosition = position + Direction.LEFT
        assertEquals(Position(1, 0), newPosition)

        newPosition = position + Direction.RIGHT
        assertEquals(Position(1, 2), newPosition)

        position = Position(1,0)

        newPosition = position + Direction.DOWN
        assertEquals(Position(2,0), newPosition)

        newPosition = position + Direction.LEFT
        assertNull(newPosition)

    }
}
