package com.example.project_cg.texture;

import android.graphics.Bitmap;

public class Texture {
    private Bitmap bitmap;
    Texture(Bitmap bm) {
        this.bitmap = bm;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
