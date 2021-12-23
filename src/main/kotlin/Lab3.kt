import java.io.File

data class Point(val x: Int, val y: Int) {
    fun normalizeCoords(): Point = Point(x + 1, y + 1)

    fun toStringValue(): String = "$x$y"
}

enum class Color(val value: String) {
    WHITE("W"),
    BLACK("B")
}

fun main() {
    val lines = File("in3.txt").bufferedReader().readLines()
    val whitePoints = lines[0].toPoints()
    val blackPoints = lines[1].toPoints()
    val matrix = mutableListOf<MutableList<String>>()
    for (i in 0..7) {
        val list = mutableListOf<String>()
        for (j in 0..7) {
            list.add("X")
        }
        matrix.add(list)
    }
    for (x in 0..7) {
        for (y in 0..7) {
            when (Point(x, y)) {
                in whitePoints -> matrix[y][x] = "W"
                in blackPoints -> matrix[y][x] = "B"
                else -> matrix[y][x] = "_"
            }
        }
    }
    matrix.forEach { println(it) }
    //ищем белые группы
    val whiteGroups = whitePoints.map {
        findGroup(matrix, it, Color.WHITE)
    }.toSet()
    //ищем черные группы
    val blackGroups = blackPoints.map {
        findGroup(matrix, it, Color.BLACK)
    }.toSet()
    val whiteTurns = allPossibleTurns(matrix, Color.WHITE)
    val blackTurns = allPossibleTurns(matrix, Color.BLACK)

    //живые по текущему состоянию белые группы
    val initialAliveWhite = findGroups(matrix, whiteGroups).first
    val initialAliveBlack = findGroups(matrix, blackGroups).first

    val whiteResult = mutableListOf<Pair<Set<Set<Point>>, Pair<List<List<String>>, Point>>>()
    for (turn in whiteTurns) {
        val groups = findGroups(turn.first, blackGroups)
        val alive = groups.first
        val dead = initialAliveBlack.minus(alive)
        if (initialAliveBlack.size > alive.size) {
            whiteResult.add(Pair(dead, turn))
        }
    }
    var whiteMax = Int.MIN_VALUE
    var whiteMaxPoint: Point? = null
    for (res in whiteResult) {
        if (res.first.flatten().size > whiteMax) {
            whiteMax = res.first.flatten().size
            whiteMaxPoint = res.second.second
        }
    }
    if (whiteResult.isEmpty()) {
        println("N")
    } else {
        println(whiteMaxPoint?.normalizeCoords()?.toStringValue())
    }


    val blackResult = mutableListOf<Pair<Set<Set<Point>>, Pair<List<List<String>>, Point>>>()
    for (turn in blackTurns) {
        val groups = findGroups(turn.first, whiteGroups)
        val alive = groups.first
        val dead = initialAliveWhite.minus(alive)
        if (initialAliveWhite.size > alive.size) {
            blackResult.add(Pair(dead, turn))
        }
    }
    var blackMax = Int.MIN_VALUE
    var blackMaxIndexPoint: Point? = null
    for (res in blackResult) {
        if (res.first.flatten().size > blackMax){
            blackMax = res.first.flatten().size
            blackMaxIndexPoint = res.second.second
        }
    }
    if (blackResult.isEmpty()) {
        println("N")
    } else {
        println(blackMaxIndexPoint?.normalizeCoords()?.toStringValue())
    }
}

fun findGroups(
    matrix: List<List<String>>,
    groups: Set<Set<Point>>
): Pair<Set<Set<Point>>, Set<Set<Point>>> {
    val aliveGroups = mutableSetOf<Set<Point>>()
    val deadGroups = mutableSetOf<Set<Point>>()
    for (group in groups) {
        group_loup@ for (point in group) {
            for (neighbor in neighbors(point)) {
                if (matrix[neighbor.y][neighbor.x] == "_") {
                    aliveGroups.add(group)
                    break@group_loup
                } else {
                    deadGroups.add(group)
                }
            }
        }
    }
    return Pair(aliveGroups, deadGroups)
}

fun allPossibleTurns(matrix: MutableList<MutableList<String>>, color: Color): List<Pair<List<List<String>>, Point>> {
    val result = mutableListOf<Pair<List<List<String>>, Point>>()
    for (y in matrix.indices) {
        for (x in matrix[y].indices) {
            if (matrix[y][x] == "_") {
                result.add(Pair(matrix.toList().mapIndexed { lineIndex, listLine ->
                    List(listLine.size) { rowIndex ->
                        if (Point(rowIndex, lineIndex) == Point(x, y)) color.value else matrix[lineIndex][rowIndex]
                    }
                }, Point(x, y)))
            }
        }
    }
    return result.toSet().toList()
}

fun findGroup(matrix: List<List<String>>, start: Point, color: Color): Set<Point> {
    val visited = mutableSetOf<Point>()
    fun dfs(matrix: List<List<String>>, start: Point, color: Color) {
        if (start !in visited && matrix[start.y][start.x] == color.value) {
            visited.add(start)
            for (point in neighbors(start)) {
                dfs(matrix, point, color)
            }
        }
    }
    dfs(matrix, start, color)
    return visited
}

fun neighbors(point: Point): List<Point> {
    val neighbors = mutableListOf<Point>()
    for (dy in -1..1) {
        for (dx in -1..1) {
            if ((dx != 0 && dy != 0) || (dx == 0 && dy == 0)) {
                continue
            }
            val nextPoint = Point(point.x + dx, point.y + dy)
            if (nextPoint.x < 0 || nextPoint.y < 0 || nextPoint.x >= 8 || nextPoint.y >= 8) {
                continue
            }
            neighbors.add(nextPoint)
        }
    }
    return neighbors
}


fun String.toPoints(): List<Point> =
    split(" ")
        .filter { it.isNotEmpty() && it != "00" }
        .map { Point(it[0].digitToInt() - 1, it[1].digitToInt() - 1) }
