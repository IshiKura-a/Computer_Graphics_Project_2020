package com.example.project_cg.shape;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import com.example.project_cg.observe.Light;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shader.ShaderMap;
import com.example.project_cg.shader.ShaderType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Cube extends Shape {
    private final FloatBuffer vertexBuffer;
    private final FloatBuffer colorBuffer;
    private final FloatBuffer normalBuffer;
    private final ShortBuffer indexBuffer;
    private final float[] model;
    private float shininess;
    private float[] kAmbient, kDiffuse, kSpecular;
    // Use triangles to draw a cube.
    private final short[] triangleMap = {
            0, 1, 2, 2, 3, 0,    // front
            4, 5, 6, 6, 7, 4,    // back
            8, 9, 10, 10, 11, 8,    // left
            12, 13, 14, 14, 15, 12,    // right
            16, 17, 18, 18, 19, 16,    // top
            20, 21, 22, 22, 23, 20,    // bottom
    };
    private float length;
    private float width;
    private int mProgram;
    private float height;

    public Cube(float[] base, float length, float width, float height, float[] rgba, float[] ambient, float[] diffuse, float[] specular, float shininess) {
        kAmbient = ambient.clone();
        kDiffuse = diffuse.clone();
        kSpecular = specular.clone();

        this.shininess = shininess;
        this.length = length;
        this.width = width;
        this.height = height;

        model = new float[]{
                length, 0, 0, base[0] / base[3],
                0, width, 0, base[1] / base[3],
                0, 0, height, base[2] / base[3],
                0, 0, 0, 1
        };

        float[] normalTemp = {
                1, 0, 0, 1, -1, 0, 0, 1,
                0, -1, 0, 1, 0, 1, 0, 1,
                0, 0, 1, 1, 0, 0, -1, 1
        };

        String vertexTemp = "+--++-++++-+" +
                "----+--++--+" +
                "---+--+-+--+" +
                "-+-++-+++-++" +
                "+-++++-++--+" +
                "+--++--+----";
        int i;
        float[] normal = new float[96];
        for (i = 0; i < 24; i++) {
            int indexL = 4 * i;
            int indexR = (i / 4) * 4;
            normal[indexL] = normalTemp[indexR];
            normal[indexL + 1] = normalTemp[indexR + 1];
            normal[indexL + 2] = normalTemp[indexR + 2];
            normal[indexL + 3] = normalTemp[indexR + 3];
        }

        float[] vertex = new float[96];
        for (i = 0; i < 24; i++) {
            int index = 4 * i;
            vertex[index] = vertexTemp.charAt(3 * i) == '-' ? -0.5f : 0.5f;
            vertex[index + 1] = vertexTemp.charAt(3 * i + 1) == '-' ? -0.5f : 0.5f;
            vertex[index + 2] = vertexTemp.charAt(3 * i + 2) == '-' ? -0.5f : 0.5f;
            vertex[index + 3] = 1f;
        }


        float[] color = new float[96];
        for (i = 0; i < 24; i++) {
            color[4 * i] = rgba[0];
            color[4 * i + 1] = rgba[1];
            color[4 * i + 2] = rgba[2];
            color[4 * i + 3] = rgba[3];
        }

        ByteBuffer vBB = ByteBuffer.allocateDirect(vertex.length * 4);
        vBB.order(ByteOrder.nativeOrder());
        vertexBuffer = vBB.asFloatBuffer();
        vertexBuffer.put(vertex);
        vertexBuffer.position(0);

        ByteBuffer cBB = ByteBuffer.allocateDirect(color.length * 4);
        cBB.order(ByteOrder.nativeOrder());
        colorBuffer = cBB.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        ByteBuffer nBB = ByteBuffer.allocateDirect(normal.length * 4);
        nBB.order(ByteOrder.nativeOrder());
        normalBuffer = nBB.asFloatBuffer();
        normalBuffer.put(normal);
        normalBuffer.position(0);

        ByteBuffer tBB = ByteBuffer.allocateDirect(triangleMap.length * 2);
        tBB.order(ByteOrder.nativeOrder());
        indexBuffer = tBB.asShortBuffer();
        indexBuffer.put(triangleMap);
        indexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                ShaderMap.get("shape", ShaderType.VERT));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                ShaderMap.get("shape", ShaderType.FRAG));

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

        Log.e("Compile Error: ", GLES20.glGetShaderInfoLog(shader));
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

        // get uniform handlers
        int uModelHandler = GLES20.glGetUniformLocation(mProgram, "uModel");
        int uViewHandler = GLES20.glGetUniformLocation(mProgram, "uView");
        int uProjectionHandler = GLES20.glGetUniformLocation(mProgram, "uProjection");
        int uLightPositionHandler = GLES20.glGetUniformLocation(mProgram, "uLightPosition");
        int uCameraPositionHandler = GLES20.glGetUniformLocation(mProgram, "uCameraPosition");
        int uShininessHandler = GLES20.glGetUniformLocation(mProgram, "uShininess");
        int uLightSpecularHandler = GLES20.glGetUniformLocation(mProgram, "uLightSpecular");
        int uLightDiffuseHandler = GLES20.glGetUniformLocation(mProgram, "uLightDiffuse");
        int uLightAmbientHandler = GLES20.glGetUniformLocation(mProgram, "uLightAmbient");
        int uMaterialSpecularHandler = GLES20.glGetUniformLocation(mProgram, "uMaterialSpecular");
        int uMaterialDiffuseHandler = GLES20.glGetUniformLocation(mProgram, "uMaterialDiffuse");
        int uMaterialAmbientHandler = GLES20.glGetUniformLocation(mProgram, "uMaterialAmbient");

        Light light = Observe.getLightList().get(0);
        // set uniform data
        GLES20.glUniformMatrix4fv(uModelHandler, 1, false, model, 0);
        GLES20.glUniformMatrix4fv(uViewHandler, 1, false, Observe.getViewMatrix(), 0);
        GLES20.glUniformMatrix4fv(uProjectionHandler, 1, false, Observe.getProjectionMatrix(), 0);
        GLES20.glUniform4fv(uLightPositionHandler, 1, light.getLocation(), 0);
        GLES20.glUniform4fv(uCameraPositionHandler, 1, Observe.getCamera().getEye(), 0);
        GLES20.glUniform1f(uShininessHandler, shininess);
        GLES20.glUniform4fv(uLightSpecularHandler, 1, light.getSpecular(), 0);
        GLES20.glUniform4fv(uLightDiffuseHandler, 1, light.getDiffuse(), 0);
        GLES20.glUniform4fv(uLightAmbientHandler, 1, light.getAmbient(), 0);
        GLES20.glUniform4fv(uMaterialSpecularHandler, 1, kSpecular, 0);
        GLES20.glUniform4fv(uMaterialDiffuseHandler, 1, kDiffuse, 0);
        GLES20.glUniform4fv(uMaterialAmbientHandler, 1, kAmbient, 0);


        // get attribute handlers
        int aVertexPositionHandle = GLES20.glGetAttribLocation(mProgram, "iVertexPosition");
        int aColorHandle = GLES20.glGetAttribLocation(mProgram, "iColor");
        int aNormalHandle = GLES20.glGetAttribLocation(mProgram, "iNormal");
        // Log.i("Handler: ", aVertexPositionHandle+":"+aColorHandle+":"+aNormalHandle);
        // set attribute handlers
        GLES20.glEnableVertexAttribArray(aVertexPositionHandle);
        GLES20.glVertexAttribPointer(aVertexPositionHandle, 4, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(aColorHandle);
        GLES20.glVertexAttribPointer(aColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        GLES20.glEnableVertexAttribArray(aNormalHandle);
        GLES20.glVertexAttribPointer(aNormalHandle, 4, GLES20.GL_FLOAT, false, 0, normalBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, triangleMap.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        GLES20.glDisableVertexAttribArray(aVertexPositionHandle);
    }

}
