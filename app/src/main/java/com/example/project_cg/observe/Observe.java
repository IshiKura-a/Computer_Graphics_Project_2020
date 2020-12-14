package com.example.project_cg.observe;

import android.opengl.Matrix;

public class Observe {
    private Camera camera;
    private Projection perspective, ortho;
    private boolean isOrtho = false;
    private float[] mMVPMatrix = new float[16];

    public Observe() {
        this(1200, 2313);
    }

    public Observe(float width, float height) {
        camera = new Camera().setEye(5, 5, 10)
                .setCenter(0, 0, 0)
                .setUp(0, 1, 0);
        perspective = new Perspective().setLeft(-1)
                .setRight(1)
                .setTop(1)
                .setBottom(-1)
                .setNear(3)
                .setFar(20)
                .setRatio(width, height);
        ortho = new Ortho().setLeft(-3)
                .setRight(3)
                .setBottom(-3)
                .setTop(3)
                .setNear(-100)
                .setFar(100);
    }

    public float[] getMVPMatrix() {
        if (isOrtho) {
            Matrix.multiplyMM(mMVPMatrix, 0, ortho.getProjectionMatrix(), 0, camera.getViewMatrix(), 0);
        } else {
            Matrix.multiplyMM(mMVPMatrix, 0, perspective.getProjectionMatrix(), 0, camera.getViewMatrix(), 0);
        }
        return mMVPMatrix;
    }

    public Camera getCamera() {
        return camera;
    }

    public Projection getOrtho() {
        return ortho;
    }

    public Projection getPerspective() {
        return perspective;
    }

    public Observe setOrtho(boolean flag) {
        isOrtho = flag;
        return this;
    }
}
