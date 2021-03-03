package com.example.myapplication.game

import android.graphics.drawable.BitmapDrawable
import com.example.myapplication.R
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class DemineurGame: Game("Demineur", R.id.demineurActivity) {

    var fini = false
    var gagne = false
    var menu = DemineurGameMenu.NEW
    var time = 0L
    var remaining = 0
    var flag = 0
    var difficulty = DemineurGameDifficulty.NORMAL
    var digMode = true
    lateinit var drawable : BitmapDrawable
    lateinit var fontDrawable : BitmapDrawable
    lateinit var cells : Array<Array<DemineurGameCell>>


    fun start() {
        cells = Array(difficulty.width) { Array(difficulty.height) { DemineurGameCell(this) } }
        // set the bombs
        for (i in 1..difficulty.bombs) {
            var c: DemineurGameCell
            var x: Int
            var y: Int
            do {
                x = Random.nextInt(difficulty.width)
                y = Random.nextInt(difficulty.height)
                c = cells[x][y]
            } while (c.bomb)
            // add to the neighbors
            for (xx in max(0,x-1)..min(difficulty.width-1,x+1))
                for (yy in max(0,y-1)..min(difficulty.height-1,y+1))
                    cells[xx][yy].number++ // we don't care about c because we won't use the number value
            c.bomb = true
        }
        time = System.currentTimeMillis()
        menu = DemineurGameMenu.INGAME
        fini = false
        gagne = false
        remaining = difficulty.width * difficulty.height - difficulty.bombs
    }

    fun loadTex() : Int {
        return loadTextureImage(drawable)
    }
    fun loadTexFont() : Int {
        return loadTextureImage(fontDrawable)
    }

    fun press(x: Int, y: Int) {
        // out of range or game ended
        if (fini || x < 0 || x >= difficulty.width || y < 0 || y >= difficulty.height)
            return
        val c = cells[x][y]
        // check if the cell is valid
        if (c.userState == DemineurGameCellUser.PRESSED)
            return

        // press depending of the mode
        if (digMode) {
            if (c.press())
                fini = true
            else if (c.userState == DemineurGameCellUser.PRESSED) { // not a bomb and pressed
                remaining--
                if (remaining == 0 && flag == difficulty.bombs) {
                    fini = true
                    gagne = true
                } else if (c.number == 0) {
                    // press all the neighbor if the number is 0
                    for (xx in x - 1..x + 1)
                        for (yy in y - 1..y + 1)
                            if (!(xx == x && yy == y))
                                press(xx, yy)
                }
            }
        } else
            c.pressRight()
    }

    companion object {
        enum class DemineurGameDifficulty(val title: String, val width: Int, val height: Int, val bombs: Int) {

            EASY("Facile", 7, 10, 12),
            NORMAL("Normal", 10, 13, 17),
            HARD("Difficile", 12, 15, 25);

        }
        enum class DemineurGameMenu {
            INGAME,
            NEW
        }
        class DemineurGameCell(val demineur: DemineurGame) {
            var userState = DemineurGameCellUser.UNPRESSED
            var bomb = false
            var number = 0

            /**
             * press
             * @return if the game must be ended
             */
            fun press() : Boolean {
                if (userState == DemineurGameCellUser.UNPRESSED) {
                    if (!bomb)
                        userState = DemineurGameCellUser.PRESSED
                    return bomb
                }
                return false
            }
            fun pressRight() {
                if (userState != DemineurGameCellUser.PRESSED) {
                    if (userState == DemineurGameCellUser.FLAG)
                        demineur.flag--
                    userState = DemineurGameCellUser.values()[(userState.ordinal + 1) % DemineurGameCellUser.PRESSED.ordinal]
                    if (userState == DemineurGameCellUser.FLAG)
                        demineur.flag++
                }
            }

            fun getImageIndex(): Int {
                if (userState == DemineurGameCellUser.PRESSED)
                    return 4 + number
                return userState.ordinal
            }
        }
        enum class DemineurGameCellUser {
            UNPRESSED,
            FLAG,
            UNKNOW,
            PRESSED
        }
    }
}