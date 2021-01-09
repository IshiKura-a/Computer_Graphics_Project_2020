package com.example.project_cg.html;

import android.content.Context;
import android.content.res.AssetManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;

public class HTMLManager {
    private static HashMap<String, Document> htmls = new HashMap<>();

    public static void readHTMLs(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            for(String s: assetManager.list("html")) {
                Document document = Jsoup.parse(assetManager.open("html/"+s), "utf-8", "file:///android_asset/html/");
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
