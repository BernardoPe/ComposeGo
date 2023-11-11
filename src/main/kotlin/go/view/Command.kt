package go.view

import go.model.*
import go.storage.Storage
import go.storage.TextFileStorage
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


/**
 * Represents Commands for the User Interface.
 */
abstract class Command {
    open fun execute(args : List<String>, game : Board): Board = throw IllegalStateException("Game Over")
    open val isToFinish = false
}



/**
 * Returns the list of commands.
 * @param storage The Storage to laod and save files from.
 */
fun getCommands(storage: TextFileStorage<String, Board>) : Map<String,Command> {
    return mapOf (
        "PLAY" to object: Command() {

            override fun execute(args: List<String>, game: Board): Board {
                require(game !is BoardFinish) { "Game Over" }
                val arg = requireNotNull(args.firstOrNull()) { "Missing Position" }
                val pos = getPosition(arg)
                require(game.cells[pos] == null) { "Position ${arg.uppercase()} used" }
                return game.play(pos)
            }

        },
        "PASS" to object: Command() {

            override fun execute(args: List<String>, game : Board): Board {
                return game.pass()
            }

        },
        "NEW" to object : Command() {

            override fun execute(args: List<String>, game : Board): Board {
                return newBoard()
            }

        },
        "SAVE" to object: Command() {

            override fun execute(args: List<String>, game: Board): Board{
                require(args.isNotEmpty()) { "Missing name" }
                requireNotNull( if (game == newBoard()) null else game ){ "Game not started" }
                val name = args[0]
                require(name.isNotEmpty()) { "Name must not be empty" }
                storage.create(name, game)
                return game

            }

        },
        "LOAD" to object: Command() {
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