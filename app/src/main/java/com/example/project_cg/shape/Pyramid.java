package com.example.project_cg.shape;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.example.project_cg.shape.Cube.loadShader;

public class Pyramid extends Shape{

    private FloatBuffer vertexBuffer, colorBuffer;
    private float radius=1f;
    private float height=2f;
    private int mMatrixHandler;
    private float[] mMVPMatrix;
    private int mProgram;
    private float vertex[];
    private float color[]={1.0f,1.0f,1.0f,1.0f};
    private int vSize;
    private int edge;

    public Pyramid(float baseX,float baseY,float baseZ,float radius,float height,int edge,int r,int g,int b,int a)
    {
        ArrayList<Float> pos=new ArrayList<>();
        this.radius=radius;
        this.height=height;
        this.edge=edge;
        pos.add(baseX);
        pos.add(baseY);
        pos.add(baseZ+height);
        float angDegSpan=360f/edge;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            pos.add((float) (baseX+radius*Math.sin(i*Math.PI/180f)));
            pos.add((float)(baseY+radius*Math.cos(i*Math.PI/180f)));
            pos.add(baseZ);
        }
        vertex=new float[pos.size()];    //所有的顶点

        for (int i=0;i<vertex.length;i++)
        {
            vertex[i]=pos.get(i);
        }
        vSize=vertex.length/3;

        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
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

        String vertexShaderCode = "attribute vec4 vPosition;" +
                "uniform mat4 vMatrix;" +
                "varying  vec4 vColor;" +
                "void main() {" +
                "  gl_Position = vMatrix*vPosition;" +
                "if(vPosition.z!=0.0){"+
                " vColor=vec4(0.0,0.0,0.0,1.0);"+
                " }else{ " +
                "vColor=vec4(0.9,0.9,0.9,1.0);}"+
                "}";
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);

        String fragmentShaderCode =
                "precision mediump float;" +
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




    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glUseProgram(mProgram);
        int mMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
        GLES20.glUniformMatrix4fv(mMatrix,1,false,mMVPMatrix,0);
        int mPositionHandle=GLES20.glGetAttribLocation(mProgram,"vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle,3,GLES20.GL_FLOAT,false,0,vertexBuffer);
        int mColorHandle=GLES20.glGetUniformLocation(mProgram,"vColor");
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glUniform4fv(mColorHandle,1,color,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,vSize);
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }

    @Override
    public void setMVPMatrix(float[] MVPMatrix) {
        mMVPMatrix = MVPMatrix;
    }
}

