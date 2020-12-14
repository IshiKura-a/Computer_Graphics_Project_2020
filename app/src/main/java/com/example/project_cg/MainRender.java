package com.example.project_cg;

import android.opengl.GLSurfaceView.*;
import android.util.Log;

import com.example.project_cg.shape.Cube;
import com.example.project_cg.shape.Shape;

import java.util.concurrent.LinkedBlockingDeque;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRender implements Renderer {
    LinkedBlockingDeque<Shape> shapes = new LinkedBlockingDeque<>();
    static {
        System.loadLibrary("MainRender");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        shapes.add(new Cube(0,0,0,1,2,3,1,0,0,1));
        for(Shape s: shapes) {
            s.onSurfaceCreated(gl, config);
        }
        onSurfaceCreatedCPP();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        for(Shape s:shapes) {
            s.onSurfaceChanged(gl, width, height);
        }
        onSurfaceChangedCPP(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        onDrawFrameCPP();
        for(Shape s:shapes) {
            s.onDrawFrame(gl);
        }
    }

    private static native void onSurfaceCreatedCPP();
    private static native void onSurfaceChangedCPP(int width, int height);
    private static native void onDrawFrameCPP();
}
