package go.model

fun main() {
    val stone = Stone.Black
    val stone2 = Stone.White
}

enum class Stone(val char: Char) {
    Black('#'), White('O');

    val other get() = if (this == Black) White else Black
}
