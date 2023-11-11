package model

import go.model.*
import go.view.show
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import kotlin.test.*

class BoardTest {


        @Test
        fun `create new board`() {
            val initialBoard = newBoard()
            assertIs<BoardRun>(initialBoard)
            assertTrue { initialBoard.cells.isEmpty()}
            assertTrue { initialBoard.prevCells.isEmpty()}
            assertTrue { initialBoard.player == Stone.BLACK}
            assertTrue { initialBoard.currPoints.black == 0 && initialBoard.currPoints.white == 0 }
        }

        @Test
        fun `play() board state changes`() {

            val initialBoard = newBoard()
            val updatedBoard = initialBoard.play(Position(0, 0))

            assertIs<BoardRun>(updatedBoard)
            assertIs<BoardRun>(updatedBoard.play(Position(1,2)))

            val passedBoard = BoardPass(emptyMap(), emptyMap(), Stone.BLACK, Points(0,0))
            assertIs<BoardRun>(passedBoard.play(Position(1,2)))

            assertFailsWith<IllegalStateException> { BoardFinish(emptyMap(), Points(0.0, 0.0)).play(Position(5)) }

        }

        @Test
        fun `pass() board state changes`() {

            val initialBoard = newBoard()

            val passedBoard = initialBoard.pass()
            assertIs<BoardPass>(passedBoard)

            val finishedBoard = passedBoard.pass()
            assertFailsWith<IllegalStateException> { finishedBoard.pass() }


        }


        @Test
        fun `emptyAreas single area`() {
            var board = mapOf<Position, Stone>()

            var emptyAreas = getEmptyAreas(board)
            assertTrue { emptyAreas.size == 1 }
            assertTrue { emptyAreas.first().size == BOARD_SIZE.size * BOARD_SIZE.size }

            board = mapOf(
                Position(1,2) to Stone.WHITE, Position(3,5) to Stone.BLACK,
                Position(4,2) to Stone.WHITE, Position(6,6) to Stone.BLACK,
                Position(7,1) to Stone.BLACK
            )
            emptyAreas = getEmptyAreas(board)

            assertTrue { emptyAreas.size == 1 }
            assertTrue { emptyAreas.first().size == BOARD_SIZE.size * BOARD_SIZE.size - 5 }
        }

        @Test
        fun `emptyAreas full board`() {

            var board = mapOf<Position, Stone>()

            Position.values.forEach {
                board += it to Stone.WHITE
            }

            val emptyAreas = getEmptyAreas(board)

            assertTrue { emptyAreas.isEmpty() }

        }

        @Test
        fun `empty areas multiple areas`() {
            val board = mapOf(
                Position(0,1) to Stone.WHITE, Position(0,4) to Stone.BLACK,
                Position(1,2) to Stone.WHITE, Position(1,3) to Stone.BLACK,  Position(1,5) to Stone.WHITE,
                Position(2,1) to Stone.WHITE, Position(2,5) to Stone.BLACK,
                Position(3,1) to Stone.WHITE, Position(3,2) to Stone.BLACK, Position(3,5) to Stone.BLACK,
                Position(4,0) to Stone.WHITE, Position(4,3) to Stone.BLACK, Position(4,4) to Stone.BLACK,
                Position(5,1) to Stone.WHITE, Position(5,2) to Stone.BLACK, Position(5,5) to Stone.BLACK,
                Position(6,0) to Stone.WHITE, Position(6,3) to Stone.BLACK, Position(6,4) to Stone.BLACK,
            )
            val emptyAreas = getEmptyAreas(board)

            assertTrue { emptyAreas.size == 7 }
            assertTrue { emptyAreas.count { it.size == 2 } == 3 }
            assertTrue { emptyAreas.count { it.size == 5 } == 1 }
            assertTrue { emptyAreas.count { it.size == 1 } == 1 }
            assertTrue { emptyAreas.count { it.size == 6 } == 1 }
            assertTrue { emptyAreas.count { it.size == BOARD_SIZE.size * BOARD_SIZE.size - 37 } == 1}

        }


        @Test
        fun `playerFromEmptyArea() single player area`() {
            val board = mapOf(
                Position(1, 0) to Stone.BLACK,
                Position(1, 1) to Stone.BLACK,
                Position(1, 2) to Stone.BLACK,
                Position(0,2) to Stone.BLACK
            )
            val area = Group(setOf(Position(0,0), Position(0,1)), null)
            val player = playerFromEmptyArea(area, board)

            assertEquals(Stone.BLACK, player)
        }

        @Test
        fun `playerFromEmptyArea() contested area`() {
            val board = mapOf(
                Position(1, 0) to Stone.BLACK,
                Position(1, 2) to Stone.WHITE,
                Position(0, 1) to Stone.BLACK,
                Position(2, 1) to Stone.WHITE,
            )
            val area = Group(setOf(Position(1, 1)), null)
            val player = playerFromEmptyArea(area, board)
            assertEquals(null, player)
        }

        @Test
        fun `playerFromEmptyArea() no plays`() {
            val area = Group(Position.values.toSet(), null)
            val player = playerFromEmptyArea(area, mapOf())
            assertEquals(null, player)
        }

        @Test
        fun `calculateFinalScore multiple empty areas, all contested`() {

            val board = mapOf(
                Position(0,1) to Stone.BLACK, Position(0,4) to Stone.BLACK,
                Position(1,2) to Stone.WHITE, Position(1,3) to Stone.BLACK,  Position(1,5) to Stone.WHITE,
                Position(2,1) to Stone.WHITE, Position(2,5) to Stone.BLACK,
                Position(3,1) to Stone.WHITE, Position(3,2) to Stone.BLACK, Position(3,5) to Stone.BLACK,
                Position(4,0) to Stone.WHITE, Position(4,3) to Stone.BLACK, Position(4,4) to Stone.BLACK,
                Position(5,1) to Stone.WHITE, Position(5,2) to Stone.BLACK, Position(5,5) to Stone.BLACK,
                Position(6,0) to Stone.BLACK, Position(6,3) to Stone.WHITE, Position(6,4) to Stone.BLACK,
            )

            val points = Points(3,6)
            val expectedPoints = Points(3.0,points.black - BOARD_SIZE.komi)

            assertEquals(expectedPoints.black, calculateFinalScore(board, points).black)
            assertEquals(expectedPoints.white, calculateFinalScore(board,points).white)

        }

        @Test
        fun `calculateFinalScore multiple empty areas, some contested, some not`() {

            val board = mapOf(
                Position(0,1) to Stone.WHITE, Position(0,4) to Stone.BLACK,
                Position(1,2) to Stone.WHITE, Position(1,3) to Stone.BLACK,  Position(1,5) to Stone.WHITE,
                Position(2,1) to Stone.WHITE, Position(2,5) to Stone.BLACK,
                Position(3,1) to Stone.WHITE, Position(3,2) to Stone.BLACK, Position(3,5) to Stone.BLACK,
                Position(4,0) to Stone.WHITE, Position(4,3) to Stone.BLACK, Position(4,4) to Stone.BLACK,
                Position(5,1) to Stone.WHITE, Position(5,2) to Stone.BLACK, Position(5,5) to Stone.BLACK,
                Position(6,0) to Stone.WHITE, Position(6,3) to Stone.BLACK, Position(6,4) to Stone.BLACK,
            )

            val points = Points(5,3)
            val expectedPoints = Points((points.white + 6).toDouble(), points.black + 2 - BOARD_SIZE.komi)

            assertEquals(expectedPoints.black, calculateFinalScore(board, points).black)
            assertEquals(expectedPoints.white, calculateFinalScore(board,points).white)

        }

        @Test
        fun `calculateFinalScore only one play`() {

            var board = mapOf(
                Position(0,1) to Stone.BLACK,
            )

            val points = Points(0,0)
            var expectedPoints = Points(0.0,points.black + (BOARD_SIZE.size * BOARD_SIZE.size) - 1 - BOARD_SIZE.komi)

            assertEquals(expectedPoints.black, calculateFinalScore(board, points).black)
            assertEquals(expectedPoints.white, calculateFinalScore(board,points).white)

            board = mapOf(
                Position(0,5) to Stone.WHITE
            )

            expectedPoints = Points(0.0 + (BOARD_SIZE.size * BOARD_SIZE.size) - 1,points.black  - BOARD_SIZE.komi)

            assertEquals(expectedPoints.white, calculateFinalScore(board,points).white)
            assertEquals(expectedPoints.black, calculateFinalScore(board, points).black)


        }

        @Test
        fun `calculateFinalScore dead zone`() {

            val board = mapOf(
                Position(0,1) to Stone.BLACK, Position(0,3) to Stone.WHITE,
                Position(1,0) to Stone.BLACK, Position(1,1) to Stone.BLACK, Position(1,3) to Stone.WHITE,
                Position(2,3) to Stone.WHITE,
                Position(3,0) to Stone.WHITE, Position(3,1) to Stone.WHITE, Position(3,2) to Stone.WHITE, Position(3,3) to Stone.WHITE,
                Position(4,5) to Stone.BLACK
            )
            val points = Points(0,0)
            val expectedPoints = Points(0.0,1 - BOARD_SIZE.komi)

            assertEquals(expectedPoints.black, calculateFinalScore(board, points).black)
            assertEquals(expectedPoints.white, calculateFinalScore(board, points).white)

        }



        @Test
        fun `validatePlay() ko rule`() {
            val board = mapOf(
                Position(2,2) to Stone.WHITE, Position(2,3) to Stone.BLACK,
                Position(3,1) to Stone.WHITE, Position(3,2) to Stone.BLACK, Position(3,4) to Stone.BLACK,
                Position(4,2) to Stone.WHITE, Position(4,3) to Stone.BLACK,
            )

            var run = BoardRun(board, emptyMap(), Stone.WHITE, Points(0,0))

            run = run.validatePlay(Position(3,3)) as BoardRun

            assertEquals( 1, run.currPoints.white)
            assertEquals(0, run.currPoints.black)
            assertNull(run.cells[Position(3,2)])
            assertFailsWith<IllegalArgumentException> { run.validatePlay(Position(3,2)) }

            run = run.validatePlay(Position(3,6)) as BoardRun
            run = run.validatePlay(Position(3,7)) as BoardRun

            run = run.validatePlay(Position(3,2)) as BoardRun

            assertEquals( 1, run.currPoints.white)
            assertEquals(1, run.currPoints.black)
            assertNull(run.cells[Position(3,3)])

        }

        @Test
        fun `validatePlay() liberty rule and capture`() {
            var board = mapOf(
                Position(0,1) to Stone.WHITE, Position(1,0) to Stone.WHITE,
            )
            var run = BoardRun(board, emptyMap(), Stone.BLACK, Points(0,0))
            assertFailsWith<IllegalArgumentException> { run.validatePlay(Position(0,0)) }

            board = mapOf(
                Position(1,2) to Stone.WHITE, Position(1,3) to Stone.WHITE,
                Position(2,1) to Stone.WHITE, Position(2,2) to Stone.BLACK, Position(2,3) to Stone.BLACK, Position(2,4) to Stone.WHITE,
                Position(3,1) to Stone.WHITE, Position(3,2) to Stone.BLACK, Position(3,4) to Stone.WHITE,
                Position(4,2) to Stone.WHITE, Position(4,3) to Stone.WHITE,
            )

            run = BoardRun(board, emptyMap(), Stone.BLACK, Points(0,0))
            assertFailsWith<IllegalArgumentException> { run.validatePlay(Position(3,3)) }

            board = mapOf(
                Position(1,3) to Stone.WHITE,
                Position(2,1) to Stone.WHITE, Position(2,2) to Stone.BLACK, Position(2,3) to Stone.BLACK, Position(2,4) to Stone.WHITE,
                Position(3,1) to Stone.WHITE, Position(3,2) to Stone.BLACK, Position(3,4) to Stone.WHITE,
                Position(4,2) to Stone.WHITE, Position(4,3) to Stone.WHITE,
            )

            run = BoardRun(board, emptyMap(), Stone.BLACK, Points(0,0))
            run = run.validatePlay(Position(3,3)) as BoardRun
            run = run.validatePlay(Position(1,2)) as BoardRun

            assertEquals( 4, run.currPoints.white)
            assertEquals(0, run.currPoints.black)

            assertNull(run.cells[Position(2,2)])
            assertNull(run.cells[Position(2,3)])
            assertNull(run.cells[Position(3,2)])
            assertNull(run.cells[Position(3,3)])

            assertEquals(8, run.cells.size)

        }

        @Test
        fun `validatePlay() multiple captures with one play`() {
            val board = mapOf(
                Position(0,0) to Stone.WHITE, Position(0,1) to Stone.WHITE, Position(0,2) to Stone.WHITE,
                Position(0,3) to Stone.WHITE, Position(0,4) to Stone.WHITE, Position(0,5) to Stone.WHITE, Position(0,6) to Stone.WHITE,
                Position(1,0) to Stone.WHITE,Position(1,1) to Stone.BLACK, Position(1,2) to Stone.WHITE,  Position(1,3) to Stone.BLACK, Position(1,4) to Stone.WHITE, Position(1,5) to Stone.BLACK, Position(1,6) to Stone.WHITE,
                Position(2,0) to Stone.WHITE, Position(2,1) to Stone.BLACK, Position(2,2) to Stone.WHITE,  Position(2,3) to Stone.BLACK, Position(2,4) to Stone.WHITE, Position(2,5) to Stone.BLACK,
                Position(3,0) to Stone.WHITE, Position(3,1) to Stone.WHITE, Position(3,2) to Stone.WHITE,
                Position(3,3) to Stone.WHITE, Position(3,4) to Stone.WHITE, Position(3,5) to Stone.WHITE, Position(3,6) to Stone.WHITE,
            )
            var run = BoardRun(board, emptyMap(), Stone.WHITE, Points(0,0))
            run = run.validatePlay(Position(2,6)) as BoardRun

            assertEquals( 6, run.currPoints.white)
            assertEquals(0, run.currPoints.black)

            assertNull(run.cells[Position(1,1)])
            assertNull(run.cells[Position(1,3)])
            assertNull(run.cells[Position(1,5)])
            assertNull(run.cells[Position(2,1)])
            assertNull(run.cells[Position(2,3)])
            assertNull(run.cells[Position(2,5)])

            assertEquals(board.size - 5, run.cells.size)


        }
}