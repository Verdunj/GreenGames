package com.example.myapplication.game

import android.content.Context
import android.opengl.GLES20
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * A class to help with the usage of the GLES20 class
 * @author Antoine W.
 */
class GLESUtils {
    init {
        throw RuntimeException(this::class.simpleName + " can't be instanced")
    }
    companion object {
        /** an Exception to store all the exceptions of this utility class */
        open class GLESUtilsException(msg: String) : RuntimeException(msg) {}
        /**
         * get when a compilation exception occurs
         * @see GLESUtils.compileShader
         */
        open class ShaderCompilationException(msg: String) : GLESUtilsException(msg) {}
        /**
         * get when a link exception occurs
         * @see GLESUtils.linkProgram
         */
        open class ProgramLinkException(msg: String) : GLESUtilsException(msg) {}
        /**
         * direct allocate a buffer from an array
         * @param src the array to put in the buffer
         * @return the buffer
         */
        fun allocateFloatBuffer(src: FloatArray): FloatBuffer {
            val buffer = ByteBuffer.allocateDirect(src.size * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer()
            buffer.put(src).position(0)
            return buffer
        }

        /**
         * compile the shader and return the handler
         * @param shaderCode the code of the shader
         * @param shaderType the type of the shader, example: GLES20.GL_FRAGMENT_SHADER, GLES20.GL_VERTEX_SHADER
         * @return shader handler
         * @throws ShaderCompilationException if the shader can't be compiled
         */
        fun compileShader(shaderCode: String, shaderType: Int): Int {
            // create the shader
            val shaderHandler = GLES20.glCreateShader(shaderType)
            // set the source code
            GLES20.glShaderSource(shaderHandler, shaderCode)
            // compile
            GLES20.glCompileShader(shaderHandler)

            // check compilation error
            val r = getShaderResult(shaderHandler)
            if (r != null) {
                GLES20.glDeleteShader(shaderHandler)
                throw ShaderCompilationException(r)
            }
            return shaderHandler
        }

        /**
         * disable the alpha blend
         */
        fun disableAlpha() {
            GLES20.glDisable(GLES20.GL_BLEND)
        }
        /**
         * enable the alpha blend
         */
        fun enableAlpha() {
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        }

        /**
         * check if the mouse is inside a rectangle
         * @param mx mouse x location
         * @param my mouse y location
         * @param x rect x location
         * @param y rect y location
         * @param w rect width
         * @param h rect height
         * @return if the mouse is inside the rect
         */
        fun isInRec(mx: Int, my: Int, x: Int, y: Int, w: Int, h: Int): Boolean {
            return mx >= x && mx <= x + w && my >= y && my <= y + h
        }
        /**
         * check if the mouse is inside a square
         * @param mx mouse x location
         * @param my mouse y location
         * @param x square x location
         * @param y square y location
         * @param w square width
         * @return if the mouse is inside the square
         */
        fun isInSquare(mx: Int, my: Int, x: Int, y: Int, w: Int): Boolean {
            return isInRec(mx, my, x, y, w, w)
        }

        /**
         * link the vertex and the fragment shader with some attributes in a program and return it
         * @param vertexShaderHandle the vertex shader handler
         * @param fragmentShaderHandle the fragment shader handler
         * @param attribs the attributes to bind
         * @throws ProgramLinkException if the program can't be linked
         */
        fun linkProgram(vertexShaderHandle: Int, fragmentShaderHandle: Int, attribs: Array<String>): Int {
            // Create a program object and store the handle to it.
            var programHandle = GLES20.glCreateProgram()
            if (programHandle != 0) {
                // Bind the vertex shader to the program.
                GLES20.glAttachShader(programHandle, vertexShaderHandle)

                // Bind the fragment shader to the program.
                GLES20.glAttachShader(programHandle, fragmentShaderHandle)

                // Bind attributes
                for ((i, s) in attribs.withIndex())
                    GLES20.glBindAttribLocation(programHandle, i, s)

                // Link the two shaders together into a program.
                GLES20.glLinkProgram(programHandle)

                // Get the link status.
                val r = getProgramResult(programHandle)
                if (r != null)
                    throw ProgramLinkException(r)
            }
            return programHandle
        }
        private fun getShaderResult(shader: Int): String? {
            val s = intArrayOf(0)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, s, 0)
            if (s[0] == 0)
                return GLES20.glGetShaderInfoLog(shader)
            return null
        }

        /**
         * generate n*n buffers into an array with tile corresponding
         * @param tiles n number of tiles
         * @return the buffers
         */
        fun genSquareTexVertices(tiles: Int): Array<FloatBuffer> {
            return Array(tiles * tiles) { id ->
                val i = id % tiles
                val j = id / tiles
                val u0 = i / tiles.toFloat()
                val v0 = j / tiles.toFloat()
                val u = (i + 1) / tiles.toFloat()
                val v = (j + 1) / tiles.toFloat()
                allocateFloatBuffer(
                        floatArrayOf(
                                // 1st triangle
                                u0, v,  // top left
                                u0, v0, // bottom left
                                u, v0,  // bottom right
                                // 2nd triangle
                                u, v0, // bottom right
                                u, v,  // top right
                                u0, v  // top left
                        )
                )
            }
        }
        /**
         * generate a buffers of vertices of a square
         * @return the buffer
         */
        fun genSquareVertices(): FloatBuffer {
            return GLESUtils.allocateFloatBuffer(floatArrayOf(
                    // 1st triangle
                    0f, 1f, 0.0f, // top left
                    0f, 0f, 0.0f, // bottom left
                    1f, 0f, 0.0f, // bottom right
                    // 2nd triangle
                    1f, 0f, 0.0f, // bottom right
                    1f, 1f, 0.0f, // top right
                    0f, 1f, 0.0f  // top left
            ))
        }
        private fun getProgramResult(prog: Int): String? {
            val s = intArrayOf(0)
            GLES20.glGetProgramiv(prog, GLES20.GL_LINK_STATUS, s, 0)
            if (s[0] == 0)
                return GLES20.glGetShaderInfoLog(prog)
            return null
        }
    }
}