package com.example.myapplication

import android.view.MotionEvent
import android.view.View
import com.example.myapplication.game.DemineurGame
import com.example.myapplication.game.SnakeGame
import com.example.myapplication.gl.GLESHelperActivity
import com.example.myapplication.gl.GLESHelperRenderer
import com.example.myapplication.gl.GLESUtils
import java.lang.RuntimeException
import android.content.Context
import kotlin.math.min

class SnakeActivity : GLESHelperActivity(R.drawable.snake, 5) {
    val snake = GameData.core.findGameByClass(SnakeGame::class) ?: throw RuntimeException("Can't find game")
    private var tileSize = 1
    private var startX = 0
    private var startY = 0
    private var t = 0L


    override fun onDraw(glesHelper: GLESHelperRenderer) {
        val width = glesHelper.width
        val height = glesHelper.height

        when(snake.menu) {
            SnakeGame.Companion.SnakeGameMenu.NEW -> {
                tileSize = width / "Lancer".length
                //glesHelper.renderStringCenteredxy("Lancer", width / 2, height /2, tileSize, true, false)
                glesHelper.renderString("ance", 1 * tileSize, height / 2, tileSize, 1)
                glesHelper.renderString("L", 0, height / 2, tileSize, 14)
                glesHelper.renderString("r", 5 * tileSize, height / 2, tileSize, 2)
                glesHelper.renderString(" ", 5 * tileSize, height / 2 + tileSize, tileSize, 9)
                glesHelper.renderStringCenteredxy("Snake", width / 2, tileSize, tileSize)


            } SnakeGame.Companion.SnakeGameMenu.INGAME -> {
                val time = System.currentTimeMillis()
                tileSize=min(width/snake.width,height/snake.height)
                // draw water
                var x1 = 0
                var y1 = 0
                for(i in 0..snake.height-1){
                    for(j in 0..snake.width) {
                        glesHelper.renderTex(21, j * tileSize, i * tileSize, tileSize, tileSize)
                        x1 = i
                        y1 = j
                    }
                }
                println("i = $x1 j = $y1")
                if(time>t){
                    snake.update()
                    t=time+snake.wait_time
                }

                glesHelper.renderTex(snake.pomme.type, snake.pomme.x*tileSize, snake.pomme.y*tileSize, tileSize, tileSize)
                for(lm in snake.snake){
                    glesHelper.renderTex(lm.imgNb, lm.x*tileSize, lm.y*tileSize, tileSize, tileSize)
                }


                // Score
                var score  = snake.fruitEaten.toString()
                glesHelper.renderStringCenteredxy(score, width / 2, tileSize * snake.height + tileSize*4, tileSize * 4)
                //Pause button
                glesHelper.renderTex(16, width / 2 - tileSize * 4 / 2, tileSize * snake.height + tileSize * 8, tileSize * 4, tileSize * 4)
            }
        }
    }

    override fun onClick(v: View, e: MotionEvent, renderer: GLESHelperRenderer): Boolean {

        when (snake.menu) {
            SnakeGame.Companion.SnakeGameMenu.NEW -> {
                if (GLESUtils.isInRec(e.x.toInt(), e.y.toInt(),0,renderer.height /2,tileSize*"Lancer".length,tileSize)){
                    snake.start()
                }
            }
            SnakeGame.Companion.SnakeGameMenu.INGAME -> {
                if (GLESUtils.isInSquare(e.x.toInt(), e.y.toInt(), renderer.width / 2 - tileSize * 4 / 2, tileSize * snake.height + tileSize * 8, tileSize * 4)) {
                    // pause
                    snake.menu = SnakeGame.Companion.SnakeGameMenu.NEW
                }
            }
         }
        return true
        }

    override fun onSwipeUp(renderer: GLESHelperRenderer){
        snake.mouvement= SnakeGame.Companion.SnakeMoves.HAUT

    }

    override fun onSwipeDown(renderer: GLESHelperRenderer){
        snake.mouvement= SnakeGame.Companion.SnakeMoves.BAS

    }
    override fun onSwipeLeft(renderer: GLESHelperRenderer){
        snake.mouvement= SnakeGame.Companion.SnakeMoves.GAUCHE


    }
    override fun onSwipeRight(renderer: GLESHelperRenderer){
        snake.mouvement= SnakeGame.Companion.SnakeMoves.DROITE

    }

    override fun onInit(renderer: GLESHelperRenderer) {
    }

}
