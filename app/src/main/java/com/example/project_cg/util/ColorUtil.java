package com.example.project_cg.util;

import android.os.Build;
import android.util.Log;

public class ColorUtil {
    public static float[] parseRGBA(String color) {
        String padding = "#FFFFFFFF";
        Log.i("Before", color);
        if (color.length() < 9) {
            color = color + padding.substring(color.length(), padding.length());
        }
        Log.i("After", color);
        float r, g, b, a;
        r = Integer.parseUnsignedInt(color.substring(1, 3), 16) / 255f;
        g = Integer.parseUnsignedInt(color.substring(3, 5), 16) / 255f;
        b = Integer.parseUnsignedInt(color.substring(5, 7), 16) / 255f;
        a = Integer.parseUnsignedInt(color.substring(7, 9), 16) / 255f;

        Log.i("Parse", r+":"+g+":"+b+":"+a);
        return new float[]{r, g, b, a};
    }
}
