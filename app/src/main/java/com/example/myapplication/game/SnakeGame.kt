package com.example.myapplication.game

import com.example.myapplication.R
import com.example.myapplication.gl.GLESUtils
import kotlin.math.log
import kotlin.random.Random

class SnakeGame: Game("Snake", R.id.snakeActivity) {
    var fini = false
    var menu= SnakeGame.Companion.SnakeGameMenu.NEW
    var snake = mutableListOf<SnakeBody>(SnakeBody(0, 0,14),SnakeBody(1, 0,1), SnakeBody(2, 0,4))
    var mouvement=SnakeMoves.PAUSE
    var wait_time=200L
    var pomme = Apple(0,0,15)
    val height = 25
    val width = 20
    var fruitList = listOf<Int>(15,20)
    var fruitEaten=10



    fun start() {
        menu = SnakeGame.Companion.SnakeGameMenu.INGAME
        mouvement=SnakeMoves.PAUSE
        fini = false
        snake.clear()
        snake.add(SnakeBody(0, 0,14))
        snake.add(SnakeBody(1, 0,1))
        snake.add(SnakeBody(2, 0,4))
        pomme.x= Random.nextInt(width)
        pomme.y= Random.nextInt(height)
        fruitEaten=10
        pomme.type=fruitList[Random.nextInt(fruitList.size)]
        wait_time=200L

    }

    fun update(){
        if(mouvement==SnakeMoves.PAUSE || fini){
            return
        }
        var cou= snake[snake.size-2]
        var tete= snake[snake.size-1]
        val dx= tete.x-cou.x+ mouvement.x
        val dy= tete.y-cou.y+ mouvement.y
        tete.imgNb=verif_dir(dx,dy)
        var nv_tete= SnakeBody(tete.x+mouvement.x,tete.y+mouvement.y,mouvement.img)
        for(b in snake){
            if(nv_tete.x==b.x && nv_tete.y == b.y){
                start()
                return
            }
        }
        if(!GLESUtils.isInRec(nv_tete.x,nv_tete.y,0,0,width-1,height-1)){
            start()
            return
        }
        snake.add(nv_tete)
        if(manger_pomme()){
            pomme.x= Random.nextInt(width)
            pomme.y= Random.nextInt(height)
            pomme.type=fruitList[Random.nextInt(fruitList.size)]
            fruitEaten++
            if(wait_time>20) {
                wait_time = wait_time - (40 / fruitEaten)
            }
            println(wait_time)
            println(fruitEaten)
        }else{
            snake.removeAt(0)
            var tail=snake[0]
            var pre_tail=snake[1]
            val tdx=tail.x-pre_tail.x
            val tdy=tail.y-pre_tail.y
            tail.imgNb=verif_dir_queue(tdx,tdy)
        }





    }


    fun verif_dir(x: Int,y:Int):Int {
        if (x == 0) {
            // y==2 ou y == -2
            return 7
        }
        if (y == 0) {
            return 1
        }
        if (x == 1) {
            if (y == 1) {
                if (mouvement.x == 1) {
                    return 5
                } else {
                    return 2
                }
            } else {
                if (mouvement.x == 1) {
                    return 0
                } else {
                    return 12
                }

            }

        } else {
            if (y == 1) {
                if (mouvement.x == -1) {
                    return 12
                } else {
                    return 0
                }
            } else {
                if (mouvement.x == -1) {
                    return 2
                } else {
                    return 5
                }

            }

        }
    }

    fun verif_dir_queue(x: Int,y:Int):Int {
        if(x==1){
            return 18
        }else if(x==-1){
            return 14
        }else if(y==-1){
            return 19
        }else{
            return 13
        }

    }
fun manger_pomme():Boolean {
    for (b in snake) {
        if (pomme.x == b.x && pomme.y == b.y) {
            return true
        }
    }
    return false
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

        class SnakeBody(var x: Int, var y: Int, var imgNb: Int){

        }
        class Apple(var x: Int, var y: Int,var type: Int){

        }
        enum class SnakeMoves(val x:Int,val y:Int,val img:Int) {
            PAUSE(0,0,4),
            DROITE(1,0,4),
            HAUT(0,-1,3),
            BAS(0,1,9),
            GAUCHE(-1,0,8)
        }

    }
}