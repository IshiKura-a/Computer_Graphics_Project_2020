package com.example.project_cg.html;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class JSInterfaceGetHTML {
    @JavascriptInterface
    public void getHtml(String html) {
        Log.i("HTML", html);
    }
}
