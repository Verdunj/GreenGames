package com.example.myapplication.game

import android.graphics.drawable.BitmapDrawable
import android.opengl.GLES20
import android.opengl.GLUtils
import androidx.core.graphics.drawable.toBitmap
import com.example.myapplication.R
import java.util.concurrent.atomic.AtomicInteger

open class Game(_name: String, _fragmentId : Int) {
    companion object {
        val game_id = AtomicInteger()
    }
    val name = _name
    val id = game_id.getAndIncrement()
    val fragmentId = _fragmentId

    fun onLoad() {}
}