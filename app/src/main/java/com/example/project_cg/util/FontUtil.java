package com.example.project_cg.util;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtil {
    public static Typeface gillSans;

    public static void init(Context context) {
        gillSans = Typeface.createFromAsset(context.getAssets(),
                "font/Gill Sans.otf");
    }
}
