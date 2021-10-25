import java.io.File

fun main() {
    val inputPoints = pointsFromFile("in.txt")
    val graph = completeGraph(inputPoints)
    val resultGraph = Graph(graph.connections, mutableListOf())
    for (edge in graph.edges) {
        if (!resultGraph.hasCycle(edge)) {
            resultGraph.edges.add(edge)
        }
        if (resultGraph.points() == inputPoints) {
            break
        }
    }
    resultGraph.printResult()
}

fun pointsFromFile(fileName: String): List<Point> {
    val input: List<String> = File(fileName).bufferedReader().readLines()
    when (input.size) {
        0 -> throw IllegalArgumentException("Был передан пустой файл")
        1 -> throw IllegalArgumentException(
            "Был передан файл с указанием кол-ва точек," +
                    " но без указания координат самих точек"
        )
    }
    return input.drop(1).map { Point(it.split(" "), input.indexOf(it)) }
}

/**
 * Собирает полносвязный граф
 */
fun completeGraph(points: List<Point>): Graph {
    val connections: Map<Point, List<Point>> = points.associateWith { points.minus(it) }
    val edges = mutableListOf<Edge>()
    for (current in points) {
        for (another in points) {
            if (current == another) continue
            val edge = Edge(current, another)
            if (edge !in edges) edges.add(edge)
        }
    }
    return Graph(connections, edges.sortedBy { it.weight }.toMutableList())
}
