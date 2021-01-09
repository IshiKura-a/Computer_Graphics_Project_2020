package com.example.project_cg.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.example.project_cg.MainActivity;
import com.example.project_cg.R;
import com.example.project_cg.html.HTMLManager;
import com.example.project_cg.shape.Frustum;
import com.example.project_cg.shape.MtlInfo;
import com.example.project_cg.shape.Prism;
import com.example.project_cg.shape.Pyramid;
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
    private int toEdit = -1;
    private String content;

    public ShapeDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public ShapeDialog(Activity activity, int toEdit) {
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

        Document document = HTMLManager.get("shape_editor.html");
        if(toEdit != -1) {
            Shape s = ((MainActivity)activity).getmRender().getShapes().get(toEdit);
            float[] base = s.getBasePara();
            float[] shape = s.getShapePara();
            float rotateX = s.getRotateX();
            float rotateY = s.getRotateY();
            float rotateZ = s.getRotateZ();
            ShapeType type = s.getType();
            String color = ColorUtil.parseFloat(s.getColor());
            MtlInfo mtlInfo = s.getMtl();


            document.getElementById("baseX").attr("value", ""+(base[0]/base[3]));
            document.getElementById("baseY").attr("value", ""+(base[1]/base[3]));
            document.getElementById("baseZ").attr("value", ""+(base[2]/base[3]));

            document.getElementById("rotateX").attr("value", ""+rotateX);
            document.getElementById("rotateY").attr("value", ""+rotateY);
            document.getElementById("rotateZ").attr("value", ""+rotateZ);

            document.getElementById("color").attr("value", color);
            String style = document.getElementById("colorDisplayer").attributes().get("style");
            style = style.replaceAll("background: #[0-9a-fA-F]{8};", "background: " + color + ";");
            document.getElementById("colorDisplayer").attr("style", style);

            String ambient = ColorUtil.parseFloat(mtlInfo.kAmbient);
            document.getElementById("ambient").attr("value", ambient);
            style = document.getElementById("ambientDisplayer").attributes().get("style");
            style = style.replaceAll("background: #[0-9a-fA-F]{8};", "background: " + ambient + ";");
            document.getElementById("ambientDisplayer").attr("style", style);

            String diffuse = ColorUtil.parseFloat(mtlInfo.kDiffuse);
            document.getElementById("diffuse").attr("value", diffuse);
            style = document.getElementById("diffuseDisplayer").attributes().get("style");
            style = style.replaceAll("background: #[0-9a-fA-F]{8};", "background: " + diffuse + ";");
            document.getElementById("diffuseDisplayer").attr("style", style);

            String specular = ColorUtil.parseFloat(mtlInfo.kSpecular);
            document.getElementById("specular").attr("value", specular);
            style = document.getElementById("specularDisplayer").attributes().get("style");
            style = style.replaceAll("background: #[0-9a-fA-F]{8};", "background: " + specular + ";");
            document.getElementById("specularDisplayer").attr("style", style);

            document.getElementById("shininess").attr("value", ""+mtlInfo.shininess);

            document.getElementById("typeSelector").attr("disabled", true);
            document.getElementById("typeSelector").attr("choose", type.getName());

            if(type.getName().matches("(Prism|Pyramid|Frustum)")) {
                style = document.getElementsByClass("Edges").get(0).attributes().get("style");
                style = style.replaceAll("list: [a-zA-Z]*;", "list: inline;");
                document.getElementsByClass("Edges").get(0).attr("style", style);
                document.getElementById("edges").attr("disabled", true);

                if(type == ShapeType.PRISM) {
                    document.getElementsByClass("Edges").get(0).attr("value", ""+((Prism) s).getEdge());
                }
                else if(type == ShapeType.PYRAMID) {
                    document.getElementsByClass("Edges").get(0).attr("value", ""+((Pyramid) s).getEdge());
                }
                else if(type == ShapeType.FRUSTUM) {
                    document.getElementsByClass("Edges").get(0).attr("value", ""+((Frustum) s).getEdge());
                    document.getElementsByClass("Fraction").get(0).attr("value", ""+((Frustum) s).getFraction());
                    document.getElementById("fraction").attr("disabled", true);

                    style = document.getElementsByClass("Fraction").get(0).attributes().get("style");
                    style = style.replaceAll("list: [a-zA-Z]*;", "list: inline;");
                    document.getElementsByClass("Fraction").get(0).attr("style", style);
                }
            }
        }
        webView.loadDataWithBaseURL("file:///android_asset/html/", document.html(), "text/html", "utf-8", null);
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

        if(toEdit != -1) {
            Shape s = ((MainActivity)activity).getmRender().getShapes().get(toEdit);
            s.setBasePara(RenderUtil.base);
            s.setColor(RenderUtil.color);
            s.setMtl(RenderUtil.mtlInfo);
            s.setShapePara(RenderUtil.shape);
            s.setRotateX(rotateX);
            s.setRotateY(rotateY);
            s.setRotateZ(rotateZ);
        }
        else {
            int position = ((MainActivity)activity).getmRender().getShapes().size();
            ((MainActivity)activity).getmRender().addShape();
            while(!((MainActivity)activity).getmRender().addDone());
            ((MainActivity)activity).notifyObjectsChanged(position);
        }
    }

    public static void displayDialog(Activity activity) {
        displayDialog(activity, -1);
    }

    public static void displayDialog(Activity activity, int toEdit) {
        ShapeDialog dialog = new ShapeDialog(activity, toEdit);
        dialog.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        dialog.getWindow().setLayout((int) (displayMetrics.widthPixels / 1.5f), (int) (displayMetrics.heightPixels / 1.3f));
    }
}
