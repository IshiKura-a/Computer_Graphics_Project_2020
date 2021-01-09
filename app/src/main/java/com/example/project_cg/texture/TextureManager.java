package com.example.project_cg.texture;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static android.opengl.GLU.gluErrorString;

public class TextureManager {
    static {
        System.loadLibrary("BmpReader");
    }

    private static AssetManager assetManager;
    public static int cnt = 0;
    public static int[] textureIds = new int[32];
    public static String[] textureNames = new String[32];
    public static HashMap<String, Integer> textureNameMap = new HashMap<>();
    public static final LinkedList<Texture> bitmapBuffer = new LinkedList<>();

    public static void readTextures(Context context) {
        try {
            assetManager = context.getAssets();
            for (String s : assetManager.list("bmp/")) {
                byte[] c = ("bmp/" + s).getBytes();
                int[] color = loadBmpCPP(c, assetManager);
                Bitmap bitmap = Bitmap.createBitmap(color, getWidthCPP(), getHeightCPP(), Bitmap.Config.ARGB_8888);

                loadTexture(bitmap, s);
            }
            for (String s : assetManager.list("png")) {
                Bitmap bitmap = BitmapFactory.decodeStream(assetManager.open("png/" + s));
                assert bitmap != null;

                loadTexture(bitmap, s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getTextureCount() {
        return cnt;
    }

    public static AssetManager getAssetManager() {
        return assetManager;
    }

    private static native int[] loadBmpCPP(byte[] bFileName, AssetManager assetManager);

    private static native int getWidthCPP();

    private static native int getHeightCPP();

    public static ArrayList<String> getAll() {
        ArrayList<String> res = new ArrayList<>();
        for (String s : textureNameMap.keySet()) {
            res.add("<option id=\"" + s + "\" value=\"" + s + "\">" + s + "</option>");
        }
        return res;
    }

    public static String getTextureNameByIndex(int index) {
        if (index == -1) return "NotUsed";
        else return textureNames[index];
    }

    public static Integer getTextureIdByName(String name) {
        if (name.compareTo("NotUsed") == 0) return -1;
        else return textureIds[textureNameMap.get(name)];
    }

    public static int getTextureIdByIndex(int index) {
        return textureIds[index];
    }

    public static void loadTexture(Bitmap bitmap, String name) {
        synchronized (bitmapBuffer) {
            bitmapBuffer.add(new Texture(bitmap, name));
        }
    }
}
