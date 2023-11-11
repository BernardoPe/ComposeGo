package model

import go.model.*
import org.junit.jupiter.api.Test
import kotlin.test.*

class GroupTest {

    @Test
    fun testGroupSize() {
        val positions = setOf(Position(0), Position(1), Position(2))
        val group = Group(positions, Stone.BLACK)
        assertEquals(3, group.size)
    }

    @Test
    fun testHasLiberties() {

        var board = mutableMapOf(
            Position(0,0) to Stone.BLACK,
            Position(0,1) to Stone.BLACK,
        )

        var positions = setOf(Position(0), Position(1))
        var group = Group(positions, Stone.BLACK)

        assertTrue(group.hasLiberties(board))

        board += Position(1,0) to Stone.WHITE
        assertTrue(group.hasLiberties(board))

        board += Position(1,1) to Stone.WHITE
        assertTrue(group.hasLiberties(board))

        board += Position(0,2) to Stone.WHITE
        assertFalse { group.hasLiberties(board) }


        board = mutableMapOf(
            Position(0,2) to Stone.WHITE, Position(0,3) to Stone.WHITE,
            Position(1,1) to Stone.WHITE,  Position(1,2) to Stone.BLACK,  Position(1,3) to Stone.BLACK,  Position(1,4) to Stone.WHITE,
            Position(2,1) to Stone.WHITE,  Position(2,2) to Stone.BLACK,  Position(2,3) to Stone.BLACK,  Position(2,4) to Stone.BLACK, Position(2,5) to Stone.WHITE,
            Position(3,2) to Stone.BLACK,  Position(3,3) to Stone.WHITE,  Position(3,4) to Stone.WHITE,
            Position(4,2) to Stone.WHITE,
        )
        positions = setOf(
            Position(1,2), Position(1,3),
            Position(2,2), Position(2,2), Position(2,3), Position(2,4),
            Position(3,2), Position(1,2), Position(1,2)
        )
        group = Group(positions, Stone.BLACK)

        assertTrue { group.hasLiberties(board) }

        board += Position(3,1) to Stone.WHITE

        assertFalse { group.hasLiberties(board) }

    }

    @Test
    fun testGetAdjacentGroups() {

        val board = mutableMapOf(
            Position(0,0) to Stone.BLACK, Position(0,1) to Stone.BLACK, Position(0,2) to Stone.WHITE, Position(0,3) to Stone.WHITE, Position(0,4) to Stone.BLACK,
            Position(1,2) to Stone.WHITE, Position(1,3) to Stone.WHITE,
            Position(2,1) to Stone.BLACK, Position(2,2) to Stone.BLACK, Position(2,3) to Stone.WHITE,
            Position(3,0) to Stone.WHITE, Position(3,1) to Stone.BLACK,  Position(3,2) to Stone.BLACK,
        )

        val group1 = Group(setOf(Position(0,0), Position(0,1)), Stone.BLACK)
        val group2 = Group(setOf(Position(0,2), Position(0,3), Position(1,2), Position(1,3), Position(2,3)), Stone.WHITE)
        val group3 = Group(setOf(Position(0,4)), Stone.BLACK)
        val group4 = Group(setOf(Position(2,1), Position(2,2), Position(3,1), Position(3,2)), Stone.BLACK)
        val group5 = Group(setOf(Position(3,0)), Stone.WHITE)

        BoardRun(board, emptyMap(), Stone.BLACK, Points(0,0))
        var adjacentGroups = group1.getAdjacentGroups(board, Stone.WHITE)

        assertEquals(1, adjacentGroups.size)
        assertTrue { adjacentGroups.any{ group -> group.size == group2.size && group.positions.all { it in group2.positions} }}

        adjacentGroups = group2.getAdjacentGroups(board, Stone.BLACK)
        assertEquals(3, adjacentGroups.size)
        assertTrue { adjacentGroups.any{ group -> group.size == group1.size && group.positions.all { it in group1.positions} }}
        assertTrue { adjacentGroups.any{ group -> group.size == group3.size && group.positions.all { it in group3.positions} }}
        assertTrue { adjacentGroups.any{ group -> group.size == group4.size && group.positions.all { it in group4.positions} }}


        adjacentGroups = group3.getAdjacentGroups(board, Stone.WHITE)
        assertEquals(1, adjacentGroups.size)
        assertTrue { adjacentGroups.any{ group -> group.size == group2.size && group.positions.all { it in group2.positions} }}


        adjacentGroups = group4.getAdjacentGroups(board, Stone.WHITE)
        assertEquals(2, adjacentGroups.size)
        assertTrue { adjacentGroups.any{ group -> group.size == group2.size && group.positions.all { it in group2.positions} }}
        assertTrue { adjacentGroups.any{ group -> group.size == group5.size && group.positions.all { it in group5.positions} }}
    }

}
