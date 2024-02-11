package go.view

import go.model.*
import go.storage.Storage
import go.storage.TextFileStorage
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime




/**
 * Creates and returns the list of commands.
 * @param storage The Storage to load and save files from.
 * @return A map of command names to Command objects
 */
fun getCommands(storage: TextFileStorage<String, Board>) : Map<String,Command> {

    return mapOf (
        "PLAY" to Play,
        "PASS" to Pass,
        "NEW" to New,
        "SAVE" to Save(storage),
        "LOAD" to Load(storage),
        "EXIT" to Exit,
    )

}

/**
 * Represents Commands for the User Interface.
 */
abstract class Command {
    open fun execute(args : List<String>, game : Board): Board = throw IllegalStateException("Game Over")
    open val isToFinish = false
}


/**
 * @throws IllegalArgumentException if the game has already ended.
 * @throws IllegalArgumentException if the provided position is invalid or has already been used.
 */
object Play : Command() {
    override fun execute(args: List<String>, game: Board): Board {
        if (game is BoardFinish) error { "Game Over" }
        val arg = requireNotNull(args.firstOrNull()) { "Missing Position" }
        val pos = getPosition(arg)
        return game.play(pos)
    }

}
object Pass : Command() {
    override fun execute(args: List<String>, game : Board): Board {
        return game.pass()
    }

}

object New : Command() {
    override fun execute(args: List<String>, game : Board): Board {
        return newBoard()
    }

}

object Load: Command() {

    /**
     * @throws IllegalArgumentException if the name is missing or if the specified game is not found.
     */
    private var fileStorage : TextFileStorage<String, Board>? = null

    override fun execute(args: List<String>, game: Board): Board {
        requireNotNull(fileStorage) {" Invalid storage "}
        val name = requireNotNull(args.firstOrNull()) { "Missing name" }
        return checkNotNull(fileStorage!!.read(name)) { "Game $name not found" }
    }

    operator fun invoke(storage: TextFileStorage<String, Board>) : Load {
        fileStorage = storage
        return this
    }


}

object Save : Command() {

    /**
     * @throws IllegalArgumentException if the name is missing or empty.
     */
    private var fileStorage : TextFileStorage<String, Board>? = null

    override fun execute(args: List<String>, game: Board): Board{
        requireNotNull(fileStorage) {" Invalid storage "}
        require(args.isNotEmpty()) { "Missing name" }
        val name = args[0]
        require(name.isNotEmpty()) { "Name must not be empty" }
        fileStorage!!.create(name, game)
        return game
    }

    operator fun invoke(storage: TextFileStorage<String, Board>) : Save {
        fileStorage = storage
        return this
    }

}

object Exit : Command() {
    override val isToFinish: Boolean = true
}







<<<<<<< HEAD
            /**
             * @throws IllegalArgumentException if the name is missing or if the specified game is not found.
             */
            override fun execute(args: List<String>, game : Board): Board {
                val name = requireNotNull(args.firstOrNull()) { "Missing name" }
                return checkNotNull(storage.read(name)) { "Game $name not found" }
            }
        },
        "EXIT" to object: Command() {
            override val isToFinish: Boolean = true
        }
    )
}
=======
>>>>>>> 76fdd85 (Final)
