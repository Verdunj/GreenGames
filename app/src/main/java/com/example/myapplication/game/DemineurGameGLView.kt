package com.example.myapplication.game

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.MotionEvent
import androidx.navigation.findNavController
import com.example.myapplication.DemineurActivity
import com.example.myapplication.R
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min


@SuppressLint("ClickableViewAccessibility")
class DemineurGameGLView(context: DemineurActivity, demineur: DemineurGame) : GLSurfaceView(context) {
    private val renderer: DemineurGameGLViewRenderer
    init {
        // TODO: remove that
        demineur.start()
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = DemineurGameGLViewRenderer(demineur, context)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)

        setOnTouchListener { v, e ->
            if (e.action == MotionEvent.ACTION_DOWN) {
                // check default buttons
                if (GLESUtils.isInSquare(e.x.toInt(), e.y.toInt(), width / 2 - renderer.tileSize * 6 / 2, height - renderer.tileSize * 5 / 2,renderer.tileSize * 2)) {
                    // flag
                    demineur.digMode = false
                } else if (GLESUtils.isInSquare(e.x.toInt(), e.y.toInt(), width / 2 - renderer.tileSize * 2 / 2,height - renderer.tileSize * 5 / 2,renderer.tileSize * 2)) {
                    // pause
                    context.findNavController(R.id.FirstFragment).navigate(R.id.optionGameFragment)
                } else if (GLESUtils.isInSquare(e.x.toInt(), e.y.toInt(), width / 2 + renderer.tileSize * 2 / 2,height - renderer.tileSize * 5 / 2,renderer.tileSize * 2)) {
                    // dig
                    demineur.digMode = true
                } else // press on the game
                    demineur.press(
                        (e.x.toInt() - renderer.startX) / renderer.tileSize,
                        (e.y.toInt() - renderer.startY) / renderer.tileSize
                    )
            }
            true
        }
    }

    companion object {
        class DemineurGameGLViewRenderer(private val demineur: DemineurGame, private val mActivityContext: Context) : GLSurfaceView.Renderer {
            private val squareVertices = GLESUtils.allocateFloatBuffer(floatArrayOf(
                    // positions
                    0f, 1f, 0.0f, // top left
                    0f, 0f, 0.0f, // bottom left
                    1f, 0f, 0.0f, // bottom right
                    // 2nd triangle
                    1f, 0f, 0.0f, // bottom right
                    1f, 1f, 0.0f, // top right
                    0f, 1f, 0.0f  // top left
            ))
            private val squareTexVertices = GLESUtils.genSquareTexVertices(4)
            private val fontTexVertices = GLESUtils.genSquareTexVertices(16)
            private val mMVPMatrix = FloatArray(16)
            private val mViewMatrix = FloatArray(16)
            private val mModelMatrix = FloatArray(16)
            private var mMVPHandle = 0
            private var mPositionHandle = 0
            private var mTextureCoordinateHandle = 0
            private var mProgramHandle = 0
            private var width = 1
            private var height = 1

            private var mTextureDataHandle = 0
            private var mFontTextureDataHandle = 0

            internal var tileSize = 1
            internal var startX = 0
            internal var startY = 0

            override fun onSurfaceCreated(glUnused: GL10?, config: EGLConfig?) {
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
                mTextureDataHandle = demineur.loadTex()
                mFontTextureDataHandle = demineur.loadTexFont()
            }

            override fun onSurfaceChanged(glUnused: GL10?, width: Int, height: Int) {
                // Set the OpenGL viewport to the same size as the surface.
                GLES20.glViewport(0, 0, width, height)
                this.width = width
                this.height = height
                Matrix.orthoM(mViewMatrix, 0, 0f, width.toFloat(), height.toFloat(), 0f, -1f, 1f)
                Matrix.setIdentityM(mModelMatrix, 0)
            }

            override fun onDrawFrame(glUnused: GL10?) {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

                val wr = width / demineur.difficulty.width
                val hr = height / (demineur.difficulty.height + 6)

                tileSize = min(wr, hr)
                startX = width / 2 - tileSize * demineur.difficulty.width / 2
                startY = height / 2 - tileSize * (demineur.difficulty.height) / 2


                // enable ALPHA
                GLES20.glEnable(GLES20.GL_BLEND)
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)


                // render top text
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFontTextureDataHandle)
                renderStringCenteredxy(demineur.flag.toString() + " / " + demineur.difficulty.bombs, width / 2, tileSize, tileSize / 2)
                if (demineur.fini) {
                    if (demineur.gagne)
                        renderStringCenteredxy("gagne !", width / 2, tileSize * 2, tileSize / 2)
                    else
                        renderStringCenteredxy("perdu !", width / 2, tileSize * 2, tileSize / 2)
                }
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle)

                for ((x, cc) in demineur.cells.withIndex())
                    for ((y, c) in cc.withIndex()) {
                        renderTex(
                            c.getImageIndex(),
                            startX + x * tileSize,
                            startY + y * tileSize,
                            tileSize,
                            tileSize
                        )
                        // render the bomb on top of the image
                        if (demineur.fini && !demineur.gagne && c.bomb)
                            renderTex(
                                3,
                                startX + x * tileSize,
                                startY + y * tileSize,
                                tileSize,
                                tileSize
                            )
                    }

                // menu tile
                renderTex(if(demineur.digMode) 0 else 4, width / 2 - tileSize * 6 / 2, height - tileSize * 5 / 2, tileSize * 2, tileSize * 2)
                renderTex(0, width / 2 - tileSize * 2 / 2, height - tileSize * 5 / 2, tileSize * 2, tileSize * 2)
                renderTex(if(demineur.digMode) 4 else 0, width / 2 + tileSize * 2 / 2, height - tileSize * 5 / 2, tileSize * 2, tileSize * 2)

                // menu icon
                renderTex(13, width / 2 - tileSize * 6 / 2, height - tileSize * 5 / 2, tileSize * 2, tileSize * 2)
                renderTex(15, width / 2 - tileSize * 2 / 2, height - tileSize * 5 / 2, tileSize * 2, tileSize * 2)
                renderTex(14, width / 2 + tileSize * 2 / 2, height - tileSize * 5 / 2, tileSize * 2, tileSize * 2)

                // disable ALPHA
                GLES20.glDisable(GLES20.GL_BLEND)
            }


            private fun renderTex(index: Int, x: Int, y: Int, w: Int, h: Int) {
                renderTex(squareTexVertices[index], x, y, w, h)
            }
            private fun renderStringCenteredxy(s: String, x: Int, y: Int, h: Int) {
                renderString(s, x - s.length * h  / 2, y - h / 2, h)
            }
            private fun renderString(s: String, x: Int, y: Int, h: Int) {
                var xx = x
                for (c in s) {
                    renderChar(c, xx, y, h)
                    xx += h
                }
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
        }
    }
}