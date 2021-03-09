package com.example.myapplication.game

import com.example.myapplication.R
import com.example.myapplication.gl.GLESHelperTexturedSquare

class SudokuGame : Game("Sudoku", R.id.sudokuActivity) {
    val cells = Array(9) {Array(9) {SudokuGameCell()} }
    var selectedCell : SudokuGameCell? = null
    var end = false
    var win = false

    init {
        cells[3][3].apply {
            updatable = false
            number = 3
        }
        cells[2][5].apply {
            updatable = false
            number = 6
        }
    }

    fun checkGameEnd() {
        //TODO

        //end = true
        //win = true|false
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