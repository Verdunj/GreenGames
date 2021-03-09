package com.example.myapplication.gl

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min

class GLESHelperRenderer(private val mainTexture: BitmapDrawable, tiles: Int, private val fontTexture: BitmapDrawable, private val drawFunct: (GLESHelperRenderer) -> Unit, private val initFunct: (GLESHelperRenderer) -> Unit): GLSurfaceView.Renderer {
    var width = 1
    var height = 1

    var fontBackTextureId = 0
    var fontBackSelectedTextureId = 4

    private val mMVPMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mModelMatrix = FloatArray(16)

    private val squareVertices = GLESUtils.genSquareVertices()
    private val squareTexVertices = GLESUtils.genSquareTexVertices(tiles)
    private val fontTexVertices = GLESUtils.genSquareTexVertices(16)

    private var mMVPHandle = 0
    private var mPositionHandle = 0
    private var mTextureCoordinateHandle = 0
    private var mProgramHandle = 0

    private var mTextureDataHandle = 0
    private var mFontTextureDataHandle = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set the background clear color to black.
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        // compile the shader into a program
        val vertexShaderHandle: Int = GLESUtils.compileShader(
                """
                            attribute vec4 a_Position;
                            attribute vec2 a_TexCoordinate;
                            varying vec2 texture_coordinates;
                            uniform mat4 MVP;
                            
                            void main() {
                                gl_Position = MVP * a_Position;
                                texture_coordinates = a_TexCoordinate;
                            }
                        """.trimIndent()

                , GLES20.GL_VERTEX_SHADER)
        val fragmentShaderHandle: Int = GLESUtils.compileShader("""
                            precision mediump float;
                            varying vec2 texture_coordinates;
                            uniform sampler2D u_Texture;

                            void main() {
                                gl_FragColor = texture2D(u_Texture, texture_coordinates);
                            }
                        """.trimIndent(), GLES20.GL_FRAGMENT_SHADER)
        mProgramHandle = GLESUtils.linkProgram(vertexShaderHandle, fragmentShaderHandle, arrayOf("a_Position", "a_TexCoordinate"))

        // Set program handles for cube drawing.
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position")
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate")
        mMVPHandle = GLES20.glGetUniformLocation(mProgramHandle, "MVP")
        // Load the texture
        mTextureDataHandle = GLESUtils.loadTextureImage(mainTexture)
        mFontTextureDataHandle = GLESUtils.loadTextureImage(fontTexture)
    }

    fun renderStringCenteredxy(s: String, x: Int, y: Int, h: Int) {
        renderStringCenteredxy(s, x, y, h, false, false)
    }
    fun renderStringCenteredxy(s: String, x: Int, y: Int, h: Int, back: Boolean, selected: Boolean) {
        renderString(s, x - s.length * h  / 2, y - h / 2, h, back, selected)
    }
    fun renderString(s: String, x: Int, y: Int, h: Int) {
        renderString(s, x, y, h, false, false)
    }
    fun renderString(s: String, x: Int, y: Int, h: Int, back: Boolean, selected: Boolean) {
        if (back) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle)
            val key = if (selected) fontBackSelectedTextureId else fontBackTextureId
            var xx = x
            for (c in s) {
                renderTex(key, xx, y, h, h)
                xx += h
            }
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFontTextureDataHandle)
        var xx = x
        for (c in s) {
            renderChar(c, xx, y, h)
            xx += h
        }
    }

    fun renderString(s: String, x: Int, y: Int, h: Int, key:Int) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle)
        var xx = x
        for (c in s) {
            renderTex(key, xx, y, h, h)
            xx += h
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFontTextureDataHandle)
        xx = x
        for (c in s) {
            renderChar(c, xx, y, h)
            xx += h
        }
    }

    fun renderTex(index: Int, x: Int, y: Int, w: Int, h: Int) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle)
        renderTex(squareTexVertices[index], x, y, w, h)
    }
    private fun renderChar(code: Char, x: Int, y: Int, h: Int) {
        renderTex(fontTexVertices[min(max(code.toInt(), 0), 255)], x, y, h, h)
    }
    /**
     * Draws a cube.
     */
    private fun renderTex(vertices: FloatBuffer, x: Int, y: Int, w: Int, h: Int) {
        // Pass in the position information
        squareVertices.position(0)
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                0, squareVertices)
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        // Pass in the texture coordinate information

        val texVertices = vertices
        texVertices.position(0)
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                0, texVertices)
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)

        // mMVPMatrix = mViewMatrix * mModelMatrix
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        Matrix.translateM(mMVPMatrix, 0, x.toFloat(), y.toFloat(), 0f)
        Matrix.scaleM(mMVPMatrix, 0, w.toFloat(), h.toFloat(), 1f)
        // pass the proj matrix
        GLES20.glUniformMatrix4fv(mMVPHandle, 1, false, mMVPMatrix, 0)
        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)
        GLES20.glUseProgram(mProgramHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height)
        this.width = width
        this.height = height
        Matrix.orthoM(mViewMatrix, 0, 0f, width.toFloat(), height.toFloat(), 0f, -1f, 1f)
        Matrix.setIdentityM(mModelMatrix, 0)
        initFunct(this)
    }
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLESUtils.enableAlpha()
        drawFunct(this)
        GLESUtils.disableAlpha()
    }
}