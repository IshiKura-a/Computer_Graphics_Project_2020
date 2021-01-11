package com.example.project_cg.texture;

import android.graphics.Bitmap;

public class Texture {
    private Bitmap bitmap;
    private String name;
    Texture(Bitmap bm, String name) {
        this.bitmap = bm;
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getName() {
        return name;
    }
}
