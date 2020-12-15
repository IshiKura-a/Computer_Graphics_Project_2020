package com.example.project_cg;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.*;
import android.opengl.Matrix;
import android.util.Log;

import com.example.project_cg.observe.Observe;
import com.example.project_cg.shape.Cube;
import com.example.project_cg.shape.Shape;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRender implements Renderer {
    Observe observe = new Observe().setOrtho(false);
    ArrayList<Shape> shapes = new ArrayList<>();

    static {
        System.loadLibrary("MainRender");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        shapes.add(new Cube(0, 0, 0, 1, 1, 1, 1, 0, 0, 1));
        // GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        onSurfaceCreatedCPP();
        for (Shape s : shapes) {
            s.onSurfaceCreated(gl, config);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // GLES20.glViewport(0,0,width,height);
        onSurfaceChangedCPP(width, height);
        Log.i("TAG", "Width:" + width + ",height:" + height);
        observe.getPerspective().setRatio(width, height);
        observe.getOrtho().setRatio(width, height);
        float[] mMVPMatrix = observe.getMVPMatrix();
        for (Shape s : shapes) {
            s.setMVPMatrix(mMVPMatrix);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        onDrawFrameCPP();
        // Shapes should be drawn after the canvus
        for(Shape s:shapes) {
            s.onDrawFrame(gl);
        }
    }

    private static native void onSurfaceCreatedCPP();
    private static native void onSurfaceChangedCPP(int width, int height);
    private static native void onDrawFrameCPP();
}
