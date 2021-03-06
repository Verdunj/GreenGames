package com.example.myapplication.gl

data class GLESHelperLabel(var title: String, var x: Int, var y: Int, var h: Int, var back: Boolean, var selected: Boolean) : GLESHelperDisplay<GLESHelperLabel>(){
    override fun render0(renderer: GLESHelperRenderer) {
        renderer.renderString(title, x - title.length * h / 2, y - h / 2, h, back, selected)
    }
    override fun isIn(mx: Int, my: Int): Boolean {
        return GLESUtils.isInRec(mx, my, x - title.length * h / 2, y - h / 2, title.length * h, h)
    }
}