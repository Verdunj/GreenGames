package com.example.myapplication.gl

abstract class GLESHelperDisplay<T : GLESHelperDisplay<T>> {
    private var show = {true}
    private var click = {}

    fun renderableAction(action: (() -> Boolean)): T {
        show = action
        return this as T
    }
    fun clickableAction(action: (() -> Unit)): T {
        click = action
        return this as T

    }

    fun click(mx: Int, my: Int): Boolean {
        if(show() && isIn(mx, my)) {
            click()
            return true
        }
        return false
    }

    fun render(renderer: GLESHelperRenderer) {
        if (show())
            render0(renderer)
    }

    protected abstract fun render0(renderer: GLESHelperRenderer)
    protected open fun isIn(mx: Int, my: Int): Boolean {
        return false
    }
}