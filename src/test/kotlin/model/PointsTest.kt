package model

import go.model.*
import org.junit.jupiter.api.Test
import kotlin.test.*


class PointsTest {

        @Test
        fun `test Initialization`() {

            val intPoints = Points(0,5)
            val doublePoints = Points(3.0, 5.5)

            assertIsNot<Double>(intPoints.black)
            assertIsNot<Double>(intPoints.white)

            assertEquals(0, intPoints.white)
            assertEquals(5, intPoints.black)

            assertIsNot<Int>(doublePoints.black)
            assertIsNot<Int>(doublePoints.white)

            assertEquals(3.0, doublePoints.white)
            assertEquals(5.5, doublePoints.black)

        }


}