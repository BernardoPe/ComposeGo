package go.model
class Group(val positions : Set<Position>, val player: Stone) {
    val size get() = positions.size
}