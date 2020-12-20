package com.example.project_cg.observe;

import android.opengl.Matrix;
import android.util.Log;

public class Ortho extends Projection{
    @Override
    public float[] getProjectionMatrix() {
        if(ratio > 1) {
            Matrix.orthoM(mProjectionMatrix, 0, left * ratio, right * ratio, bottom, top, near, far);
            // Log.i("TAG", (left*ratio)+":"+(right*ratio)+":"+(bottom)+":"+(top)+":"+near+":"+far);
        }
        else {
            Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom / ratio, top / ratio, near, far);
            // Log.i("TAG", (left)+":"+(right)+":"+(bottom/ratio)+":"+(top/ratio)+":"+near+":"+far);
        }
        return mProjectionMatrix;
    }
}
