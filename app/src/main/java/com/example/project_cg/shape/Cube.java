package com.example.project_cg.shape;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import com.example.project_cg.observe.Light;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shader.ShaderMap;
import com.example.project_cg.shader.ShaderType;
import com.example.project_cg.texture.TextureManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Cube extends Shape {
    private final FloatBuffer vertexBuffer;
    private final FloatBuffer textureBuffer;
    private final FloatBuffer normalBuffer;
    private FloatBuffer colorBuffer;
    private final ShortBuffer indexBuffer;
    private float[] color;
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
    private int mProgram;

    public Cube(float[] base, float[] shape, float[] dir, float[] rgba, float[] ambient, float[] diffuse, float[] specular, float shininess) {
        basePara = new float[4];
        shapePara = new float[4];
        dirPara = new float[4];

        setBasePara(base);
        setShapePara(shape);
        setDirPara(dir);

        translatePara = new float[4];
        scalePara = new float[4];
        rotatePara = new float[4];

        translatePara[3] = 1;

        rotatePara[1] = 1;

        scalePara[0] = 1;
        scalePara[1] = 1;
        scalePara[2] = 1;
        scalePara[3] = 1;

        textureUsed = new ArrayList<>();

        kAmbient = ambient.clone();
        kDiffuse = diffuse.clone();
        kSpecular = specular.clone();

        this.shininess = shininess;

        updateModelMatrix();

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
        float[] textureCoord = new float[48];
        for (i = 0; i < 24; i++) {
            int index = 4 * i;
            vertex[index] = vertexTemp.charAt(3 * i) == '-' ? -0.5f : 0.5f;
            vertex[index + 1] = vertexTemp.charAt(3 * i + 1) == '-' ? -0.5f : 0.5f;
            vertex[index + 2] = vertexTemp.charAt(3 * i + 2) == '-' ? -0.5f : 0.5f;
            vertex[index + 3] = 1f;

            int mod = i % 4;
            textureCoord[2 * i] = (mod == 1 || mod == 2) ? 1f : 0f;
            textureCoord[2 * i + 1] = (mod == 2 || mod == 3) ? 1f : 0f;
        }


        color = new float[96];
        for (i = 0; i < 24; i++) {
            color[4 * i] = rgba[0];
            color[4 * i + 1] = rgba[1];
            color[4 * i + 2] = rgba[2];
            color[4 * i + 3] = rgba[3];
        }

        vertexBuffer = ByteBuffer.allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertex)
                .position(0);

        updateColorBuffer();

        normalBuffer = ByteBuffer.allocateDirect(normal.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        normalBuffer.put(normal)
                .position(0);

        textureBuffer = ByteBuffer.allocateDirect(textureCoord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureBuffer.put(textureCoord)
                .position(0);

        indexBuffer = ByteBuffer.allocateDirect(triangleMap.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        indexBuffer.put(triangleMap)
                .position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                ShaderMap.get("shape", ShaderType.VERT));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                ShaderMap.get("shape", ShaderType.FRAG));

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
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
        updateTexture();
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
        int uUseTextureHandler = GLES20.glGetUniformLocation(mProgram, "uUseTexture");
        int uTextureHandler = GLES20.glGetUniformLocation(mProgram, "uTexture");
        int uAffineHandler = GLES20.glGetUniformLocation(mProgram, "uAffine");

        Light light = Observe.getLightList().get(0);
        updateModelMatrix();
        updateAffineMatrix();
        // set uniform data
        GLES20.glUniformMatrix4fv(uModelHandler, 1, false, model, 0);
        GLES20.glUniformMatrix4fv(uAffineHandler, 1, false, affine, 0);
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
        GLES20.glUniform1i(uUseTextureHandler, useTexture ? 1 : 0);
        if (useTexture) GLES20.glUniform1i(uTextureHandler, textureUsed.get(0));



        // get attribute handlers
        int iVertexPositionHandle = GLES20.glGetAttribLocation(mProgram, "iVertexPosition");
        int iColorHandle = GLES20.glGetAttribLocation(mProgram, "iColor");
        int iNormalHandle = GLES20.glGetAttribLocation(mProgram, "iNormal");
        int iTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "iTextureCoord");


        // set attribute handlers
        GLES20.glEnableVertexAttribArray(iVertexPositionHandle);
        GLES20.glVertexAttribPointer(iVertexPositionHandle, 4, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(iColorHandle);
        GLES20.glVertexAttribPointer(iColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        GLES20.glEnableVertexAttribArray(iNormalHandle);
        GLES20.glVertexAttribPointer(iNormalHandle, 4, GLES20.GL_FLOAT, false, 0, normalBuffer);

        GLES20.glEnableVertexAttribArray(iTextureCoordHandle);
        GLES20.glVertexAttribPointer(iTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, triangleMap.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        GLES20.glDisableVertexAttribArray(iVertexPositionHandle);
    }

    @Override
    public void setColor(float[] rgba) {
        int i;
        for (i = 0; i < 24; i++) {
            color[4 * i] = rgba[0];
            color[4 * i + 1] = rgba[1];
            color[4 * i + 2] = rgba[2];
            color[4 * i + 3] = rgba[3];
        }

        updateColorBuffer();
    }

    public void updateColorBuffer() {
        ByteBuffer cBB = ByteBuffer.allocateDirect(color.length * 4);
        cBB.order(ByteOrder.nativeOrder());
        colorBuffer = cBB.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);
    }

}
