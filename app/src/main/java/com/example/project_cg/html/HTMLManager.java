package com.example.project_cg.html;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.util.Log;

import com.example.project_cg.R;
import com.example.project_cg.shader.ShaderType;
import com.example.project_cg.texture.Texture;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;

public class HTMLManager {
    private static HashMap<String, Document> htmls = new HashMap<>();

    public static void readHTMLs(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            for(String s: assetManager.list("html")) {
                Document document = Jsoup.parse(assetManager.open("html/"+s), "utf-8", "file://android_asset/html/");
                htmls.put(s, document);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Document get(String name) {
        return htmls.get(name);
    }
}
