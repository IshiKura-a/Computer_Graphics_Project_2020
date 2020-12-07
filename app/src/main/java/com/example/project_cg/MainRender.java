package com.example.project_cg;

import android.opengl.GLSurfaceView.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRender implements Renderer {

    static {
        System.loadLibrary("MainRender");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        onSurfaceCreatedCPP();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        onSurfaceChangedCPP(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        onDrawFrameCPP();
    }

    private static native void onSurfaceCreatedCPP();
    private static native void onSurfaceChangedCPP(int width, int height);
    private static native void onDrawFrameCPP();
}
