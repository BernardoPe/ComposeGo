package model

import go.model.*
import org.junit.jupiter.api.Test
import kotlin.test.*


class DirectionTest {

    @Test
    fun testUpDirection() {
        val direction = Direction.UP
        assertEquals(0, direction.dx)
        assertEquals(-1, direction.dy)
    }

    @Test
    fun testDownDirection() {
        val direction = Direction.DOWN
        assertEquals(0, direction.dx)
        assertEquals(1, direction.dy)
    }

    @Test
    fun testLeftDirection() {
        val direction = Direction.LEFT
        assertEquals(-1, direction.dx)
        assertEquals(0, direction.dy)
    }

    @Test
    fun testRightDirection() {
        val direction = Direction.RIGHT
        assertEquals(1, direction.dx)
        assertEquals(0, direction.dy)
    }
}