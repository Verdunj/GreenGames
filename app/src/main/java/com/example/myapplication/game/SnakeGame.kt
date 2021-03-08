package com.example.myapplication.game

import com.example.myapplication.R

class SnakeGame: Game("Snake", R.id.snakeActivity) {
    var fini = false
    var gagne = false
    var menu= SnakeGame.Companion.SnakeGameMenu.NEW

    companion object{
        enum class SnakeGameMenu {
            INGAME,
            NEW
        }
    }
}