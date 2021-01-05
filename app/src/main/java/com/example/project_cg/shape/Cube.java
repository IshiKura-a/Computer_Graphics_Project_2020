package com.example.project_cg.shape;

import android.opengl.GLES20;
import android.util.Log;

import com.example.project_cg.observe.Light;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shader.ShaderMap;
import com.example.project_cg.shader.ShaderType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Cube extends Shape {
    private final int[] faces = {
            1, 5, 1, 2, 6, 1, 3, 2, 1,
            1, 5, 1, 3, 2, 1, 4, 1, 1,
            5, 8, 2, 8, 4, 2, 7, 3, 2,
            5, 8, 2, 7, 3, 2, 6, 7, 2,
            1, 9, 3, 5, 10, 3, 6, 7, 3,
            1, 9, 3, 6, 7, 3, 2, 6, 3,
            2, 6, 4, 6, 7, 4, 7, 3, 4,
            2, 6, 4, 7, 3, 4, 3, 2, 4,
            3, 13, 5, 7, 14, 5, 8, 12, 5,
            3, 13, 5, 8, 12, 5, 4, 11, 5,
            5, 10, 6, 1, 9, 6, 4, 11, 6,
            5, 10, 6, 4, 11, 6, 8, 12, 6
    };

    public Cube(float[] base, float[] shape, float[] dir, float[] rgba, MtlInfo mtl) {
        type = ShapeType.CUBE;
        color = rgba.clone();
        method = DrawMethod.SIMPLE;
        this.mtl = mtl;

        setRotateX(90 + dir[0]);
        setRotateY(dir[1]);
        setRotateZ(dir[2]);

        basePara = new float[4];
        shapePara = new float[4];

        setBasePara(base);
        setShapePara(shape);

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

        updateModelMatrix();

        vertexBuffer = ByteBuffer.allocateDirect(faces.length / 3 * 16)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        normalBuffer = ByteBuffer.allocateDirect(faces.length / 3 * 16)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        textureBuffer = ByteBuffer.allocateDirect(faces.length / 3 * 8)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        float[] vertex = {
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f
        };

        float[] texture = {
                0, 0,
                0.333334f, 0,
                0.666667f, 0,
                1, 0,
                0, 0.25f,
                0.333334f, 0.25f,
                0.666667f, 0.25f,
                1, 0.25f,
                0.333334f, 0.5f,
                0.666667f, 0.5f,
                0.333334f, 0.75f,
                0.666667f, 0.75f,
                0.333334f, 1,
                0.666667f, 1
        };

        float[] normal = {
                0, -1, 0,
                0, 1, 0,
                1, 0, 0,
                0, 0, 1,
                -1, 0, 0,
                0, 0, -1
        };

        int cntVertex = 0, cntNormal = 0, cntTexture = 0;
        for (int i = 0; i < faces.length; ) {

            vertexBuffer.put(cntVertex++, vertex[3 * faces[i] - 3]);
            vertexBuffer.put(cntVertex++, vertex[3 * faces[i] - 2]);
            vertexBuffer.put(cntVertex++, vertex[3 * faces[i++] - 1]);
            vertexBuffer.put(cntVertex++, 1.0f);

            textureBuffer.put(cntTexture++, texture[2 * faces[i] - 2]);
            textureBuffer.put(cntTexture++, texture[2 * faces[i++] - 1]);

            normalBuffer.put(cntNormal++, normal[3 * faces[i] - 3]);
            normalBuffer.put(cntNormal++, normal[3 * faces[i] - 2]);
            normalBuffer.put(cntNormal++, normal[3 * faces[i++] - 1]);
            normalBuffer.put(cntNormal++, 1.0f);
        }

        vertexBuffer.position(0);
        normalBuffer.position(0);
        textureBuffer.position(0);


        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                ShaderMap.get("object", ShaderType.VERT));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                ShaderMap.get("object", ShaderType.FRAG));

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
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
        int uColorHandler = GLES20.glGetUniformLocation(mProgram, "uColor");

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
        GLES20.glUniform1f(uShininessHandler, mtl.shininess);
        GLES20.glUniform4fv(uLightSpecularHandler, 1, light.getSpecular(), 0);
        GLES20.glUniform4fv(uLightDiffuseHandler, 1, light.getDiffuse(), 0);
        GLES20.glUniform4fv(uLightAmbientHandler, 1, light.getAmbient(), 0);
        GLES20.glUniform4fv(uMaterialSpecularHandler, 1, mtl.kSpecular, 0);
        GLES20.glUniform4fv(uMaterialDiffuseHandler, 1, mtl.kDiffuse, 0);
        GLES20.glUniform4fv(uMaterialAmbientHandler, 1, mtl.kAmbient, 0);
        GLES20.glUniform1i(uUseTextureHandler, useTexture ? 1 : 0);
        if (useTexture) GLES20.glUniform1i(uTextureHandler, textureUsed.get(0));
        GLES20.glUniform4fv(uColorHandler, 1, color, 0);



        // get attribute handlers
        int iVertexPositionHandle = GLES20.glGetAttribLocation(mProgram, "iVertexPosition");
        int iColorHandle = GLES20.glGetAttribLocation(mProgram, "iColor");
        int iNormalHandle = GLES20.glGetAttribLocation(mProgram, "iNormal");
        int iTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "iTextureCoord");


        // set attribute handlers
        GLES20.glEnableVertexAttribArray(iVertexPositionHandle);
        GLES20.glVertexAttribPointer(iVertexPositionHandle, 4, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(iNormalHandle);
        GLES20.glVertexAttribPointer(iNormalHandle, 4, GLES20.GL_FLOAT, false, 0, normalBuffer);

        GLES20.glEnableVertexAttribArray(iTextureCoordHandle);
        GLES20.glVertexAttribPointer(iTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, faces.length / 3);
        GLES20.glDisableVertexAttribArray(iVertexPositionHandle);
    }

}
