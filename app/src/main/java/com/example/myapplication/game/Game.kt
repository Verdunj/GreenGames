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

    fun loadTextureImage(drawable: BitmapDrawable) : Int {
        // create texture
        val textures = IntArray(1){0}
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])

        // load pixels
        val bitmap = drawable.toBitmap()
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        return textures[0]
    }

}