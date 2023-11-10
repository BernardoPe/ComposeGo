package go.model


/**
 * Represents a direction in the Go Grid
 */
enum class Direction(val dx : Int, val dy : Int) {
    UP(0,1),
    DOWN(0,-1),
    LEFT(-1, 0),
    RIGHT(1,0)
}

