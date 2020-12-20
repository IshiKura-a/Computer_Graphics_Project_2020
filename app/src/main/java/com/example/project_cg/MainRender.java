package com.example.project_cg;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.*;
import android.opengl.Matrix;
import android.os.Build;
import android.util.Log;

import com.example.project_cg.observe.Light;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shape.Cube;
import com.example.project_cg.shape.Shape;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MainRender implements Renderer {
    ArrayList<Shape> shapes = new ArrayList<>();

    static {
        System.loadLibrary("MainRender");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Observe.setOrtho(false);
        Observe.getLightList().add(new Light()
                .setAmbient(new float[]{1, 1, 1, 1})
                .setDiffuse(new float[]{1, 1, 1, 1})
                .setSpecular(new float[]{1, 1, 1, 1})
                .setLocation(new float[]{5, 5, -5, 1}));

        shapes.add(new Cube(new float[]{0, 0, 0, 1}, 1, 1, 2,
                new float[]{1, 0, 0, 1}, new float[]{0.2f, 0.2f, 0.2f, 1},
                new float[]{0.8f, 0.8f, 0.8f, 1}, new float[]{0, 0, 0, 1}, 20));
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
        Observe.getPerspective().setRatio(width, height);
        Observe.getOrtho().setRatio(width, height);
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
