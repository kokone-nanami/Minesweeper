package minesweeper

import kotlin.random.Random

fun inputGeneration(): Int {
    println("How many mines do you want on the field? ")
    return readLine()!!.toInt()
}

fun generate(n: Int): Array<Array<Int>> {
    var matrix = Array(9) {Array(9) {0} }
    var cnt = n
    while (cnt > 0) {
        val p = Pair(Random.nextInt(0, 9), Random.nextInt(0, 9))
        if (matrix[p.first][p.second] == 1) {
            continue
        }
        else {
            matrix[p.first][p.second] = 1
            cnt--
        }
    }
    return matrix
}

fun printMF(field: Array<Array<Int>>) {
    field.map { x -> x.map { y -> if (y == 1) print('X') else print('.')}; println()}
}

fun main() {
    printMF(generate(inputGeneration()))
}
