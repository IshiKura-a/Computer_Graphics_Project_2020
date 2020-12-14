package com.example.project_cg.observe;

import android.opengl.Matrix;

public class Ortho extends Projection{
    @Override
    public float[] getProjectionMatrix() {
        Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        return mProjectionMatrix;
    }
}
