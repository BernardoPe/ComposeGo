package go

import go.model.*
import go.storage.BoardSerialize
import go.storage.TextFileStorage
import go.view.getCommands
import go.view.readCmdLine
import go.view.show

fun main() {
    var board: Board = newBoard()
    val storage = TextFileStorage<String, Board>("saves", BoardSerialize)
    val commands = getCommands(storage)
    while (true) {
        board.show()
        val (name, args) = readCmdLine()
        val cmd = commands[name]
        if(cmd == null) println("Invalid Command $name")
        else {
            try{
                if(cmd.isToFinish) break
                board = cmd.execute(args, board)
            } catch (e: IllegalStateException) {
                println(e.message)
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }
}