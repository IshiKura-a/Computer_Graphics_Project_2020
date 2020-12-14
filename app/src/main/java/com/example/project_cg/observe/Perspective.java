package com.example.project_cg.observe;

import android.opengl.Matrix;

public class Perspective extends Projection {
    @Override
    public float[] getProjectionMatrix() {
        Matrix.frustumM(mProjectionMatrix, 0, left * ratio, right * ratio,
                bottom, top, near, far);
        return mProjectionMatrix;
    }
}
