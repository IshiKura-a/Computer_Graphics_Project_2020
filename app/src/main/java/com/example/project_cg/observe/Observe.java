package com.example.project_cg.observe;

import android.opengl.Matrix;

import java.util.ArrayList;

public class Observe {
    private static ArrayList<Light> lightList = new ArrayList<>();
    private static Camera camera = new Camera().setEye(5, 5, 10, 1)
            .setCenter(0, 0, 0, 1)
            .setUp(0, 1, 0, 1);
    private static Projection perspective = new Perspective().setLeft(-1)
            .setRight(1)
            .setTop(1)
            .setBottom(-1)
            .setNear(3)
            .setFar(20)
            .setRatio(1200, 2313);
    private static Projection ortho = new Ortho().setLeft(-3)
            .setRight(3)
            .setBottom(-3)
            .setTop(3)
            .setNear(-3)
            .setFar(20)
            .setRatio(1200, 2313);;
    private static boolean isOrtho = false;
    private static float[] mMVPMatrix = new float[16];


    public static float[] getMVPMatrix() {
        if (isOrtho) {
            Matrix.multiplyMM(mMVPMatrix, 0, ortho.getProjectionMatrix(), 0, camera.getViewMatrix(), 0);
        } else {
            Matrix.multiplyMM(mMVPMatrix, 0, perspective.getProjectionMatrix(), 0, camera.getViewMatrix(), 0);
        }
        return mMVPMatrix;
    }

    public static Camera getCamera() {
        return camera;
    }

    public static Projection getOrtho() {
        return ortho;
    }

    public static Projection getPerspective() {
        return perspective;
    }

    public static void setOrtho(boolean flag) {
        isOrtho = flag;
    }

    public static ArrayList<Light> getLightList() {
        return lightList;
    }
}
