package com.example.myapplication

import android.view.MotionEvent
import android.view.View
import com.example.myapplication.game.DemineurGame
import com.example.myapplication.gl.GLESHelperActivity
import com.example.myapplication.gl.GLESHelperRenderer
import com.example.myapplication.gl.GLESUtils
import java.lang.RuntimeException
import kotlin.math.max
import kotlin.math.min

class DemineurActivity : GLESHelperActivity(R.drawable.demineur, 4) {
    val demineur = GameData.core.findGameByClass(DemineurGame::class) ?: throw RuntimeException("Can't find game")
    private var tileSize = 1
    private var startX = 0
    private var startY = 0

    override fun onClick(v: View, e: MotionEvent, renderer: GLESHelperRenderer): Boolean {
        when (demineur.menu) {
            DemineurGame.Companion.DemineurGameMenu.NEW -> {
                val diffs = DemineurGame.Companion.DemineurGameDifficulty.values()

                var top = (renderer.height - tileSize * diffs.size * 3 / 2) / 2
                for (m in diffs) {
                    if (GLESUtils.isInRec(e.x.toInt(), e.y.toInt(), (renderer.width - tileSize * m.title.length) / 2, top - tileSize / 2, tileSize * m.title.length, tileSize))
                        demineur.difficulty = m
                    top += tileSize * 3 / 2
                }

                if (GLESUtils.isInRec(e.x.toInt(), e.y.toInt(), (renderer.width - tileSize*"Lancer".length) / 2, renderer.height - tileSize * 3 / 2, tileSize * "Lancer".length, tileSize))
                    demineur.start()
            }
            DemineurGame.Companion.DemineurGameMenu.INGAME -> {
                // check default buttons
                if (GLESUtils.isInSquare(e.x.toInt(), e.y.toInt(), renderer.width / 2 - tileSize * 6 / 2, renderer.height - tileSize * 5 / 2,tileSize * 2)) {
                    // flag
                    demineur.digMode = false
                } else if (GLESUtils.isInSquare(e.x.toInt(), e.y.toInt(), renderer.width / 2 - tileSize * 2 / 2,renderer.height - tileSize * 5 / 2,tileSize * 2)) {
                    // pause
                    demineur.menu = DemineurGame.Companion.DemineurGameMenu.NEW
                } else if (GLESUtils.isInSquare(e.x.toInt(), e.y.toInt(), renderer.width / 2 + tileSize * 2 / 2,renderer.height - tileSize * 5 / 2,tileSize * 2)) {
                    // dig
                    demineur.digMode = true
                } else // press on the game
                    demineur.press(
                            (e.x.toInt() - startX) / tileSize,
                            (e.y.toInt() - startY) / tileSize
                    )

            }
        }
        return true
    }

    override fun onDraw(glesHelper: GLESHelperRenderer) {
        val width = glesHelper.width
        val height = glesHelper.height
        when (demineur.menu) {
            DemineurGame.Companion.DemineurGameMenu.NEW -> {
                var nm = max("Difficulte".length, "Lancer".length)
                val diffs = DemineurGame.Companion.DemineurGameDifficulty.values()
                for (m in diffs)
                    if (m.title.length > nm)
                        nm = m.title.length
                val wr = width / nm
                val hr = height / (diffs.size * 3 / 2 + 4)

                tileSize = min(wr, hr)

                glesHelper.renderStringCenteredxy("Difficulte", width / 2, tileSize, tileSize)

                var top = (glesHelper.height - tileSize * diffs.size * 3 / 2) / 2
                for (m in diffs) {
                    glesHelper.renderStringCenteredxy(m.title, width / 2, top, tileSize, true, m == demineur.difficulty)
                    top += tileSize * 3 / 2
                }

                glesHelper.renderStringCenteredxy("Lancer", width / 2, height - tileSize, tileSize, true, false)

            }
            DemineurGame.Companion.DemineurGameMenu.INGAME -> {
                val wr = width / demineur.difficulty.width
                val hr = height / (demineur.difficulty.height + 6)

                tileSize = min(wr, hr)
                startX = width / 2 - tileSize * demineur.difficulty.width / 2
                startY = height / 2 - tileSize * (demineur.difficulty.height) / 2
                // render top text
                glesHelper.renderStringCenteredxy(demineur.flag.toString() + " / " + demineur.difficulty.bombs, glesHelper.width / 2, tileSize, tileSize / 2)
                if (demineur.fini) {
                    if (demineur.gagne)
                        glesHelper.renderStringCenteredxy("gagne !", width / 2, tileSize * 2, tileSize / 2)
                    else
                        glesHelper.renderStringCenteredxy("perdu !", width / 2, tileSize * 2, tileSize / 2)
                }

                for ((x, cc) in demineur.cells.withIndex())
                    for ((y, c) in cc.withIndex()) {
                        glesHelper.renderTex(
                                c.getImageIndex(),
                                startX + x * tileSize,
                                startY + y * tileSize,
                                tileSize,
                                tileSize
                        )
                        // render the bomb on top of the image
                        if (demineur.fini && !demineur.gagne && c.bomb)
                            glesHelper.renderTex(
                                    3,
                                    startX + x * tileSize,
                                    startY + y * tileSize,
                                    tileSize,
                                    tileSize
                            )
                    }

                // menu tile
                glesHelper.renderTex(if(demineur.digMode) 0 else 4, width / 2 - tileSize * 6 / 2, height - tileSize * 5 / 2, tileSize * 2, tileSize * 2)
                glesHelper.renderTex(0, glesHelper.width / 2 - tileSize * 2 / 2, height - tileSize * 5 / 2, tileSize * 2, tileSize * 2)
                glesHelper.renderTex(if(demineur.digMode) 4 else 0, width / 2 + tileSize * 2 / 2, height - tileSize * 5 / 2, tileSize * 2, tileSize * 2)

                // menu icon
                glesHelper.renderTex(13, width / 2 - tileSize * 6 / 2, height - tileSize * 5 / 2, tileSize * 2, tileSize * 2)
                glesHelper.renderTex(15, width / 2 - tileSize * 2 / 2, height - tileSize * 5 / 2, tileSize * 2, tileSize * 2)
                glesHelper.renderTex(14, width / 2 + tileSize * 2 / 2, height - tileSize * 5 / 2, tileSize * 2, tileSize * 2)

            }
        }
    }
}