package com.example.project_cg.shape;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import com.example.project_cg.observe.Light;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shader.ShaderMap;
import com.example.project_cg.shader.ShaderType;
import com.example.project_cg.texture.TextureManager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Model extends Shape {
    ArrayList<Float> vertex = new ArrayList<>();
    ArrayList<Float> normal = new ArrayList<>();
    ArrayList<Float> texture = new ArrayList<>();

    public Model() {
        type = ShapeType.MODEL;
        method = DrawMethod.SIMPLE;
        translatePara = new float[4];
        scalePara = new float[4];
        rotatePara = new float[4];

        translatePara[3] = 1;

        rotatePara[1] = 1;

        scalePara[0] = 1;
        scalePara[1] = 1;
        scalePara[2] = 1;
        scalePara[3] = 1;

        textureUsed = new LinkedList<>();

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                ShaderMap.get("object", ShaderType.VERT));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                ShaderMap.get("object", ShaderType.FRAG));

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }


    public static Model readObject(Model model, BufferedReader br) {
        ArrayList<Float> tmpVertex = new ArrayList<>();
        ArrayList<Float> tmpNormal = new ArrayList<>();
        ArrayList<Float> tmpTexture = new ArrayList<>();

        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] list = line.split("[ ]+");
                switch (list[0]) {
                    case "v": {
                        for (int i = 1; i < 4; i++) {
                            tmpVertex.add(Float.parseFloat(list[i]));
                        }
                        tmpVertex.add(1.0f);
                        break;
                    }
                    case "vn": {
                        for (int i = 1; i < 4; i++) {
                            tmpNormal.add(Float.parseFloat(list[i]));
                        }
                        tmpNormal.add(1.0f);
                        break;
                    }
                    case "vt": {
                        for (int i = 1; i < 3; i++) {
                            tmpTexture.add(Float.parseFloat(list[i]));
                        }
                        break;
                    }
                    case "f": {
                        for(int i=1;i<list.length;i++) {
                            String[] parts = list[i].split("/");
                            int index;
                            if(parts.length > 0) {
                                index = 4*(Integer.parseInt(parts[0])-1);
                                model.vertex.add(tmpVertex.get(index++));
                                model.vertex.add(tmpVertex.get(index++));
                                model.vertex.add(tmpVertex.get(index++));
                                model.vertex.add(tmpVertex.get(index));
                            }
                            if(parts.length > 1) {
                                index = 2*(Integer.parseInt(parts[1])-1);
                                model.texture.add(tmpTexture.get(index++));
                                model.texture.add(tmpTexture.get(index));
                            }
                            if(parts.length > 2) {
                                index = 4*(Integer.parseInt(parts[2])-1);
                                model.normal.add(tmpNormal.get(index++));
                                model.normal.add(tmpNormal.get(index++));
                                model.normal.add(tmpNormal.get(index++));
                                model.normal.add(tmpNormal.get(index));
                            }
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


        model.vertexBuffer = ByteBuffer.allocateDirect(model.vertex.size() * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        for(float f: model.vertex) {
            model.vertexBuffer.put(f);
        }
        model.vertexBuffer.position(0);

        model.normalBuffer = ByteBuffer.allocateDirect(model.normal.size() * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        for(float f: model.normal) {
            model.normalBuffer.put(f);
        }
        model.normalBuffer.position(0);

        model.textureBuffer = ByteBuffer.allocateDirect(model.texture.size() * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        for(float f: model.texture) {
            model.textureBuffer.put(f);
        }
        model.textureBuffer.position(0);
        return model;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
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
        int flag=GLES20.glGetUniformLocation(mProgram, "ischosen");

        int uLightPositionHandler2 = GLES20.glGetUniformLocation(mProgram, "uLightPosition2");
        int uLightSpecularHandler2 = GLES20.glGetUniformLocation(mProgram, "uLightSpecular2");
        int uLightDiffuseHandler2 = GLES20.glGetUniformLocation(mProgram, "uLightDiffuse2");
        int uLightAmbientHandler2 = GLES20.glGetUniformLocation(mProgram, "uLightAmbient2");

        int uLightPositionHandler3 = GLES20.glGetUniformLocation(mProgram, "uLightPosition3");
        int uLightSpecularHandler3 = GLES20.glGetUniformLocation(mProgram, "uLightSpecular3");
        int uLightDiffuseHandler3 = GLES20.glGetUniformLocation(mProgram, "uLightDiffuse3");
        int uLightAmbientHandler3 = GLES20.glGetUniformLocation(mProgram, "uLightAmbient3");

        int uLightPositionHandler4 = GLES20.glGetUniformLocation(mProgram, "uLightPosition4");
        int uLightSpecularHandler4 = GLES20.glGetUniformLocation(mProgram, "uLightSpecular4");
        int uLightDiffuseHandler4 = GLES20.glGetUniformLocation(mProgram, "uLightDiffuse4");
        int uLightAmbientHandler4 = GLES20.glGetUniformLocation(mProgram, "uLightAmbient4");

        int uLightPositionHandler5 = GLES20.glGetUniformLocation(mProgram, "uLightPosition5");
        int uLightSpecularHandler5 = GLES20.glGetUniformLocation(mProgram, "uLightSpecular5");
        int uLightDiffuseHandler5 = GLES20.glGetUniformLocation(mProgram, "uLightDiffuse5");
        int uLightAmbientHandler5 = GLES20.glGetUniformLocation(mProgram, "uLightAmbient5");

        int uLightPositionHandler6 = GLES20.glGetUniformLocation(mProgram, "uLightPosition6");
        int uLightSpecularHandler6 = GLES20.glGetUniformLocation(mProgram, "uLightSpecular6");
        int uLightDiffuseHandler6 = GLES20.glGetUniformLocation(mProgram, "uLightDiffuse6");
        int uLightAmbientHandler6 = GLES20.glGetUniformLocation(mProgram, "uLightAmbient6");

        int uLightPositionHandler7 = GLES20.glGetUniformLocation(mProgram, "uLightPosition7");
        int uLightSpecularHandler7 = GLES20.glGetUniformLocation(mProgram, "uLightSpecular7");
        int uLightDiffuseHandler7 = GLES20.glGetUniformLocation(mProgram, "uLightDiffuse7");
        int uLightAmbientHandler7 = GLES20.glGetUniformLocation(mProgram, "uLightAmbient7");

        int uLightPositionHandler8 = GLES20.glGetUniformLocation(mProgram, "uLightPosition8");
        int uLightSpecularHandler8 = GLES20.glGetUniformLocation(mProgram, "uLightSpecular8");
        int uLightDiffuseHandler8 = GLES20.glGetUniformLocation(mProgram, "uLightDiffuse8");
        int uLightAmbientHandler8 = GLES20.glGetUniformLocation(mProgram, "uLightAmbient8");

        int uLightPositionHandler9 = GLES20.glGetUniformLocation(mProgram, "uLightPosition9");
        int uLightSpecularHandler9 = GLES20.glGetUniformLocation(mProgram, "uLightSpecular9");
        int uLightDiffuseHandler9 = GLES20.glGetUniformLocation(mProgram, "uLightDiffuse9");
        int uLightAmbientHandler9 = GLES20.glGetUniformLocation(mProgram, "uLightAmbient9");

        int uLightPositionHandler10 = GLES20.glGetUniformLocation(mProgram, "uLightPosition10");
        int uLightSpecularHandler10 = GLES20.glGetUniformLocation(mProgram, "uLightSpecular10");
        int uLightDiffuseHandler10 = GLES20.glGetUniformLocation(mProgram, "uLightDiffuse10");
        int uLightAmbientHandler10 = GLES20.glGetUniformLocation(mProgram, "uLightAmbient10");

        LinkedList<Light> lightList;
        synchronized (Observe.getLightList()) {
            lightList = new LinkedList<>(Observe.getLightList());
        }
        Light blacklight =new Light()
                .setAmbient(new float[]{0f, 0f, 0f, 0f})
                .setDiffuse(new float[]{0f, 0f, 0f, 0f})
                .setSpecular(new float[]{0f, 0f, 0f, 0f})
                .setLocation(new float[]{3, 2, 10, 1});
        int num=0;
        if(lightList.size()<10)
        {
            num=10-lightList.size();
            for(int i=0;i<num;i++)
            {
                lightList.add(blacklight);
            }
        }
        Light light = lightList.get(0);
        Light light2 = lightList.get(1);
        Light light3 = lightList.get(2);
        Light light4 = lightList.get(3);
        Light light5 = lightList.get(4);
        Light light6 = lightList.get(5);
        Light light7 = lightList.get(6);
        Light light8 = lightList.get(7);
        Light light9 = lightList.get(8);
        Light light10 = lightList.get(9);

        updateModelMatrix();
        updateAffineMatrix();
        // set uniform data
        float chosenflag=0;
        if(isChosen)
        {
            chosenflag=1.0f;
        }
        else
        {
            chosenflag=0f;
        }
        GLES20.glUniform1f(flag,chosenflag);
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
        GLES20.glUniform4fv(uLightPositionHandler2, 1, light2.getLocation(), 0);
        GLES20.glUniform4fv(uLightSpecularHandler2, 1, light2.getSpecular(), 0);
        GLES20.glUniform4fv(uLightDiffuseHandler2, 1, light2.getDiffuse(), 0);
        GLES20.glUniform4fv(uLightAmbientHandler2, 1, light2.getAmbient(), 0);

        GLES20.glUniform4fv(uLightPositionHandler3, 1, light3.getLocation(), 0);
        GLES20.glUniform4fv(uLightSpecularHandler3, 1, light3.getSpecular(), 0);
        GLES20.glUniform4fv(uLightDiffuseHandler3, 1, light3.getDiffuse(), 0);
        GLES20.glUniform4fv(uLightAmbientHandler3, 1, light3.getAmbient(), 0);

        GLES20.glUniform4fv(uLightPositionHandler4, 1, light4.getLocation(), 0);
        GLES20.glUniform4fv(uLightSpecularHandler4, 1, light4.getSpecular(), 0);
        GLES20.glUniform4fv(uLightDiffuseHandler4, 1, light4.getDiffuse(), 0);
        GLES20.glUniform4fv(uLightAmbientHandler4, 1, light4.getAmbient(), 0);

        GLES20.glUniform4fv(uLightPositionHandler5, 1, light5.getLocation(), 0);
        GLES20.glUniform4fv(uLightSpecularHandler5, 1, light5.getSpecular(), 0);
        GLES20.glUniform4fv(uLightDiffuseHandler5, 1, light5.getDiffuse(), 0);
        GLES20.glUniform4fv(uLightAmbientHandler5, 1, light5.getAmbient(), 0);

        GLES20.glUniform4fv(uLightPositionHandler6, 1, light6.getLocation(), 0);
        GLES20.glUniform4fv(uLightSpecularHandler6, 1, light6.getSpecular(), 0);
        GLES20.glUniform4fv(uLightDiffuseHandler6, 1, light6.getDiffuse(), 0);
        GLES20.glUniform4fv(uLightAmbientHandler6, 1, light6.getAmbient(), 0);

        GLES20.glUniform4fv(uLightPositionHandler7, 1, light7.getLocation(), 0);
        GLES20.glUniform4fv(uLightSpecularHandler7, 1, light7.getSpecular(), 0);
        GLES20.glUniform4fv(uLightDiffuseHandler7, 1, light7.getDiffuse(), 0);
        GLES20.glUniform4fv(uLightAmbientHandler7, 1, light7.getAmbient(), 0);

        GLES20.glUniform4fv(uLightPositionHandler8, 1, light8.getLocation(), 0);
        GLES20.glUniform4fv(uLightSpecularHandler8, 1, light8.getSpecular(), 0);
        GLES20.glUniform4fv(uLightDiffuseHandler8, 1, light8.getDiffuse(), 0);
        GLES20.glUniform4fv(uLightAmbientHandler8, 1, light8.getAmbient(), 0);

        GLES20.glUniform4fv(uLightPositionHandler9, 1, light9.getLocation(), 0);
        GLES20.glUniform4fv(uLightSpecularHandler9, 1, light9.getSpecular(), 0);
        GLES20.glUniform4fv(uLightDiffuseHandler9, 1, light9.getDiffuse(), 0);
        GLES20.glUniform4fv(uLightAmbientHandler9, 1, light9.getAmbient(), 0);

        GLES20.glUniform4fv(uLightPositionHandler10, 1, light10.getLocation(), 0);
        GLES20.glUniform4fv(uLightSpecularHandler10, 1, light10.getSpecular(), 0);
        GLES20.glUniform4fv(uLightDiffuseHandler10, 1, light10.getDiffuse(), 0);
        GLES20.glUniform4fv(uLightAmbientHandler10, 1, light10.getAmbient(), 0);
        GLES20.glUniform1i(uUseTextureHandler, useTexture ? 1 : 0);
        if (useTexture) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0+textureUsed.get(0));
            GLES20.glEnable(GLES20.GL_TEXTURE_2D);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureManager.getTextureIdByIndex(textureUsed.get(0)));
            GLES20.glUniform1i(uTextureHandler, textureUsed.get(0));
        }
        GLES20.glUniform4fv(uColorHandler, 1, color, 0);


        // get attribute handlers
        int iVertexPositionHandle = GLES20.glGetAttribLocation(mProgram, "iVertexPosition");
        int iNormalHandle = GLES20.glGetAttribLocation(mProgram, "iNormal");
        int iTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "iTextureCoord");


        // set attribute handlers
        GLES20.glEnableVertexAttribArray(iVertexPositionHandle);
        GLES20.glVertexAttribPointer(iVertexPositionHandle, 4, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(iNormalHandle);
        GLES20.glVertexAttribPointer(iNormalHandle, 4, GLES20.GL_FLOAT, false, 0, normalBuffer);

        GLES20.glEnableVertexAttribArray(iTextureCoordHandle);
        GLES20.glVertexAttribPointer(iTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertex.size() / 4);
        GLES20.glDisableVertexAttribArray(iVertexPositionHandle);
    }

    @Override
    public void setColor(float[] rgba) {
        color = rgba.clone();
    }

    public static void writeObject(LinkedList<Shape> shapes, FileOutputStream fos) throws IOException {
        int base = 1;
        for (Shape s : shapes) {
            s.updateModelMatrix();
            float[] model = s.model.clone();
            FloatBuffer vertexBuffer = s.vertexBuffer.asReadOnlyBuffer();
            FloatBuffer normalBuffer = s.normalBuffer.asReadOnlyBuffer();
            FloatBuffer textureBuffer = s.textureBuffer.asReadOnlyBuffer();

            vertexBuffer.position(0);
            normalBuffer.position(0);
            textureBuffer.position(0);

            for (int i = 0; i < vertexBuffer.limit(); i += 4) {
                float[] tmpVertex = new float[4];
                vertexBuffer.get(tmpVertex);
                Matrix.multiplyMV(tmpVertex, 0, model, 0, tmpVertex, 0);
                fos.write(String.format(Locale.getDefault(), "v %.6f %.6f %.6f\n",
                        tmpVertex[0] / tmpVertex[3], tmpVertex[1] / tmpVertex[3], tmpVertex[2] / tmpVertex[3]).getBytes());
            }
            vertexBuffer.position(0);

            for (int i = 0; i < textureBuffer.limit(); i += 2) {
                float[] tmpTexture = new float[2];
                textureBuffer.get(tmpTexture);
                fos.write(String.format(Locale.getDefault(), "vt %.6f %.6f\n", tmpTexture[0], tmpTexture[1]).getBytes());
            }
            textureBuffer.position(0);

            for (int i = 0; i < normalBuffer.limit(); i += 4) {
                float[] tmpNormal = new float[4];
                normalBuffer.get(tmpNormal);
                Matrix.multiplyMV(tmpNormal, 0, model, 0, tmpNormal, 0);
                fos.write(String.format(Locale.getDefault(), "vn %.6f %.6f %.6f\n",
                        tmpNormal[0] / tmpNormal[3], tmpNormal[1] / tmpNormal[3], tmpNormal[2] / tmpNormal[3]).getBytes());
            }
            normalBuffer.position(0);

            if (s.method == DrawMethod.SIMPLE) {
                for (int index = 0; index < vertexBuffer.limit() / 4; index += 3) {
                    int i = base + index;
                    int j = i + 1;
                    int k = i + 2;
                    fos.write(String.format(Locale.getDefault(), "f %d/%d/%d %d/%d/%d %d/%d/%d\n", i, i, i, j, j, j, k, k, k).getBytes());
                }
            } else if (s.method == DrawMethod.STRIPE) {
                for (int index = 0; index < vertexBuffer.limit() / 4 - 2; index++) {
                    int i = base + index;
                    int j = i + 1;
                    int k = i + 2;
                    fos.write(String.format(Locale.getDefault(), "f %d/%d/%d %d/%d/%d %d/%d/%d\n", i, i, i, j, j, j, k, k, k).getBytes());
                }
            } else if (s.method == DrawMethod.FAN) {
                int i = base;
                for(int index = 1; index < vertexBuffer.limit() / 4; index += 2) {
                    int j = i + index;
                    int k = j + 1;
                    fos.write(String.format(Locale.getDefault(), "f %d/%d/%d %d/%d/%d %d/%d/%d\n", i, i, i, j, j, j, k, k, k).getBytes());
                }
            }
            else {
                Log.e("Error", "DrawMethod not supported");
            }
            base += vertexBuffer.limit() / 4;
        }
    }
}