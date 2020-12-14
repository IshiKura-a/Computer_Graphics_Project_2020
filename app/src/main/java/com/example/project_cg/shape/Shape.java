package com.example.project_cg.shape;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class Shape {
    abstract public void onSurfaceCreated(GL10 gl, EGLConfig config);
    abstract public void onDrawFrame(GL10 gl);
    abstract public void setMVPMatrix(float[] MVPMatrix);
}
