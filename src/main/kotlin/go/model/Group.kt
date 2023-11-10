package go.model
class Group(val positions : Set<Position>, val player: Stone) {
    val size get() = positions.size

    fun hasLiberties(board: BoardCells): Boolean {
        for (pos in positions) {
            pos.getAdjacents().forEach { adjPos ->
                if (board[Position(adjPos.index)] == null) {
                    return true
                }
            }
        }
        return false
    }

    fun getAdjacentGroups(board: BoardCells): Set<Group> {

        val adjacentGroups = mutableSetOf<Group>()
        val checkedPositions = mutableSetOf<Position>()

        for (pos in positions) {
            pos.getAdjacents().forEach { adjPos ->
                if (board[Position(adjPos.index)] != null && board[Position(adjPos.index)] != player) {

                    val adjGroup = adjPos.getPosGroup(board, player.other)

                    if (adjGroup.positions.isNotEmpty()) {
                        if (adjGroup.positions.all { checkedPositions.add(it) }) {
                            adjacentGroups.add(adjGroup)
                        }
                    }

                }
            }
        }
        return adjacentGroups
    }

}