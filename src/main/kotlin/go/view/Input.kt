package go.view

data class CommandLine  (
    val name : String,
    val args: List<String>
)

fun readCmdLine() : CommandLine {
    print("> ")
    val line = readln().split(" ").filter{ it.isNotBlank() }
    return if(line.isEmpty()) readCmdLine() else CommandLine(line.first().uppercase(), line.drop(1))
}

