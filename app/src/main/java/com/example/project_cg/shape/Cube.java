package com.example.project_cg.shape;

import android.opengl.GLES20;
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
    private FloatBuffer vertexBuffer, colorBuffer, normalBuffer;
    private ShortBuffer indexBuffer;
    private float length, width, height;
    private float shininess;
    private float[] kAmbient, kDiffuse, kSpecular;

    private float[] vertex;
    private float[] normal;
    private float[] color;
    private int mProgram;

    // Use triangles to draw a cube.
    private final short[] triangleMap = {
            6, 7, 4, 6, 5, 4,    // back
            1, 2, 5, 5, 2, 6,    // right
            0, 1, 4, 1, 4, 5,    // bottom
            0, 1, 2, 0, 2, 3,    // front
            0, 3, 4, 4, 3, 7,    // left
            2, 3, 7, 2, 7, 6,    // top
    };

    public Cube(float[] base, float length, float width, float height, float[] color, float[] ambient, float[] diffuse, float[] specular, float shininess) {
        kAmbient = ambient.clone();
        kDiffuse = diffuse.clone();
        kSpecular = specular.clone();

        this.shininess = shininess;
        this.length = length;
        this.width = width;
        this.height = height;

        vertex = new float[32];
        normal = new float[32];
        int i, j, k;
        for (i = 0; i < 2; i++) {
            for (j = 0; j < 2; j++) {
                for (k = 0; k < 2; k++) {
                    int index = (4 * i + 2 * j + k) * 4;
                    normal[index] = (i == 1) ? -length : length;
                    normal[index + 1] = (j == k) ? -width : width;
                    normal[index + 2] = (j == 0) ? -height : height;
                    normal[index + 3] = 1;

                    vertex[index] = base[0] / base[3] + length * ((i == 1) ? -0.5f : 0.5f);
                    vertex[index + 1] = base[1] / base[3] + width * ((j == k) ? -0.5f : 0.5f);
                    vertex[index + 2] = base[2] / base[3] + height * ((j == 0) ? -0.5f : 0.5f);
                    vertex[index + 3] = 1;
                }
            }
        }

        this.color = new float[32];
        for (i = 0; i < 8; i++) {
            this.color[4 * i] = color[0];
            this.color[4 * i + 1] = color[1];
            this.color[4 * i + 2] = color[2];
            this.color[4 * i + 3] = color[3];
        }

        ByteBuffer vBB = ByteBuffer.allocateDirect(vertex.length * 4);
        vBB.order(ByteOrder.nativeOrder());
        vertexBuffer = vBB.asFloatBuffer();
        vertexBuffer.put(vertex);
        vertexBuffer.position(0);

        ByteBuffer cBB = ByteBuffer.allocateDirect(this.color.length * 4);
        cBB.order(ByteOrder.nativeOrder());
        colorBuffer = cBB.asFloatBuffer();
        colorBuffer.put(this.color);
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
        int uMVPMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
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
        GLES20.glUniformMatrix4fv(uMVPMatrixHandler, 1, false, Observe.getMVPMatrix(), 0);
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
        int aVertexPositionHandle = GLES20.glGetAttribLocation(mProgram, "aVertexPosition");
        int aColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        int aNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");

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
