package go

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import go.model.*
import go.mongo.MongoDriver
import go.viewmodel.AppViewModel
import go.viewmodel.AppViewModel.InputName

val CELL_SIDE = 40.dp
val GRID_THICKNESS = 2.dp
val BOARD_SIDE = CELL_SIDE * BOARD_SIZE.size + CELL_SIDE


@Composable
@Preview
fun FrameWindowScope.App(driver: MongoDriver, exitFunction: () -> Unit) {
    val scope = rememberCoroutineScope()
    val vm = remember { AppViewModel(driver, scope) }

    MenuBar  {
        Menu("Game") {
            Item("New Game", onClick = vm::showNewGameDialog)
            Item("Join Game", onClick = vm::showJoinGameDialog)
            Item("Exit", onClick = { vm.exit(); driver.close(); exitFunction() })
        }
        Menu("Play") {
            Item("Pass", onClick = vm::passTurn, enabled = vm.isRunning)
            Item("Captures", onClick = vm::showCapturesDialog, enabled = vm.isRunning)
            Item("Score", onClick = vm::showScoreDialog, enabled = vm.isFinished)
        }
        Menu("Options") {
            CheckboxItem("Show Last", checked = vm.viewLast, onCheckedChange = { vm.setShowLast(it) } )
        }
    }
    MaterialTheme {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BoardView(
                boardCells = vm.board?.cells,
                viewLast = vm.viewLast,
                lastPlay = vm.lastPlay,
                onClick = vm::play)
            StatusBar(vm.board, vm.me)
        }
        vm.inputName?.let{
            when(it) {
                InputName.SCORE -> ScoreDialog(
                    score = vm.score,
                    type = InputName.CAPTURES,
                    closeDialog = vm::cancelInput,
                )
                InputName.CAPTURES -> CapturesDialog(
                    score = vm.captures,
                    type = InputName.CAPTURES,
                    closeDialog = vm::cancelInput,
                )
                else -> StartOrJoinDialog(
                    type = it,
                    onCancel = vm::cancelInput,
                    onAction = if(it==InputName.NEW) vm::newGame else vm::joinGame
                )
            }

        }
        vm.errorMessage?.let { ErrorDialog(it, onClose = vm::hideError) }
        if(vm.isWaiting) waitingIndicator()
    }
}

@Composable
fun waitingIndicator() = CircularProgressIndicator(
    Modifier.fillMaxSize().padding(30.dp),
    strokeWidth = 15.dp
)


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DialogBase(
    title: String,
    onClose: ()->Unit,
    content: @Composable ()->Unit
) = AlertDialog(
    onDismissRequest = onClose,
    title = { Text(title, style = MaterialTheme.typography.h4) },
    text = content,
    confirmButton = {  TextButton(onClick = onClose , modifier = Modifier.size(50.dp)){
        Image(painter = painterResource("x.png"), contentDescription = null, contentScale = ContentScale.FillBounds)
    } }
)


@Composable
fun ErrorDialog(message: String, onClose: ()->Unit) =
    DialogBase("", onClose) {
        Text(message, style = MaterialTheme.typography.h6)
    }
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StartOrJoinDialog(
    type: InputName,
    onCancel: ()->Unit,
    onAction: (String)->Unit) {

    var name by remember { mutableStateOf("") }

    AlertDialog(
        title = { Text("${type.txt} Game") },
        onDismissRequest = onCancel,
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Game Name") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onAction (name) }, modifier = Modifier.size(50.dp)){
                Image(painter = painterResource("tick.png"), contentDescription = null, contentScale = ContentScale.FillBounds)
            }
        },
        dismissButton = {
            TextButton(onClick=onCancel, modifier = Modifier.size(50.dp)){
                Image(painter = painterResource("x.png"), contentDescription = null, contentScale = ContentScale.FillBounds)
            }
        }
    )

}


@OptIn(ExperimentalMaterialApi::class, ExperimentalStdlibApi::class)
@Composable
fun CapturesDialog(score: Points<Int>?, type: InputName, closeDialog: () -> Unit) =
    AlertDialog(
        onDismissRequest = closeDialog,
        confirmButton = { TextButton(onClick=closeDialog, modifier = Modifier.size(50.dp)){
            Image(painter = painterResource("x.png"), contentDescription = null, contentScale = ContentScale.FillBounds)
        } },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column( horizontalAlignment = Alignment.CenterHorizontally){
                    Stone.entries.forEach { player ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Cell(player, size = 20.dp)
                            Text(
                                text = if (player == Stone.WHITE) " - ${score?.white ?: 0}" else  " - ${score?.black ?: 0}",
                                style = MaterialTheme.typography.h4
                            )
                        }
                    }
                }
            }
        }
    )

@OptIn(ExperimentalMaterialApi::class, ExperimentalStdlibApi::class)
@Composable
fun ScoreDialog(score: Points<Double>?, type: InputName, closeDialog: () -> Unit) =
    AlertDialog(
        onDismissRequest = closeDialog,
        confirmButton = { TextButton(onClick=closeDialog, modifier = Modifier.size(50.dp)){
            Image(painter = painterResource("x.png"), contentDescription = null, contentScale = ContentScale.FillBounds)
        } },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column( horizontalAlignment = Alignment.CenterHorizontally){
                    Stone.entries.forEach { player ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Cell( player, size = 20.dp)
                            Text(
                                text = if (player == Stone.WHITE) " - ${score?.white ?: 0}" else  " - ${score?.black ?: 0}",
                                style = MaterialTheme.typography.h4
                            )
                        }
                    }
                }
            }
        }
    )


@Composable
fun StatusBar(board: Board?, me: Stone?) =
    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center ) {
        me?.let{
            Text("You: ", style = MaterialTheme.typography.h4)
            Cell(player = it, size=50.dp)
            Spacer(Modifier.width(30.dp))
        }
        val (txt, player) = when(board){
            is BoardRun -> "Turn:" to board.player
            is BoardFinish -> "Winner:" to board.winner
            null -> "No Game" to null
        }
        Text(text=txt, style=MaterialTheme.typography.h4, textAlign = TextAlign.Center )
        if (player != null) Cell(player, size = 50.dp)
    }


@Composable
fun BoardView(boardCells: BoardCells?, viewLast : Boolean, lastPlay: Position?, onClick: (Position)->Unit) =

    Column(
        modifier = Modifier
            .size(BOARD_SIDE)
            .paint(
                painter = painterResource("board.png"),
                contentScale = ContentScale.FillBounds
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().height(CELL_SIDE),
        ) {
            Spacer(modifier = Modifier.width(CELL_SIDE))
            repeat(BOARD_SIZE.size) { col ->
                Text(
                    text = ('A' + col).toString(),
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(CELL_SIDE).height(CELL_SIDE).wrapContentWidth(align = Alignment.CenterHorizontally).wrapContentHeight(Alignment.Bottom),
                )
            }
        }


        repeat(BOARD_SIZE.size){ row ->
            Row(
                modifier = Modifier.fillMaxWidth().height(CELL_SIDE),
            ){

                Text(
                    text = (BOARD_SIZE.size - row).toString(),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 20.sp,
                    modifier = Modifier.width(CELL_SIDE).height(CELL_SIDE).wrapContentWidth(align = Alignment.CenterHorizontally).wrapContentHeight(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )

                repeat(BOARD_SIZE.size){ col ->

                    val pos = Position(row, col)

                    Box( modifier =  Modifier.size(CELL_SIDE).background(color = Color.Transparent))
                    {
                        IntersectionPoint(pos)
                        Cell(
                            boardCells?.get(pos),
                            highlight = viewLast && pos == lastPlay,
                            onClick = { onClick(pos)},
                        )
                    }


                }
            }
        }


    }



@Composable
fun Cell(player: Stone?, highlight: Boolean = false, size: Dp = CELL_SIDE, onClick: ()->Unit={}){


    val modifier = if(highlight) Modifier.size(size).background(color = Color.Transparent).border(width = 1.dp, color = Color.Red)
                   else Modifier.size(size).background(color = Color.Transparent)

    if(player == null) {
        Box(modifier.clickable(onClick = onClick))
    }else {
        val filename = when (player) {
            Stone.BLACK -> "blackStone.png"
            Stone.WHITE -> "whiteStone.png"
        }
        Image(
            painter = painterResource(filename),
            contentDescription = "Player $player",
            modifier = modifier
        )
    }
}

@Composable
fun IntersectionPoint(position: Position) {

    val (horizontalWidth, horizontalAlignment) = getHorizontal(position)

    val (verticalHeight, verticalAlignment) = getVertical(position)

    Box(
        modifier = Modifier
            .size(CELL_SIDE)
            .background(color = Color.Transparent)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .width(horizontalWidth)
                .height(GRID_THICKNESS)
                .background(color = Color.Black)
                .align(horizontalAlignment)
        )

        Box(
            modifier = Modifier
                .height(verticalHeight)
                .width(GRID_THICKNESS)
                .background(color = Color.Black)
                .align(verticalAlignment)
        )
    }
}

fun getHorizontal(position: Position): Pair<Dp, Alignment> =
    when(position.col) {
        BOARD_SIZE.size - 1 -> Pair(CELL_SIDE / 2, Alignment.CenterStart)
        0 -> Pair(CELL_SIDE / 2, Alignment.CenterEnd)
        else -> Pair(CELL_SIDE, Alignment.Center)
    }


fun getVertical(position: Position): Pair<Dp, Alignment> =
    when(position.row) {
        BOARD_SIZE.size - 1 -> Pair(CELL_SIDE / 2, Alignment.TopCenter)
        0 -> Pair(CELL_SIDE / 2, Alignment.BottomCenter)
        else -> Pair(CELL_SIDE, Alignment.Center)
    }


fun main() {
    MongoDriver("GO").use { driver ->
        application {
            Window(
                onCloseRequest = {},
                title = "Go Game",
                state = WindowState(size = DpSize.Unspecified)
            ) {
                App(driver, ::exitApplication)
            }
        }
    }
}
