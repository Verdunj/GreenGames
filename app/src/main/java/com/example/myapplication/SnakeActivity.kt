package com.example.myapplication

import android.view.MotionEvent
import android.view.View
import com.example.myapplication.game.DemineurGame
import com.example.myapplication.game.SnakeGame
import com.example.myapplication.gl.GLESHelperActivity
import com.example.myapplication.gl.GLESHelperRenderer
import com.example.myapplication.gl.GLESUtils
import java.lang.RuntimeException
import kotlin.math.min

class SnakeActivity : GLESHelperActivity(R.drawable.snake, 5) {
    val snake = GameData.core.findGameByClass(SnakeGame::class) ?: throw RuntimeException("Can't find game")
    private var tileSize = 1
    private var startX = 0
    private var startY = 0

    override fun onDraw(glesHelper: GLESHelperRenderer) {
        val width = glesHelper.width
        val height = glesHelper.height

        tileSize = width / "Lancer".length

        println(" "+width+" "+height)
        when(snake.menu) {
            SnakeGame.Companion.SnakeGameMenu.NEW -> {
                //glesHelper.renderStringCenteredxy("Lancer", width / 2, height /2, tileSize, true, false)
                glesHelper.renderString("ance", 1 * tileSize, height / 2, tileSize, 1)
                glesHelper.renderString("L", 0, height / 2, tileSize, 14)
                glesHelper.renderString("r", 5 * tileSize, height / 2, tileSize, 2)
                glesHelper.renderString(" ", 5 * tileSize, height / 2 + tileSize, tileSize, 9)
                glesHelper.renderStringCenteredxy("Snake", width / 2, tileSize, tileSize)


            } SnakeGame.Companion.SnakeGameMenu.INGAME -> {


            }
        }
      /*  when (snake.menu) {
            SnakeGame.Companion.SnakeGameMenu.NEW -> {

            }
        }*/
    }

    override fun onClick(v: View, e: MotionEvent, renderer: GLESHelperRenderer): Boolean {
        print(e.x.toInt())
        print(" "+e.y.toInt())
        print("-------------------------------------------------------------------\n")
        println(" "+(renderer.width-"Lancer".length/2))
        println(" "+(renderer.height - tileSize * 3 / 2))
        when (snake.menu) {
            SnakeGame.Companion.SnakeGameMenu.NEW -> {
                if (GLESUtils.isInRec(e.x.toInt(), e.y.toInt(),0,renderer.height /2,tileSize*"Lancer".length,tileSize))
                    println("j'ai cliqué sur le bouton")
                     snake.start()
            }

         }
        return true
        }

    }
