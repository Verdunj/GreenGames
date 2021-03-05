package com.example.myapplication.gl

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.GameData
import com.example.myapplication.R
import com.example.myapplication.game.DemineurGame

abstract class GLESHelperActivity(@DrawableRes private val  mainTexture: Int, private val tiles: Int) : AppCompatActivity() {
    internal lateinit var glView : GLESHelperView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glView = object : GLESHelperView(this,
                resources.getDrawable(mainTexture, theme) as BitmapDrawable,
                tiles,
                resources.getDrawable(R.drawable.font, theme) as BitmapDrawable) {
            override fun clickFunc(v: View, e: MotionEvent): Boolean {
                return onClick(v, e, glView.renderer)
            }

            override fun drawFunc(renderer: GLESHelperRenderer) {
                onDraw(renderer)
            }
        }
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

    abstract fun onDraw(renderer: GLESHelperRenderer)
    abstract fun onClick(v: View, e: MotionEvent, renderer: GLESHelperRenderer): Boolean
}