package com.example.project_cg.observe;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.LinkedList;

public class Observe {
    private static LinkedList<Light> lightList = new LinkedList<>();
    private static Camera camera = new Camera().setEye(0, 5, 15, 1)
            .setCenter(0, 0, 0, 1)
            .setUp(0, 1, 0, 1);
    private static Projection perspective = new Perspective().setLeft(-1)
            .setRight(1)
            .setTop(1)
            .setBottom(-1)
            .setNear(2f)
            .setFar(1000f)
            .setRatio(1200, 2313);
    private static Projection ortho = new Ortho().setLeft(-3)
            .setRight(3)
            .setBottom(-3)
            .setTop(3)
            .setNear(-3)
            .setFar(20)
            .setRatio(1200, 2313);;
    private static boolean isOrtho = false;

    public static float[] getViewMatrix() {
        return camera.getViewMatrix();
    }

    public static float[] getProjectionMatrix() {
        if(isOrtho) return ortho.getProjectionMatrix();
        else return perspective.getProjectionMatrix();
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

    public static LinkedList<Light> getLightList() {
        return lightList;
    }
}
