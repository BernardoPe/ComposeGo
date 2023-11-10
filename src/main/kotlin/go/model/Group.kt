package go.model


/**
 * Represents a group of Positions belonging to a player in the Go board
 * @property positions the group's positions
 * @property player the player this group belongs to. May be null to represent a group of empty cells
 * @property size the amount of positions belonging to this group
 */
class Group(val positions : Set<Position>, val player: Stone?) {
    val size get() = positions.size


    /**
     * Checks if a group has any remaining liberties in the [board]
     * @param board the board to check
     */
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

    /**
     * Returns the adjacent groups belonging to [type]
     * @param board the board to check
     * @param type The type of cell, may be null to return adjacent empty groups.
     */
    fun getAdjacentGroups(board: BoardCells, type : Stone?): Set<Group> {

        var adjacentGroups = setOf<Group>()

        for (pos in positions) {
            pos.getAdjacents().forEach { adjPos ->
                if (board[adjPos] == type && adjacentGroups.all{ !it.positions.contains(adjPos) }) {
                    val adjGroup = adjPos.getPosGroup(board, board[adjPos])
                    if (adjGroup.positions.isNotEmpty()) {
                        adjacentGroups = adjacentGroups + adjGroup
                    }
                }
            }
        }
        return adjacentGroups
    }

}

