package com.example.myapplication.game

import android.content.Context
import android.opengl.GLES20
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class GLESUtils {
    companion object {
        fun allocateFloatBuffer(src: FloatArray): FloatBuffer {
            val buffer = ByteBuffer.allocateDirect(src.size * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer()
            buffer.put(src).position(0)
            return buffer
        }
        fun genSquareTexVertices(tiles: Int) : Array<FloatBuffer> {
            return Array(tiles * tiles) { id ->
                val i = id % tiles
                val j = id / tiles
                val u0 = i / tiles.toFloat()
                val v0 = j / tiles.toFloat()
                val u = (i + 1) / tiles.toFloat()
                val v = (j + 1) / tiles.toFloat()
                allocateFloatBuffer(
                    floatArrayOf(
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
        fun getShaderResult(shader: Int) : String? {
            val s = intArrayOf(0)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, s, 0)
            if (s[0] == 0)
                return GLES20.glGetShaderInfoLog(shader)
            return null
        }
        fun getProgramResult(prog: Int) : String? {
            val s = intArrayOf(0)
            GLES20.glGetProgramiv(prog, GLES20.GL_LINK_STATUS, s, 0)
            if (s[0] == 0)
                return GLES20.glGetShaderInfoLog(prog)
            return null
        }
        fun compileShader(shaderCode: String, shaderType: Int): Int {
            val shaderHandler = GLES20.glCreateShader(shaderType)
            GLES20.glShaderSource(shaderHandler, shaderCode)
            GLES20.glCompileShader(shaderHandler)
            val r = getShaderResult(shaderHandler)
            if (r != null) {
                GLES20.glDeleteShader(shaderHandler)
                throw RuntimeException(r)
            }
            return shaderHandler
        }

        fun linkProgram(vertexShaderHandle: Int, fragmentShaderHandle: Int, fields: Array<String>): Int {


            // Create a program object and store the handle to it.
            var programHandle = GLES20.glCreateProgram()
            if (programHandle != 0) {
                // Bind the vertex shader to the program.
                GLES20.glAttachShader(programHandle, vertexShaderHandle)

                // Bind the fragment shader to the program.
                GLES20.glAttachShader(programHandle, fragmentShaderHandle)

                // Bind attributes
                for ((i, s) in fields.withIndex())
                    GLES20.glBindAttribLocation(programHandle, i, s)

                // Link the two shaders together into a program.
                GLES20.glLinkProgram(programHandle)

                // Get the link status.
                val r = getProgramResult(programHandle)
                if (r != null)
                    throw RuntimeException(r)
            }
            return programHandle
        }
        fun getRawResource(activityContext: Context, id: Int) : String{
            val isr = InputStreamReader(activityContext.resources.openRawResource(id))
            val br = BufferedReader(isr)
            var line: String
            val content = StringBuilder()

            try {
                while (br.readLine().also{ line = it } != null) {
                    content.append(line)
                    content.append('\n')
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            return content.toString()
        }

        fun isInRec(mx:Int, my: Int, x: Int, y: Int, w: Int, h: Int): Boolean {
            return mx >= x && mx <= x + w && my >= y && my <= y + h
        }
        fun isInSquare(mx:Int, my: Int, x: Int, y: Int, w: Int): Boolean {
            return isInRec(mx, my, x, y, w, w)
        }
    }
}