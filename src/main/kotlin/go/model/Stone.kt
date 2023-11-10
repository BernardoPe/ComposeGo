package go.model
enum class Stone(val char: Char) {
    Black('#'), White('O');

    val other get() = if (this == Black) White else Black
}
fun String.toStone() = Stone.valueOf(this )
