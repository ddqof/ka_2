import java.io.File

class Constants {
    companion object {
        const val INPUT_FILENAME = "in.txt"
        val MATCHINGS = mutableMapOf<Int, Int>()
        val USED = mutableSetOf<Int>()
    }
}

fun main() {
    val graph = readInput(Constants.INPUT_FILENAME)
    for (setIndex in graph.keys) {
        dfs(graph, setIndex)
    }
    if (graph.size == Constants.MATCHINGS.size) {
        println("Y")
        println(Constants.MATCHINGS.keys.joinToString(" "))
    } else {
        println("N")
    }
}

fun dfs(graph: Map<Int, List<Int>>, node: Int): Boolean {
    val matchings = Constants.MATCHINGS
    val used = Constants.USED
    if (used.contains(node)) {
        return false
    }
    used.add(node)
    if (graph[node] != null) {
        for (adjacent in graph[node]!!) {
            if (!matchings.containsKey(adjacent) || dfs(graph, matchings[adjacent]!!)) {
                matchings[adjacent] = node
                return true
            }
        }
    }
    return false
}

fun readInput(fileName: String): Map<Int, List<Int>> {
    val input: List<String> = File(fileName).bufferedReader().readLines()
    return input.drop(1)
        .dropLast(1)
        .withIndex()
        .associate {
            Pair(
                it.index,
                it.value.split(" ").map { char -> char.toInt() }
            )
        }
}