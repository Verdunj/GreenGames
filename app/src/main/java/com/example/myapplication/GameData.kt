package com.example.myapplication

import com.example.myapplication.game.Game
import java.util.*
import kotlin.collections.HashMap

class GameData {
    companion object {
        val core = GameData()
    }

    private val games = HashMap<Int, Game>()

    init {
        registerGame(Game("test_1"))
        registerGame(Game("test_2"))
        registerGame(Game("test_3"))
        registerGame(Game("test_4"))
        registerGame(Game("snake"))
    }

    fun registerGame(game : Game) : Unit {
        games.put(game.id, game)
    }

    fun findGameById(id : Int) : Game? {
        return games.get(id)
    }

    fun findAllGames() : Collection<Game> {
        return Collections.unmodifiableCollection(games.values);
    }
}