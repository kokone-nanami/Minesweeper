package minesweeper

import kotlin.random.Random
import minesweeper.GameStatus.*
import minesweeper.Status.*

enum class GameStatus { ONGOING, LOST, WIN }

enum class Status { NUMBER, BACK, FLAG }

val around: Array<Pair<Int, Int>> = arrayOf(
    -1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 0, 0 to 1, 1 to -1, 1 to 0, 1 to 1
)

fun askForInput(): Int {
    println("How many mines do you want on the field? ")
    return readLine()!!.toInt()
}

class MineSweeper(val size: Int, val count: Int) {

    class Case(var value: Int, var status: Status)

    var field = Array(size) { Array(size) {Case(0, BACK)} }

    var gameStatus = ONGOING


    init {
        var cnt = count
        while (cnt > 0) {
            val p = Pair(Random.nextInt(0, size), Random.nextInt(0, size))
            if (field[p.first][p.second].value == 0) {
                field[p.first][p.second].value = 9
                cnt--
            }
        }
        for (i in 0 until size)
            for (j in 0 until size)
                if (field[i][j].value == 9) {
                    around.forEach { offset ->
                        run {
                            val row = i + offset.first
                            val col = j + offset.second
                            if (row >= 0 && col >= 0 && row < size && col < size && field[row][col].value != 9) {
                                field[row][col].value++
                                field[row][col].status = NUMBER
                            }
                        }
                    }
                }
    }

    fun showMap() {
        println(" │123456789│")
        println("—│—————————│")
        for (i in field.indices) {
            print("${i+1}│")
            field[i].map { y ->
                if (y.status == NUMBER)
                    print(y.value)
                else if (y.status == FLAG)
                    print('*')
                else
                    print('.')
            }
            println("│")
        }
        println("—│—————————│")
    }

    fun toggleCase(x: Int, y: Int): Boolean {
        if (field[x][y].status == NUMBER) {
            return false
        }
        else {
            if (field[x][y].status == BACK) {
                field[x][y].status = FLAG
            }
            else {
                field[x][y].status = BACK
            }
            return true
        }
    }

    fun win(): Boolean {
        var remain = 0
        var wrong = 0
        for (i in 0 until size)
            for (j in 0 until size) {
                if (field[i][j].status == BACK && field[i][j].value == 9)
                    remain++
                if (field[i][j].status == FLAG && field[i][j].value != 9)
                    wrong++
            }
        if (remain != 0 || wrong != 0) return false
        else return gameStatus != LOST
    }

}

fun main() {
    val s = askForInput()
    val ms = MineSweeper(9, s)
    ms.showMap()
    while (!ms.win()) {
        println("Set/delete mines marks (x and y coordinates): ")
        val coo = readLine()!!.split(" ").map { it.toInt() }
        if (ms.toggleCase(coo[1]-1, coo[0]-1))
            ms.showMap()
        else
            println("There is a number here!")
    }
    println("Congratulations! You found all mines!")
}
