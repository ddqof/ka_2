import java.io.File

class Constants {
    companion object {
        const val INPUT_FILENAME = "in.txt"
        val MATCHINGS = mutableMapOf<String, String>()
        val USED = mutableSetOf<String>()
    }
}

fun main() {
    val graph = readInput(Constants.INPUT_FILENAME)
    for (setIndex in graph.keys) {
        Constants.USED.clear()
        dfs(graph, setIndex)
    }
    if (graph.size == Constants.MATCHINGS.size) {
        println("Y")
        println(Constants.MATCHINGS.toList().sortedBy { it.second }.toMap().keys.joinToString(" "))
    } else {
        println("N")
    }
}

fun dfs(graph: Map<String, List<String>>, node: String): Boolean {
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

fun readInput(fileName: String): Map<String, List<String>> {
    val input: List<String> = File(fileName).bufferedReader().readLines()
    return input.drop(1)
        .dropLast(1)
        .withIndex()
        .associate {
            Pair(
                "A_${it.index}",
                it.value.split(" ")
            )
        }
}