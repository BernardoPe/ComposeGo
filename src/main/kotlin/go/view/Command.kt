package go.view

import go.model.*
import java.util.*

abstract class Command(val arg : String = "") {
    open fun execute(args : List<String>, game : Board): Board = throw IllegalStateException("Game Over")
    open val isToFinish = false
}

object Play: Command("play") {
    override fun execute(args: List<String>, game : Board) : Board {
        val arg = requireNotNull(args.firstOrNull()){"Missing Position"}
        val pos = getPosition(arg)
        requireNotNull(game.cells[pos]) {"Position ${arg.uppercase()} used"}
        return game.play(pos)
    }
}

fun getCommands(storage: Storage<String, Board>) : Map<String,Command> {
    return mapOf<String, Command> (
        "PLAY" to Play,
        "PASS" to object: Command() {
            override fun execute(args: List<String>, game : Board): Board {
                return game.pass()
            }
        },
        "NEW" to object : Command() {
            override fun execute(args: List<String>, game : Board): Board {
                TODO()
            }
        },
        "SAVE" to object: Command() {
            override fun execute(args: List<String>, game : Board): Board {
                TODO()
            }
        },
        "LOAD" to object: Command() {
            override fun execute(args: List<String>, game : Board): Board {
                TODO()
            }
        },
        "EXIT" to object: Command() {
            override val isToFinish: Boolean = true
        }
    )
}