package go.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import go.model.*
import go.mongo.MongoDriver
import go.storage.*


import kotlinx.coroutines.*

class AppViewModel(driver: MongoDriver, val scope: CoroutineScope) {

    private val storage = MongoStorage<String, Board>("games", driver, BoardSerialize)
    private var clash by mutableStateOf( Clash(storage))

    var viewLast by mutableStateOf(false)
        private set
    var inputName by mutableStateOf<InputName?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null) //ErrorDialog state
        private set

    private var waitingJob by mutableStateOf<Job?>(null)

    val board: Board? get() = (clash as? ClashRun)?.game
    val captures: Points<Int>? get() {
        val game = (clash as? ClashRun)?.game
        return (game as? BoardRun)?.currPoints
    }

    val score : Points<Double>? get() {
        val game = (clash as? ClashRun)?.game
        return (game as? BoardFinish)?.score
    }

    val me: Stone? get() = (clash as? ClashRun)?.me
    val isRunning: Boolean get() = board is BoardRun

    val isFinished: Boolean get() = board is BoardFinish

    val newAvailable: Boolean get() = clash.canNewBoard()

    val lastPlay: Position? get() {
        return board?.cells?.keys?.lastOrNull()
    }
    val isWaiting: Boolean get() = waitingJob != null
    private val turnAvailable: Boolean
        get() = (board as? BoardRun)?.player == me || newAvailable

    fun setShowLast(show: Boolean) {
        viewLast = show
    }

    fun hideError() { errorMessage = null }

    fun play(pos: Position){
        try {
            clash = clash.play(pos)
        } catch (e: Exception) {
            errorMessage = e.message
        }
        waitForOtherSide()
    }

    enum class InputName(val txt: String) {
        NEW("Start"), JOIN("Join"), SCORE("Score"), CAPTURES("Captures")
    }

    fun showNewGameDialog() { inputName = InputName.NEW }
    fun showJoinGameDialog() { inputName = InputName.JOIN }
    fun showScoreDialog() { inputName = InputName.SCORE }
    fun showCapturesDialog() { inputName = InputName.CAPTURES }
    fun cancelInput() { inputName = null }
    fun newGame(gameName: String) {
        cancelWaiting()
        try {
            clash = clash.startClash(gameName)
        } catch (e: Exception) {
            errorMessage = "Game ${gameName} already exists"
        }

        inputName = null
    }

    fun joinGame(gameName: String) {
        cancelWaiting()
        try {
            clash = clash.joinClash(gameName)
        } catch (e: Exception) {
            errorMessage = e.message
        }
        inputName = null
        waitForOtherSide()
    }

    fun exit() {
        clash.deleteIfIsOwner()
        cancelWaiting()
    }

    private fun cancelWaiting() {
        waitingJob?.cancel()
        waitingJob = null
    }

    private fun waitForOtherSide() {
        if (turnAvailable) return
        waitingJob = scope.launch(Dispatchers.IO) {
            do {
                delay(100)
                try { clash = clash.refreshClash() }
                catch (e: NoChangesException) {  }
                catch (e: Exception) {
                    errorMessage = e.message
                    if (e is GameDeletedException) clash = Clash(storage)
                }
            } while (!turnAvailable)
            waitingJob = null
        }
    }
    fun passTurn() {
        try {
            clash = clash.pass()
        } catch (e: Exception) {
            errorMessage = e.message
        }
        waitForOtherSide()
    }


}