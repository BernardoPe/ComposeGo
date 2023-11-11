package model

import go.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class StoneTest {

    @Test
    fun `test Initialization`() {

        val blackStone = Stone.BLACK
        val whiteStone = Stone.WHITE

        assertEquals('#', blackStone.char)
        assertEquals('O', whiteStone.char)

        assertEquals(Stone.WHITE, blackStone.other)
        assertEquals(Stone.BLACK, whiteStone.other)

        assertEquals(Stone.BLACK, "BLACK".toStone())
        assertEquals(Stone.WHITE, "WHITE".toStone())

    }

}