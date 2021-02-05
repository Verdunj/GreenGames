package com.example.myapplication.game

import java.util.concurrent.atomic.AtomicInteger

class Game(_name: String) {
    companion object {
        val game_id = AtomicInteger()
    }
    val name = _name
    val id = game_id.getAndIncrement()

}