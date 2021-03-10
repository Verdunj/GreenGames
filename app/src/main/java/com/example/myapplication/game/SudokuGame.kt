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
    // recherche si un chiffre est present sur une ligne
    fun absentSurLigne(element:Int, l:Int): Boolean{

            for (j in 0..cells.size){
                if (cells[l][j].number == element)
                    return false
            }
            return true
    }

    // recherche si un ciffre est present sur la colonne
    fun absentSurCol(element: Int, c:Int): Boolean {
        for (i in 0..cells.size){
            if(cells[i][c].number == element)
                return false
        }
        return true
    }

    //un chiffre absent sur le bloc de 3x3

    fun absentSurBloc(element:Int, l:Int,c:Int): Boolean {

        var initI = l-(l%3)
        var initJ = c-(c%3)
        var i = initI
        var j = initJ
        var dp = initI+3
        for(i in initI+3..i+1 ){
            for(j in initJ..j+1){
                if(cells[i][j].number == element)
                    return false
            }
        }
        return true
    }



    // grille valide

    fun validCells(position:Int): Boolean{
        if(position == 9*9)
            return true
        var i= position/9
        var j= position/9
        if(cells[i][j].number != 0 )
            return validCells(position+1)
        // la suite normalement c'est de verrifier las cases occupée
        // ensuite verifier toute la grille
        
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