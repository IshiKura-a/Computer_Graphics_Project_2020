package com.example.project_cg.shape;

import android.opengl.GLES20;
import android.util.Log;

import com.example.project_cg.observe.Light;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shader.ShaderMap;
import com.example.project_cg.shader.ShaderType;
import com.example.project_cg.texture.TextureManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Ball extends Shape{

    private int Vsize;
    public Ball(float[] base, float[] shape, float[] dir, float[] rgba, MtlInfo mtl)
    {
        color = rgba.clone();
        method = DrawMethod.FAN;
        this.mtl = mtl;
        this.type=ShapeType.BALL;

        setRotateX(-90 + dir[0]);
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

        textureUsed = new LinkedList<>();

        updateModelMatrix();


        float step=1f;
        ArrayList<Float> data=new ArrayList<>();
        float r1,r2;
        float h1,h2;
        float sin,cos;
        for(float i=-90;i<90+step;i+=step){
            r1 = (float)Math.cos(i * Math.PI / 180.0);
            r2 = (float)Math.cos((i + step) * Math.PI / 180.0);
            h1 = (float)Math.sin(i * Math.PI / 180.0);
            h2 = (float)Math.sin((i + step) * Math.PI / 180.0);
            // 固定纬度, 360 度旋转遍历一条纬线
            float step2=step*2;
            for (float j = 0.0f; j <360.0f+step; j +=step2 ) {
                cos = (float) Math.cos(j * Math.PI / 180.0);
                sin = -(float) Math.sin(j * Math.PI / 180.0);
                data.add(r1 * cos);
                data.add(r1 * sin);
                data.add(h1);

                data.add(r2 * cos);
                data.add(r2 * sin);
                data.add(h2);

            }
        }
        float vertex[]=new float[data.size()];
        for(int i=0;i<vertex.length;i++){
            vertex[i]=data.get(i);
        }
        Vsize=vertex.length/3;

        float normal[]=new float[data.size()];
        for(int i=0;i<normal.length;i++){
            normal[i]=vertex[i];
        }


        vertexBuffer = ByteBuffer.allocateDirect(vertex.length/3 *4*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        normalBuffer = ByteBuffer.allocateDirect(normal.length/3 *4*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureBuffer = ByteBuffer.allocateDirect(vertex.length/3 *4*2)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();


        int cntVertex = 0, cntNormal = 0, cntTexture = 0;
        for (int i = 0; i < Vsize; i++) {
            vertexBuffer.put(cntVertex++, vertex[3*i]);
            vertexBuffer.put(cntVertex++, vertex[3*i+1]);
            vertexBuffer.put(cntVertex++, vertex[3*i+2]);
            vertexBuffer.put(cntVertex++, 1.0f);

            normalBuffer.put(cntNormal++, normal[3*i]);
            normalBuffer.put(cntNormal++, normal[3*i+1]);
            normalBuffer.put(cntNormal++, normal[3*i+2]);
            normalBuffer.put(cntNormal++, 1.0f);
        }

        ArrayList<Float> tex=new ArrayList<>();
        for(float i=-90;i<90+step;i+=step){
            // 固定纬度, 360 度旋转遍历一条纬线
            float step2=step*2;
            for (float j = 0.0f; j <360.0f+step; j +=step2 ) {
                float texU1 = j/360.0f;
                float texV1 = i/180.0f+0.5f;
                float texU2 = j/360.0f;
                float texV2 = (i+step)/180.0f+0.5f;
                tex.add(texU1);
                tex.add(texV1);
                tex.add(texU2);;
                tex.add(texV2);
            }
        }
        float texture[]=new float[tex.size()];
        for(int i=0;i<tex.size();i++)
        {
            texture[i]=tex.get(i);
        }
        for (int i = 0; i < Vsize; i++) {
            textureBuffer.put(cntTexture++,texture[2*i]);
            textureBuffer.put(cntTexture++,texture[2*i+1]);
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

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // get uniform handlers
        GLES20.glUseProgram(mProgram);
        int flag=GLES20.glGetUniformLocation(mProgram, "ischosen");
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
        Light blackLight =new Light()
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
                lightList.add(blackLight);
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
        GLES20.glUniform4fv(uMaterialSpecularHandler, 1, mtl.kSpecular, 0);
        GLES20.glUniform4fv(uMaterialDiffuseHandler, 1, mtl.kDiffuse, 0);
        GLES20.glUniform4fv(uMaterialAmbientHandler, 1, mtl.kAmbient, 0);
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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, Vsize);
        GLES20.glDisableVertexAttribArray(iVertexPositionHandle);
    }
}

