package com.example.myapplication.gl

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.opengl.GLSurfaceView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat

abstract class GLESHelperView(context: Context, mainTexture: BitmapDrawable, tiles: Int, fontTexture: BitmapDrawable) : GLSurfaceView(context), View.OnTouchListener, GestureDetector.OnGestureListener {
    internal val renderer = GLESHelperRenderer(mainTexture, tiles, fontTexture, this::drawFunc, this::initFunc)
    var gestureDetector= GestureDetectorCompat(context,this)
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100
    init {

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)

        setOnTouchListener(this)
    }

    abstract fun drawFunc(renderer: GLESHelperRenderer)
    abstract fun clickFunc(e: MotionEvent): Boolean
    abstract fun initFunc(renderer: GLESHelperRenderer)
    abstract fun onSwipeRight()
    abstract fun onSwipeLeft()
    abstract fun onSwipeUp()
    abstract fun onSwipeDown()

    override fun onTouch(v: View, e: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(e)
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        try {
            val diffY = e2?.getY()!! - e1?.getY()!!
            val diffX = e2.getX() - e1.getX()
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeDown()
                    } else {
                        onSwipeUp()
                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }


        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return clickFunc(e!!)
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return true
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return true
    }
}