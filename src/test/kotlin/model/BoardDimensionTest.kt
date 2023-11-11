package model

import go.model.*
import org.junit.jupiter.api.Test
import kotlin.test.*

class BoardDimensionTest {

    @Test
    fun `toBoardDim() Int to BoardDimension`() {
        assertEquals(BoardDimension.SMALL, 9.toBoardDim())
        assertEquals(BoardDimension.MEDIUM, 13.toBoardDim())
        assertEquals(BoardDimension.LARGE, 19.toBoardDim())
    }

    @Test
    fun `toBoardDim() invalid board size`() {
        assertFailsWith<IllegalArgumentException> { 8.toBoardDim() }
        assertFailsWith<IllegalArgumentException> { 14.toBoardDim() }
        assertFailsWith<IllegalArgumentException> { 20.toBoardDim() }
    }
}