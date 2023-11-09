package go.model

fun main() {
    val pos = Position(0)
    val group = Group(setOf(Position(0), Position(1)), Stone.WHITE)
}

class Group(positions : Set<Position>, player: Stone)