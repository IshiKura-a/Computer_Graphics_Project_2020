package com.example.project_cg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.project_cg.html.JSInterfaceGetHTML;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shader.ShaderType;
import com.example.project_cg.shape.Ball;
import com.example.project_cg.shape.Cube;
import com.example.project_cg.shape.MtlInfo;
import com.example.project_cg.shape.Shape;
import com.example.project_cg.shape.ShapeType;
import com.example.project_cg.util.ColorUtil;
import com.example.project_cg.util.RenderUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShapeDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private Button confirm, cancel;
    private WebView webView;
    private Shape toEdit = null;
    private String content;

    public ShapeDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public ShapeDialog(Activity activity, Shape toEdit) {
        super(activity);
        this.activity = activity;
        this.toEdit = toEdit;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.shape_editor);
        confirm = findViewById(R.id.confirmShape);
        cancel = findViewById(R.id.cancelShape);
        webView = findViewById(R.id.shapeEditor);

        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/html/shape_editor.html");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmShape: {
                webView.evaluateJavascript(
                        "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                        html -> {
                            Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
                            Matcher m = p.matcher(html);
                            StringBuffer buf = new StringBuffer(html.length());
                            while (m.find()) {
                                String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
                                m.appendReplacement(buf, Matcher.quoteReplacement(ch));
                            }
                            m.appendTail(buf);
                            content = buf.toString().substring(1, buf.length())
                            .replaceAll("\\\\n","\n")
                            .replaceAll("\\\\\"","\"");

                            assignValue();
                        });
                this.dismiss();
                break;
            }
            case R.id.cancelShape: {
                this.dismiss();
                break;
            }
            default: {
                break;
            }
        }
    }

    private void assignValue() {
        Document document = Jsoup.parse(content);
        ShapeType type = ShapeType.getShapeType(document.getElementById("typeSelector").attributes().get("choose"));
        float baseX = Float.parseFloat(document.getElementById("baseX").attributes().get("value"));
        float baseY = Float.parseFloat(document.getElementById("baseY").attributes().get("value"));
        float baseZ = Float.parseFloat(document.getElementById("baseZ").attributes().get("value"));

        float rotateX = Float.parseFloat(document.getElementById("rotateX").attributes().get("value"));
        float rotateY = Float.parseFloat(document.getElementById("rotateY").attributes().get("value"));
        float rotateZ = Float.parseFloat(document.getElementById("rotateZ").attributes().get("value"));

        float[] color = ColorUtil.parseRGBA(document.getElementById("color").attributes().get("value"));

        float width = Float.parseFloat(document.getElementById("width").attributes().get("value"));
        float height = Float.parseFloat(document.getElementById("height").attributes().get("value"));
        float length = Float.parseFloat(document.getElementById("length").attributes().get("value"));
        float fraction = Float.parseFloat(document.getElementById("fraction").attributes().get("value"));
        int edges = Integer.parseInt(document.getElementById("edges").attributes().get("value"));

        float[] ambient = ColorUtil.parseRGBA(document.getElementById("ambient").attributes().get("value"));
        float[] diffuse = ColorUtil.parseRGBA(document.getElementById("diffuse").attributes().get("value"));
        float[] specular = ColorUtil.parseRGBA(document.getElementById("specular").attributes().get("value"));
        float shininess = Float.parseFloat(document.getElementById("shininess").attributes().get("value"));

        RenderUtil.base = new float[]{baseX, baseY, baseZ, 1.0f};
        RenderUtil.dir = new float[]{rotateX, rotateY, rotateZ};
        RenderUtil.color = color;
        RenderUtil.mtlInfo = new MtlInfo(ambient, diffuse, specular, shininess);
        RenderUtil.shape = new float[]{width, length, height, 1f};
        RenderUtil.fraction = fraction;
        RenderUtil.edges = edges;
        RenderUtil.type = type;

        if(toEdit != null) {
            //todo
        }
        else {
            ((MainActivity)activity).getmRender().addShape();
        }
    }
}
