package go.model


import go.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias GameStorage = Storage<String, Board>

open class Clash( val gs: GameStorage)

class ClashRun(
    gs: GameStorage,
    val id: String,
    val me: Stone,
    val game: Board
): Clash(gs)


fun Clash.play(toPosition: Position): Clash {
    check(this is ClashRun) { "Clash not started" }
    check((game as BoardRun).player == me) { "Not your turn" }
    val gameAfter = this.game.play(toPosition)
    gs.update(id, gameAfter)
    return ClashRun( gs, id, me, gameAfter )
}

fun Clash.pass(): Clash {
    check(this is ClashRun) { "Clash not started" }
    check((game as BoardRun).player == me) { "Not your turn" }
    val gameAfter = this.game.pass()
    gs.update(id, gameAfter)
    return ClashRun( gs, id, me, gameAfter )
}

fun Clash.startClash(name: String): Clash {
    val game = go.model.newBoard()
    gs.create(name, game)
    return ClashRun( gs, name, Stone.BLACK, game)
}

fun Clash.joinClash(name: String): Clash {
    val game = gs.read(name) ?: error("Clash $name not found")
    return ClashRun( gs, name, Stone.WHITE, game)
}

class NoChangesException : IllegalStateException("No changes")
class GameDeletedException : IllegalStateException("Game deleted")

suspend fun Clash.refreshClash(): Clash {
    check(this is ClashRun) { "Clash not started" }
    val gameAfter = gs.slowRead(id) ?: throw GameDeletedException()
    if (game == gameAfter) throw NoChangesException()
    return ClashRun( gs, id, me, gameAfter )
}

suspend fun GameStorage.slowRead(key: String): Board? {
    val res = withContext(Dispatchers.IO){
        Thread.sleep(1000)
        read(key)
    }
    return res
}

fun Clash.deleteIfIsOwner() {
    if (this is ClashRun && me == Stone.BLACK)
        gs.delete(id)
}

fun Clash.newBoard():Clash  {
    check(this is ClashRun) { "Clash not started" }
    val newGame = go.model.newBoard().also { gs.update(id,it) }
    return ClashRun(gs, id, me, newGame)
}

fun Clash.canNewBoard() = this is ClashRun && game is BoardFinish
