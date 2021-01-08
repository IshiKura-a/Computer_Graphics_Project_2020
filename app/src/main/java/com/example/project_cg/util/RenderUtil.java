package com.example.project_cg.util;

import com.example.project_cg.shape.Model;
import com.example.project_cg.shape.MtlInfo;
import com.example.project_cg.shape.Shape;
import com.example.project_cg.shape.ShapeType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.LinkedList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RenderUtil {
    public static float[] base, dir, color, shape;
    public static MtlInfo mtlInfo;
    public static ShapeType type;
    public static String path;
    public static float fraction;
    public static int edges;
}
