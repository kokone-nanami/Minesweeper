package minesweeper

import kotlin.random.Random

val size = 9

val around: Array<Pair<Int, Int>> = arrayOf(
    -1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 0, 0 to 1, 1 to -1, 1 to 0, 1 to 1
)

fun inputGeneration(): Int {
    println("How many mines do you want on the field? ")
    return readLine()!!.toInt()
}

fun generate(n: Int): Array<Array<Int>> {
    val field = Array(9) {Array(size) {0} }
    var cnt = n
    while (cnt > 0) {
        val p = Pair(Random.nextInt(0, size), Random.nextInt(0, size))
        if (field[p.first][p.second] == 0) {
            field[p.first][p.second] = 9
            cnt--
        }
    }
    for (i in 0 until size)
        for (j in 0 until size)
            if (field[i][j] == 9) {
                around.forEach {
                    offset -> run {
                        val row = i + offset.first
                        val col = j + offset.second
                        if (row >= 0 && col >= 0 && row < size && col < size && field[row][col] != 9)
                            field[row][col]++
                    }
                }
            }

    return field
}

fun printMF(field: Array<Array<Int>>) {
    field.map { x -> x.map { y -> when (y) {
            0 -> print(".")
            9 -> print("X")
            else -> print(y)
        }
    }; println()}
}

fun main() {
    printMF(generate(inputGeneration()))
}
