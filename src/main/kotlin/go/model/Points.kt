package go.model


/**
 * Represents the points in the Go game
 */
class Points<T>(val white : T, val black : T) {
    override fun equals(other : Any?) = other is Points<*> && white == other.white && black == other.black

}