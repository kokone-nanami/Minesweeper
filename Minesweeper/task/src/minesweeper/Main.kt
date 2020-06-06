package minesweeper

import kotlin.random.Random
import minesweeper.GameStatus.*
import minesweeper.Status.*

enum class GameStatus { ONGOING, LOST, WIN }

enum class Status { FRONT, BACK, FLAG }

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

    constructor(size: Int, count: Int, marked: MutableSet<Pair<Int, Int>>): this(size, count) {
        for (x in marked) {
            field[x.first][x.second].status = FLAG
        }
    }

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
                            }
                        }
                    }
                }
    }

    private fun bloom(x: Int, y: Int) {
        val SAFE_CELL = 0
        if (field[x][y].value != SAFE_CELL)
            return
        around.forEach { offset ->
            run {
                val row = x + offset.first
                val col = y + offset.second
                if (row >= 0 && col >= 0 && row < size && col < size && (field[row][col].status == BACK || field[row][col].status == FLAG)) {
                    field[row][col].status = FRONT
                    bloom(row, col)
                }
            }
        }
    }

    fun showMap() {
        if (gameStatus == ONGOING) {
            println(" │123456789│")
            println("—│—————————│")
            for (i in field.indices) {
                print("${i + 1}│")
                field[i].map { y ->
                    when (y.status) {
                        FRONT -> if (y.value == 0) print("/") else if (y.value == 9) print('X') else print(y.value)
                        FLAG -> print('*')
                        BACK -> print('.')
                    }
                }
                println("│")
            }
            println("—│—————————│")
        }
        else {
            println(" │123456789│")
            println("—│—————————│")
            for (i in field.indices) {
                print("${i + 1}│")
                field[i].map { y ->
                    if (y.status == FLAG && y.value != 9) {
                        if (y.value == 0)
                            print('/')
                        else
                            print(y.value)
                    }
                    else if (y.status == FLAG)
                        print('X')
                    else if (y.status == BACK && y.value == 9)
                        print('X')
                    else if (y.status == BACK)
                        print('.')
                    else if (y.value == 0)
                        print("/")
                    else if (y.value == 9)
                        print('X')
                    else
                        print(y.value)
                }
                println("│")
            }
            println("—│—————————│")
        }
    }

    fun claim(x: Int, y: Int) {
        if (x >= 0 && y >= 0 && x < size && y < size) {
            field[x][y].status = FRONT
            if (field[x][y].value == 9) {
                gameStatus = LOST
                return
            }
            bloom(x, y)
        }
    }

    fun mark(x: Int, y: Int) {
        if (field[x][y].status == BACK) {
            field[x][y].status = FLAG
        }
        else {
            field[x][y].status = BACK
        }
    }

    fun lost(): Boolean {
        return gameStatus == LOST
    }

    fun win(): Boolean {
        var remain = 0
        for (i in 0 until size)
            for (j in 0 until size)
                if (field[i][j].status == BACK || field[i][j].status == FLAG)
                    remain++
        if (remain != count) return false
        else if (gameStatus != LOST) {
            gameStatus = WIN
            return true
        }
        else
            return false
    }

}

fun main() {
    val s = askForInput()
    var ms = MineSweeper(9, s)
    var explored = false
    val markedList = mutableSetOf<Pair<Int, Int>>()
    ms.showMap()
    outer@ while (true) {
        println("Set/delete mines marks (x and y coordinates): ")
        val cmd = readLine()!!.split(" ")
        if (cmd[2] == "mine") {
            val x = cmd[1].toInt() - 1
            val y = cmd[0].toInt() - 1
            ms.mark(x, y)
            if (!explored) {
                if (markedList.contains(x to y))
                    markedList -= x to y
                else
                    markedList += x to y
            }
        }
        if (cmd[2] == "free") {
            val x = cmd[1].toInt() - 1
            val y = cmd[0].toInt() - 1
            if (!explored) {
                while (ms.field[x][y].value == 9) {
                    ms = MineSweeper(9, s, markedList)
                }
            }
            ms.claim(cmd[1].toInt() - 1, cmd[0].toInt() - 1)
            explored = true
        }
        if (ms.win()) {
            ms.showMap()
            println("Congratulations! You found all mines!")
            break@outer
        }
        if (ms.lost()) {
            ms.showMap()
            println("You stepped on a mine and failed!")
            break@outer
        }
        ms.showMap()
    }
}
