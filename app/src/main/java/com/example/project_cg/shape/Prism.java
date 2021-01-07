package com.example.project_cg.shape;

import android.opengl.GLES20;

import com.example.project_cg.observe.Light;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shader.ShaderMap;
import com.example.project_cg.shader.ShaderType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Prism extends Shape {
    private float vertex[];
    private float normalX;
    private float normalY;
    private float normalZ;

    public Prism(float[] base, float[] shape, float[] dir, float[] rgba, MtlInfo mtl, float height, float radius1, float radius2, int edge) {
        color = rgba.clone();
        method = DrawMethod.STRIPE;
        this.mtl = mtl;


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
        ArrayList<Float> pos=new ArrayList<>();
        float angDegSpan=360f/edge;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            pos.add((float) (base[0]+radius1*Math.sin(i*Math.PI/180f)));
            pos.add((float)(base[1]+radius1*Math.cos(i*Math.PI/180f)));
            pos.add(base[2]+height);
            pos.add((float) (base[0]+radius2*Math.sin(i*Math.PI/180f)));
            pos.add((float)(base[1]+radius2*Math.cos(i*Math.PI/180f)));
            pos.add(base[2]);
        }
        vertex=new float[pos.size()];    //所有的顶点
        for (int i=0;i<vertex.length;i++)
        {
            vertex[i]=pos.get(i);
        }
        int vSize=vertex.length/3;


        ArrayList<Float> tex=new ArrayList<>();
        tex.add(0f);
        tex.add(1f);
        tex.add(0f);
        tex.add(0f);
        int flag=0;
        for(int i=0;i<edge;i+=1)
        {
            if(flag==0)
            {
                flag=1;
                tex.add(1f);
                tex.add(1f);
                tex.add(1f);
                tex.add(0f);
            }
            else
            {
                flag=0;
                tex.add(0f);
                tex.add(1f);
                tex.add(0f);
                tex.add(0f);
            }
        }
        float[] texture = new float[tex.size()];
        for (int i=0;i<texture.length;i++)
        {
            texture[i]=tex.get(i);
        }


        //法向量
        float normal[]=new float[pos.size()];
        for(int i=0;i<normal.length;i++){
            normal[i]=vertex[i];
        }

        vertexBuffer = ByteBuffer.allocateDirect(vertex.length /3*4*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        normalBuffer = ByteBuffer.allocateDirect(normal.length/3*4*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        textureBuffer = ByteBuffer.allocateDirect(vertex.length/3*8)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        int cntVertex = 0, cntNormal = 0, cntTexture = 0;
        for (int i = 0; i < vSize; i++) {
            vertexBuffer.put(cntVertex++, vertex[3*i]);
            vertexBuffer.put(cntVertex++, vertex[3*i+1]);
            vertexBuffer.put(cntVertex++, vertex[3*i+2]);
            vertexBuffer.put(cntVertex++, 1.0f);
        }
        for(int i=0;i<normal.length/3;i++)
        {
            normalBuffer.put(cntNormal++,normal[3*i]);
            normalBuffer.put(cntNormal++,normal[3*i+1]);
            normalBuffer.put(cntNormal++,normal[3*i+2]);
            normalBuffer.put(cntNormal++,1f);
        }
        for(int i=0;i<texture.length/2;i++)
        {
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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertex.length / 3);
        GLES20.glDisableVertexAttribArray(iVertexPositionHandle);
    }
    private void normalCalculate(int Index1,int Index2)
    {
        float vector1X=vertex[Index1*3]-vertex[0];
        float vector1Y=vertex[Index1*3+1]-vertex[1];
        float vector1Z=vertex[Index1*3+2]-vertex[2];
        float vector2X=vertex[Index2*3]-vertex[0];
        float vector2Y=vertex[Index2*3+1]-vertex[1];
        float vector2Z=vertex[Index2*3+2]-vertex[2];
        normalX=vector1Y*vector2Z-vector2Y*vector1Z;
        normalY=vector1Z*vector2X-vector1X*vector2Z;
        normalZ=vector1X*vector2Y-vector1Y*vector2X;
    }
}