package com.example.myapplication

import com.example.myapplication.game.DemineurGame
import com.example.myapplication.game.Game
import com.example.myapplication.game.SudokuGame
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KClass

class GameData {
    companion object {
        val core = GameData()
    }

    private val games = HashMap<KClass<out Game>, Game>()

    init {
        registerGame(DemineurGame())
        registerGame(SudokuGame())
    }

    fun registerGame(game : Game) : Unit {
        games.put(game::class, game)
    }


    @Deprecated("Old bad idea, use findGameByClass instead")
    fun findGameById(id : Int) : Game? {
        for (g in games.values)
            if (g.id == id)
                return g
        return null
    }

    fun <T : Game> findGameByClass(cls : KClass<T>) : T? {
        return games.get(cls) as T?
    }

    fun findAllGames() : Collection<Game> {
        return Collections.unmodifiableCollection(games.values);
    }
}