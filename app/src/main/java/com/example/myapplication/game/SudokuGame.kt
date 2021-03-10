package com.example.myapplication.game

import com.example.myapplication.R
import com.example.myapplication.gl.GLESHelperTexturedSquare
import de.sfuhrm.sudoku.Creator
import de.sfuhrm.sudoku.GameMatrix

class SudokuGame : Game("Sudoku", R.id.sudokuActivity) {
    val cells = Array(9) {Array(9) {SudokuGameCell()} }
    var selectedCell : SudokuGameCell? = null
    var end = false
    var win = false

    init {
        createGame()
    }

    fun createGame() {
        val matrix = Creator.createFull()
        val riddle = Creator.createRiddle(matrix)!!
        val arr = riddle.array!!
        for ((i, ba) in arr?.withIndex())
            for ((j, a) in ba.withIndex())
                cells[i][j].apply {
                    if (a == GameMatrix.UNSET) {
                        updatable = true
                        number = 0
                    } else {
                        updatable = false
                        number = a.toInt()
                    }
                }
    }

    fun checkGameEnd() {
        val buffer = IntArray(10) {0}

        if (
                checkBlocks(buffer)
                && checkLines(buffer)
                && checkCols(buffer)
        ) {
            println("win")
            end = true
            win = true
        }
    }

    private fun purgeBuffer(buffer: IntArray) {
        for (i in buffer.indices)
            buffer[i] = 0
    }

    private fun checkBlocks(buffer: IntArray): Boolean {
        for (bi in 0..2) {
            for (bj in 0..2) {
                purgeBuffer(buffer)
                // implicit hack if a block contain a 0
                buffer[0] = 1
                for (ii in 0..2)
                    for (ij in 0..2) {
                        val i = ii + bi * 3
                        val j = ij + bj * 3
                        // already this number on the block
                        if (++buffer[cells[i][j].number] > 1)
                            return false
                    }
            }
        }
        return true
    }

    private fun checkLines(buffer: IntArray): Boolean {
        for (i in 0..8) {
            purgeBuffer(buffer)
            // implicit hack if a line contain a 0
            buffer[0] = 1
            for (j in 0..8) {
                // already this number on the line
                if (++buffer[cells[i][j].number] > 1)
                    return false
            }
        }
        return true
    }

    private fun checkCols(buffer: IntArray): Boolean {
        for (j in 0..8) {
            purgeBuffer(buffer)
            // implicit hack if a column contain a 0
            buffer[0] = 1
            for (i in 0..8) {
                // already this number on the column
                if (++buffer[cells[i][j].number] > 1)
                    return false
            }
        }
        return true
    }



    class SudokuGameCell() {
        var updatable = true
            set(value) {
                if (value)
                    renderer.index = number
                else
                    renderer.index = number + 10
                field = value
            }
        var number = 0
            set(value) {
                if (updatable)
                    renderer.index = value
                else
                    renderer.index = value + 10
                field = value
            }
        val renderer = GLESHelperTexturedSquare(number, 0, 0, 0)

    }
}