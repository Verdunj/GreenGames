package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.example.myapplication.game.DemineurGame
import com.example.myapplication.game.SudokuGame
import com.example.myapplication.gl.GLESHelperActivity
import com.example.myapplication.gl.GLESHelperLabel
import com.example.myapplication.gl.GLESHelperRenderer
import com.example.myapplication.gl.GLESHelperTexturedSquare
import java.lang.RuntimeException
import kotlin.math.min

class SudokuActivity : GLESHelperActivity(R.drawable.sudoku, 5) {
    private val game = GameData.core.findGameByClass(SudokuGame::class) ?: throw RuntimeException("Can't find game")
    private val title = GLESHelperLabel("Sudoku", 0, 0, 0, false, false)
    private val cells = Array(9) {i -> Array(9){ j -> game.cells[i][j].renderer.apply {
        clickableAction {
            if (game.selectedCell == null && !game.end) {
                val c = game.cells[i][j]
                if (c.updatable) {
                    game.selectedCell = c
                    selectCell[(c.number - 1 + 10) % 10].index = c.number + 10
                }
            }
        }
    }}}
    private val pause = GLESHelperTexturedSquare(20,0,0,0).apply {
        clickableAction {
            // TODO: Pause button
        }
    }
    private val background = GLESHelperTexturedSquare(23, 0, 0, 0)
    private val selectBackground = GLESHelperTexturedSquare(22, 0, 0, 0)
    private val selectCell = Array(10) {id ->
        GLESHelperTexturedSquare((id + 1) % 10,0,0,0)
    }
    init {
        selectCell.forEachIndexed{
            id, tex ->
            tex.apply{
                renderableAction { game.selectedCell != null }
                clickableAction {
                    val c = game.selectedCell!!
                    selectCell[(c.number - 1 + 10) % 10].index = c.number
                    c.number = (id + 1) % 10
                    game.checkGameEnd()
                    // remove the color of the cell
                    index = (id + 1) % 10
                    game.selectedCell = null
                }
            }
        }
    }

    private var tileSize = 1
    private var startX = 0
    private var startY = 0

    override fun onDraw(renderer: GLESHelperRenderer) {
    }

    override fun onClick(v: View, e: MotionEvent, renderer: GLESHelperRenderer): Boolean {
        return false
    }

    override fun onInit(renderer: GLESHelperRenderer) {
        val width = renderer.width
        val height = renderer.height
        renderer.fontBackTextureId = 21
        renderer.fontBackSelectedTextureId = 22

        val wr = width / 9
        val hr = height / (9 + 4)

        tileSize = min(wr, hr)
        startX = (width - tileSize * 9) / 2
        startY = (height - tileSize * 9) / 2

        // add the background
        registerDisplay(background).apply {
            x = startX
            y = startY
            w = tileSize * 9
        }

        // set cells location
        for ((i, cc) in cells.withIndex())
            for ((j, c) in cc.withIndex())
                registerDisplay(c).apply {
                    w = tileSize * 19 / 20

                    // location in the area
                    val dx = (i % 3) * w
                    val dy = (j % 3) * w

                    // location of the top left of the area
                    val cx = startX + (i / 3) * tileSize * 3 + tileSize * 3 / 2 - w * 3 / 2
                    val cy = startY + (j / 3) * tileSize * 3 + tileSize * 3 / 2 - w * 3 / 2

                    x = cx + dx
                    y = cy + dy
                }

        // set title location
        registerDisplay(title).apply {
            x = width / 2
            y = tileSize
            h = tileSize
        }

        // set pause button
        registerDisplay(pause).apply {
            x = (width - tileSize) / 2
            y = height - tileSize * 3 / 2
            w = tileSize
        }

        // set selection background
        registerDisplay(selectBackground).apply {
            x = (width - tileSize * 17/2) / 2
            y = (height - tileSize * 17/2) / 2
            w = tileSize * 17 / 2
            renderableAction { game.selectedCell != null }
        }

        // set selection buttons
        for ((id, c) in selectCell.withIndex())
            registerDisplay(c).apply {
                x = (width - tileSize * 6) / 2 + tileSize * 2 * (id % 3)
                y = (height - tileSize * 8) / 2 + tileSize * 2 * (id / 3)
                w = tileSize * 2
            }

        // set the 0 number after
        selectCell[selectCell.size - 1].x += tileSize * 2
    }
}