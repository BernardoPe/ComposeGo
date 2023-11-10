/*
package view

import go.model.*
import go.storage.*
import go.view.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.deleteRecursively

@TestInstance(Lifecycle.PER_CLASS)
class TextFileStorageTest {

    @OptIn(ExperimentalPathApi::class)
    @AfterAll
    @BeforeAll
    fun cleanup() {
        val path = Path("storage")
        path.deleteRecursively()
    }

    @Test
    fun createBoard() {
        val textFileStorage = TextFileStorage<String, Board>("storage", BoardSerializer)

        val sut = BoardRun(
            mapOf(
                Position(0) to Stone.Black, Position(1) to Stone.White, Position(2) to Stone.Black,

            ),
            turn = Stone.White
        ).play(Position(8))

        textFileStorage.create("Board1", sut)

        val readData = textFileStorage.read("Board1")
        assertEquals(sut, readData)
    }
}

 */