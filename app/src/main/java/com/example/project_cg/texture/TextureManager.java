package com.example.project_cg.texture;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.util.ArrayList;

public class TextureManager {
    static {
        System.loadLibrary("BmpReader");
    }

    private static AssetManager assetManager;
    private static int[] textureID = new int[10];
    private static ArrayList<Texture> textures = new ArrayList<>();
    public static void readTextures(Context context) {
        try {
            assetManager = context.getAssets();
            for(String s: assetManager.list("bmp/")) {
                byte[] c = ("bmp/"+s).getBytes();
                int[] color = loadBmpCPP(c, assetManager);
                Bitmap bitmap = Bitmap.createBitmap(color, getWidthCPP(), getHeightCPP(), Bitmap.Config.ARGB_8888);
                textures.add(new Texture(bitmap));
            }
            for(String s: assetManager.list("png")) {
                Bitmap bitmap = BitmapFactory.decodeStream(assetManager.open("png/"+s));
                assert bitmap != null;
                textures.add(new Texture(bitmap));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Texture getTexture(int index) {
        return textures.get(index);
    }

    public static int getTextureCount() {
        return textures.size();
    }

    public static int[] getTextureID() {
        return textureID;
    }

    private static native int[] loadBmpCPP(byte[] bFileName, AssetManager assetManager);
    private static native int getWidthCPP();
    private static native int getHeightCPP();
}
