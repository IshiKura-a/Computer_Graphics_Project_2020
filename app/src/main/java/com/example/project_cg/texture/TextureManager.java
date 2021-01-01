package com.example.project_cg.texture;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TextureManager {
    static {
        System.loadLibrary("BmpReader");
    }

    private static AssetManager assetManager;
    private static HashMap<String, Integer> textureNames = new HashMap<>();
    private static ArrayList<Texture> textures = new ArrayList<>();
    public static void readTextures(Context context) {
        try {
            assetManager = context.getAssets();
            int[] index = new int[1];
            for(String s: assetManager.list("bmp/")) {
                byte[] c = ("bmp/"+s).getBytes();
                int[] color = loadBmpCPP(c, assetManager);
                Bitmap bitmap = Bitmap.createBitmap(color, getWidthCPP(), getHeightCPP(), Bitmap.Config.ARGB_8888);

                GLES20.glGenTextures(1, index, 0);
                textureNames.put(s, index[0]);
                textures.add(new Texture(bitmap));
            }
            for(String s: assetManager.list("png")) {
                Bitmap bitmap = BitmapFactory.decodeStream(assetManager.open("png/"+s));
                assert bitmap != null;

                GLES20.glGenTextures(1, index, 0);
                textureNames.put(s, index[0]);
                textures.add(new Texture(bitmap));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Texture getTexture(String name) {
        Integer index = textureNames.get(name);
        if(index != null)
            return getTexture(index);
        else return null;
    }

    public static Texture getTexture(int index) {
        return textures.get(index);
    }

    public static int getTextureCount() {
        return textures.size();
    }

    public static AssetManager getAssetManager() {
        return assetManager;
    }

    private static native int[] loadBmpCPP(byte[] bFileName, AssetManager assetManager);
    private static native int getWidthCPP();
    private static native int getHeightCPP();
}
