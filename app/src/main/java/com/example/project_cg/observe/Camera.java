package com.example.project_cg.observe;

import android.opengl.Matrix;

public class Camera {
    // The location of camera
    private float eyeX, eyeY, eyeZ;
    // The location of objects
    private float centerX, centerY, centerZ;
    // The DIRECTION camera' top faces
    // Only care the direction!
    private float upX, upY, upZ;
    private float[] mViewMatrix = new float[16];

    public Camera setEye(float x, float y, float z) {
        eyeX = x;
        eyeY = y;
        eyeZ = z;
        return this;
    }
    public Camera setCenter(float x, float y, float z) {
        centerX = x;
        centerY = y;
        centerZ = z;
        return this;
    }
    public Camera setUp(float x, float y, float z) {
        upX = x;
        upY = y;
        upZ = z;
        return this;
    }
    public float[] getViewMatrix() {
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        return mViewMatrix;
    }
}
