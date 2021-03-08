package com.example.myapplication.gl

data class GLESHelperTexturedSquare(var index: Int, var x: Int, var y: Int, var w: Int) : GLESHelperDisplay<GLESHelperTexturedSquare>(){
    override fun render0(renderer: GLESHelperRenderer) {
        renderer.renderTex(index, x, y, w, w)
    }
    override fun isIn(mx: Int, my: Int): Boolean {
        return GLESUtils.isInRec(mx, my, x, y, w, w)
    }
}