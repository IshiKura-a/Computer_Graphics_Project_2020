package com.example.project_cg.observe;

import android.opengl.Matrix;

public class Camera {
    // The location of camera
    private float eyeX, eyeY, eyeZ, eyeW;
    // The location of objects
    private float centerX, centerY, centerZ, centerW;
    // The DIRECTION camera' top faces
    // Only care the direction!
    private float upX, upY, upZ, upW;
    private float[] mViewMatrix = new float[16];

    public Camera setEye(float x, float y, float z, float w) {
        eyeX = x;
        eyeY = y;
        eyeZ = z;
        eyeW = w;
        return this;
    }

    public Camera setCenter(float x, float y, float z, float w) {
        centerX = x;
        centerY = y;
        centerZ = z;
        centerW = w;
        return this;
    }

    public Camera setUp(float x, float y, float z, float w) {
        upX = x;
        upY = y;
        upZ = z;
        upW = w;
        return this;
    }

    public float[] getViewMatrix() {
        Matrix.setLookAtM(mViewMatrix, 0,
                eyeX / eyeW, eyeY / eyeW, eyeZ / eyeW,
                centerX / centerW, centerY / centerW, centerZ / centerW,
                upX / upW, upY / upW, upZ / upW);
        return mViewMatrix;
    }

    public float[] getEye() {
        return new float[]{eyeX, eyeY, eyeZ, eyeW};
    }
}
