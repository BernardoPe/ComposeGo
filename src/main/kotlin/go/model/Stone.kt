package go.model

fun main() {
    val stone = Stone.BLACK
    val stone2 = Stone.WHITE
}

enum class Stone(val char: Char) {
    BLACK('#'), WHITE('O');

    val other get() = if (this == BLACK) WHITE else BLACK
}
