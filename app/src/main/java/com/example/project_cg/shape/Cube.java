package com.example.project_cg.shape;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Cube extends Shape {
    private FloatBuffer vertexBuffer, colorBuffer;
    private ShortBuffer indexBuffer;
    private float length, width, height;

    private float[] vertex;
    private float[] color;
    private int mProgram;

    private float[] mMVPMatrix;

    private int mMatrixHandler;

    // Use triangles to draw a cube.
    private final short[] triangleMap = {
            6, 7, 4, 6, 5, 4,    // back
            1, 2, 5, 5, 2, 6,    // right
            0, 1, 4, 1, 4, 5,    // bottom
            0, 1, 2, 0, 2, 3,    // front
            0, 3, 4, 4, 3, 7,    // left
            2, 3, 7, 2, 7, 6,    // top
    };

    public Cube(float baseX, float baseY, float baseZ, float length, float width, float height, float r, float g, float b, float a) {
        mMVPMatrix = new float[16];

        this.length = length;
        this.width = width;
        this.height = height;

        vertex = new float[24];
        int i, j, k;
        for (i = 0; i < 2; i++) {
            for (j = 0; j < 2; j++) {
                for (k = 0; k < 2; k++) {
                    vertex[(4 * i + 2 * j + k) * 3] = baseX + length * ((j == k) ? -0.5f : 0.5f);
                    vertex[(4 * i + 2 * j + k) * 3 + 1] = baseY + width * ((j == 0) ? -0.5f : 0.5f);
                    vertex[(4 * i + 2 * j + k) * 3 + 2] = baseZ + height * ((i == 1) ? -0.5f : 0.5f);
                }
            }
        }

        /*
        color = new float[32];
        for (i = 0; i < 8; i++) {
            color[4 * i] = r;
            color[4 * i + 1] = g;
            color[4 * i + 2] = b;
            color[4 * i + 3] = a;
        }
         */

        // test
        color = new float[]{
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 1f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
                1f, 0f, 0f, 1f,
        };
        ByteBuffer bb = ByteBuffer.allocateDirect(vertex.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertex);
        vertexBuffer.position(0);

        ByteBuffer dd = ByteBuffer.allocateDirect(color.length * 4);
        dd.order(ByteOrder.nativeOrder());
        colorBuffer = dd.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);
        ByteBuffer cc = ByteBuffer.allocateDirect(triangleMap.length * 2);
        cc.order(ByteOrder.nativeOrder());
        indexBuffer = cc.asShortBuffer();
        indexBuffer.put(triangleMap);
        indexBuffer.position(0);
        String vertexShaderCode = "attribute vec4 vPosition;" +
                "uniform mat4 vMatrix;" +
                "varying  vec4 vColor;" +
                "attribute vec4 aColor;" +
                "void main() {" +
                "  gl_Position = vMatrix*vPosition;" +
                "  vColor=aColor;" +
                "}";
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        String fragmentShaderCode = "precision mediump float;" +
                "varying vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}";
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public float getLength() {
        return length;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // enable depth test to see the depth of object
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glUseProgram(mProgram);
        // 获取变换矩阵vMatrix成员句柄
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        //获取顶点着色器的vPosition成员句柄
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        //获取片元着色器的vColor成员的句柄
        int mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //设置绘制三角形的颜色
        //GLES20.glUniform4fv(mColorHandle, 2, color, 0);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
        //索引法绘制正方体
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, triangleMap.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    @Override
    public void setMVPMatrix(float[] MVPMatrix) {
        mMVPMatrix = MVPMatrix;
    }
}
