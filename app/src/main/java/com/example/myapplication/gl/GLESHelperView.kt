package com.example.myapplication.gl

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.View

abstract class GLESHelperView(context: Context, mainTexture: BitmapDrawable, tiles: Int, fontTexture: BitmapDrawable) : GLSurfaceView(context), View.OnTouchListener {
    internal val renderer = GLESHelperRenderer(mainTexture, tiles, fontTexture, this::drawFunc, this::initFunc)

    init {

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)

        setOnTouchListener(this)
    }

    abstract fun drawFunc(renderer: GLESHelperRenderer)
    abstract fun clickFunc(v: View, e: MotionEvent): Boolean
    abstract fun initFunc(renderer: GLESHelperRenderer)

    override fun onTouch(v: View, e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN && clickFunc(v, e)) {

            return true
        }
        return false
    }
}