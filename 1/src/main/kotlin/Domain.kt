import kotlin.math.abs

data class Point(val x: Int, val y: Int, val orderNumber: Int) {
    constructor(x: String, y: String, orderNumber: Int) : this(x.toInt(), y.toInt(), orderNumber)

    constructor(coords: List<String>, orderNumber: Int) : this(coords[0], coords[1], orderNumber) {
        if (coords.size > 2) throw IllegalArgumentException(
            "Объект Point не может быть " +
                    "инициализирован списком более чем из 2-х элементов"
        )
    }

    fun distance(point: Point) = abs(x - point.x) + abs(y - point.y)
}

data class Edge(val first: Point, val second: Point) {
    val weight = first.distance(second)

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other?.javaClass != javaClass) return false
        other as Edge
        return ((other.first == first && other.second == second) || (other.second == first && other.first == second))
    }

    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        return result
    }
}

data class Graph(val connections: Map<Point, List<Point>>, val edges: MutableList<Edge>) {
    /**
     * Проверяет есть ли в графе цикл.
     *
     * Проверяет путем запуска DFS из каждой вершины графа
     */
    fun hasCycle(startEdge: Edge): Boolean {
        val visitedPoints = mutableListOf(startEdge.first)
        var hasCycle = false
        fun dfs(startPoint: Point, from: Edge) {
            visitedPoints.add(startPoint)
            val currentConnections = connections[startPoint]
            if (currentConnections.isNullOrEmpty()) return
            currentConnections.forEach {
                val edge = Edge(startPoint, it)
                if (edge != from && edge in edges) {
                    if (it in visitedPoints) {
                        hasCycle = true
                    } else {
                        dfs(it, edge)
                    }
                }
            }
        }
        dfs(startEdge.second, startEdge)
        return hasCycle
    }

    /**
     * Возвращает все точки из которых состоит граф
     */
    fun points(): List<Point> = edges.flatMap { listOf(it.first, it.second) }.distinct()

    private fun adjacencyList(): Map<Int, List<Int>> {
        val result = mutableMapOf<Point, MutableList<Point>>()
        connections.forEach {
            if (it.key !in result.keys) {
                result[it.key] = mutableListOf()
            }
            for (connection in it.value) {
                if (Edge(it.key, connection) in edges) {
                    result[it.key]!!.add(connection)
                }
            }
        }
        return result.entries.associate{
            it.key.orderNumber to it.value.map { p -> p.orderNumber }.sorted()
        }
    }

    fun printResult() {
        adjacencyList().forEach { println(it) }
        println(0)
        println(edges.fold(0) {acc, edge -> acc + edge.weight })
    }
}