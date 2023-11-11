package storage

import go.model.*
import go.storage.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions.*
import java.lang.IllegalStateException
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.deleteRecursively
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TextFileStorageTest {

    private val storageDirectory = "storage"

    @OptIn(ExperimentalPathApi::class)
    @AfterAll
    @BeforeAll
    fun cleanup() {
        val path = Path("storage")
        path.deleteRecursively()
    }

    @Test
    fun createAndReadBoard() {

        val textFileStorage = TextFileStorage<String, Board>("storage", BoardSerialize)

        val initialBoardState = mapOf(
            Position(0) to Stone.BLACK,
            Position(1) to Stone.WHITE,
            Position(2) to Stone.BLACK
        )

        val boardRun = BoardRun(initialBoardState, emptyMap(), Stone.WHITE, Points(0,0) )

        textFileStorage.create("createAndReadTest", boardRun)

        val readData = textFileStorage.read("createAndReadTest")

        assertEquals(boardRun, readData)

    }


    @Test
    fun updateBoard() {

        val textFileStorage = TextFileStorage<String, Board>(storageDirectory, BoardSerialize)

        val initialBoardState = mapOf(
            Position(0) to Stone.BLACK,
            Position(1) to Stone.WHITE,
            Position(2) to Stone.BLACK

        )

        val boardRun = BoardRun(initialBoardState, emptyMap(), Stone.WHITE, Points(0, 0))

        textFileStorage.create("updateTest", boardRun)

        val retrievedBoard = textFileStorage.read("updateTest")
        assertNotNull(retrievedBoard)

        val newBoard = newBoard()
        textFileStorage.update("updateTest", newBoard)

        val readData = textFileStorage.read("updateTest")
        assertNotEquals(retrievedBoard, readData)
    }


    @Test
    fun deleteBoard() {
        val textFileStorage = TextFileStorage<String, Board>(storageDirectory, BoardSerialize)

        val initialBoardState = mapOf(
            Position(0) to Stone.BLACK,
            Position(1) to Stone.WHITE,
            Position(2) to Stone.BLACK
        )

        val boardRun = BoardRun(initialBoardState,  emptyMap(), Stone.WHITE, Points(0,0))

        textFileStorage.create("deleteTest", boardRun)

        textFileStorage.delete("deleteTest")

        assertFailsWith<IllegalStateException> { textFileStorage.read("deleteTest") }

    }


}