package com.example.myapplication.gl

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

abstract class GLESHelperActivity(@DrawableRes private val  mainTexture: Int, private val tiles: Int) : AppCompatActivity() {
    internal lateinit var glView : GLESHelperView
    private val displays = ArrayList<GLESHelperDisplay<*>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glView = object : GLESHelperView(this,
                resources.getDrawable(mainTexture, theme) as BitmapDrawable,
                tiles,
                resources.getDrawable(R.drawable.font, theme) as BitmapDrawable) {
            override fun clickFunc(v: View, e: MotionEvent): Boolean {
                for (i in displays.indices.reversed())
                    if (displays[i].click(e.x.toInt(), e.y.toInt()))
                        return true
                return onClick(v, e, glView.renderer)
            }

            override fun initFunc(renderer: GLESHelperRenderer) {
                displays.clear()
                onInit(renderer)
            }
            override fun drawFunc(renderer: GLESHelperRenderer) {
                for (d in displays)
                    d.render(renderer)
                onDraw(renderer)
            }
        }
        setContentView(glView)
    }

    fun <T : GLESHelperDisplay<*> >registerDisplay(d: T) : T{
        displays.add(d)
        return d
    }
    fun registerLabel(text: String, x: Int, y: Int, h: Int, back: Boolean, selected: Boolean): GLESHelperLabel {
        return registerDisplay(GLESHelperLabel(text, x, y, h, back, selected))
    }
    fun registerTexture(index: Int, x: Int, y: Int, w: Int) : GLESHelperTexturedSquare {
        return registerDisplay(GLESHelperTexturedSquare(index, x, y, w))
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
    abstract fun onInit(renderer: GLESHelperRenderer)
}