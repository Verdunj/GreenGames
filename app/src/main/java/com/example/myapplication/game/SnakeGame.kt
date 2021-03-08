package com.example.myapplication.game

import com.example.myapplication.R

class SnakeGame: Game("Snake", R.id.snakeActivity) {
    var fini = false
    var gagne = false
    var menu= SnakeGame.Companion.SnakeGameMenu.NEW
    lateinit var cells : Array<Array<SnakeCellState>>
    var snake = listOf<SnakeBody>(SnakeBody(4, 8,14),SnakeBody(5, 8,1), SnakeBody(6, 8,4))

    fun start() {
        cells = Array(15) { Array(10) { SnakeGame.Companion.SnakeCellState() } }
        menu = SnakeGame.Companion.SnakeGameMenu.INGAME
        fini = false
        gagne = false
    }

    companion object{
        enum class SnakeGameMenu {
            INGAME,
            NEW
        }

        class SnakeCellState() {
            var etat= SnakeState.VIDE
            var sprite=9
        }

        enum class SnakeState {
            VIDE,
            SERPENT,
            FRUIT
        }

        class SnakeBody(val x: Int, val y: Int, val imgNb: Int){

        }
    }
}