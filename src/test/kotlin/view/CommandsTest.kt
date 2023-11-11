package view
import go.model.*
import go.view.*
import go.storage.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException
import kotlin.io.path.*
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommandsTest {


        @OptIn(ExperimentalPathApi::class)
        @AfterAll
        @BeforeAll
        fun cleanup() {
            val path = Path("storage")
            path.deleteRecursively()
        }

        @Test
        fun `play command`() {
            val game = newBoard()
            val storage = TextFileStorage<String, Board>("storage", BoardSerialize)
            val commands = getCommands(storage)

            assertDoesNotThrow { commands["PLAY"]?.execute(listOf("A9"), game) }
            assertFailsWith<IllegalArgumentException>{ commands["PLAY"]?.execute(listOf("1A"), game) }
            assertFailsWith<IllegalArgumentException>{ commands["PLAY"]?.execute(listOf(""), game) }
            assertFailsWith<IllegalArgumentException>{ commands["PLAY"]?.execute(listOf("B 1"), game) }


            when(BOARD_SIZE) {
                BoardDimension.SMALL -> {
                    assertDoesNotThrow { commands["PLAY"]?.execute(listOf("A9"), game) }
                    assertFailsWith<IllegalArgumentException>{ commands["PLAY"]?.execute(listOf("B13"), game) }
                    assertFailsWith<IllegalArgumentException>{ commands["PLAY"]?.execute(listOf("L9"), game) }
                    assertFailsWith<IllegalArgumentException>{ commands["PLAY"]?.execute(listOf("K4"), game) }
                }
                BoardDimension.MEDIUM -> {
                    assertDoesNotThrow { commands["PLAY"]?.execute(listOf("a9"), game) }
                    assertDoesNotThrow { commands["PLAY"]?.execute(listOf("B12"), game) }
                    assertFailsWith<IllegalArgumentException>{ commands["PLAY"]?.execute(listOf("A15"), game) }
                    assertFailsWith<IllegalArgumentException>{ commands["PLAY"]?.execute(listOf("Q15"), game) }
                    assertFailsWith<IllegalArgumentException>{ commands["PLAY"]?.execute(listOf("B25"), game) }
                }
                BoardDimension.LARGE -> {
                    assertDoesNotThrow { commands["PLAY"]?.execute(listOf("S16"), game) }
                    assertDoesNotThrow { commands["PLAY"]?.execute(listOf("Q1"), game) }
                    assertFailsWith<IllegalArgumentException>{ commands["PLAY"]?.execute(listOf("a20"), game) }
                    assertFailsWith<IllegalArgumentException>{ commands["PLAY"]?.execute(listOf("Z15"), game) }
                    assertFailsWith<IllegalArgumentException>{ commands["PLAY"]?.execute(listOf("T3"), game) }
                }
            }

            assertDoesNotThrow { commands["PLAY"]?.execute(listOf("c3"), BoardPass(emptyMap(), emptyMap(), Stone.WHITE, Points(0,0))) }
            assertDoesNotThrow { commands["PLAY"]?.execute(listOf("b5"), BoardRun(emptyMap(), emptyMap(), Stone.WHITE, Points(0,0))) }
            assertFailsWith<IllegalStateException>{ commands["PLAY"]?.execute(listOf("A1"), BoardFinish(emptyMap(), Points(0.0, 0.0))) }

        }


        @Test
        fun `pass command`() {
            val storage = TextFileStorage<String, Board>("storage", BoardSerialize)
            val commands = getCommands(storage)
            assertDoesNotThrow { commands["PASS"]?.execute(emptyList(), newBoard()) }
            assertDoesNotThrow { commands["PASS"]?.execute(emptyList(), BoardRun(emptyMap(), emptyMap(), Stone.WHITE, Points(0,0))) }
            assertFailsWith<IllegalStateException> { commands["PASS"]?.execute(emptyList(), BoardFinish(emptyMap(), Points(0.0,0.0))) }
            assertDoesNotThrow { commands["PASS"]?.execute(emptyList(), BoardPass(emptyMap(), emptyMap(), Stone.WHITE, Points(0,0))) }
        }

        @Test
        fun `new command`() {
            val storage = TextFileStorage<String, Board>("storage", BoardSerialize)
            val commands = getCommands(storage)
            assertDoesNotThrow { commands["NEW"]?.execute(emptyList(), newBoard()) }
            assertDoesNotThrow { commands["NEW"]?.execute(emptyList(), BoardRun(emptyMap(), emptyMap(), Stone.WHITE, Points(0,0))) }
            assertDoesNotThrow { commands["NEW"]?.execute(emptyList(), BoardFinish(emptyMap(), Points(0.0,0.0))) }
            assertDoesNotThrow { commands["NEW"]?.execute(emptyList(), BoardPass(emptyMap(), emptyMap(), Stone.WHITE, Points(0,0))) }
        }

        @Test
        fun `save command`() {
            val storage = TextFileStorage<String, Board>("storage", BoardSerialize)
            val commands = getCommands(storage)
            assertFailsWith<IllegalArgumentException> { commands["SAVE"]?.execute(emptyList(), newBoard()) }
            assertFailsWith<IllegalArgumentException> { commands["SAVE"]?.execute(listOf(""), newBoard()) }
            assertDoesNotThrow { commands["SAVE"]?.execute(listOf("savetest"), newBoard()) }
            val path = Path("storage/savetest.txt")
            assertTrue { path.exists() }
        }

        @Test
        fun `load command`() {
            val storage = TextFileStorage<String, Board>("storage", BoardSerialize)
            val commands = getCommands(storage)
            assertFailsWith<IllegalArgumentException> { commands["LOAD"]?.execute(emptyList(), newBoard()) }
            assertFailsWith<IllegalStateException> { commands["LOAD"]?.execute(listOf("loadtest"), newBoard()) }
            commands["SAVE"]?.execute(listOf("loadtest"), newBoard())
            assertDoesNotThrow { commands["LOAD"]?.execute(listOf("loadtest"), newBoard()) }
        }


}

