package com.example.myapplication

import android.graphics.drawable.BitmapDrawable
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.myapplication.game.DemineurGame
import com.example.myapplication.game.DemineurGameGLView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class DemineurActivity : AppCompatActivity() {
    lateinit var glView : DemineurGameGLView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demineur)
        val game = GameData.core.findGameByClass(DemineurGame::class) ?: return
        game.drawable = resources.getDrawable(R.drawable.demineur, theme) as BitmapDrawable
        game.fontDrawable = resources.getDrawable(R.drawable.font, theme) as BitmapDrawable
        glView = DemineurGameGLView(this, game)
        setContentView(glView)
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glView.onPause()
    }
}