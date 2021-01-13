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
import com.example.project_cg.observe.Light;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.util.ColorUtil;
import com.example.project_cg.util.RequestUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LightDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private Button confirm, cancel;
    private WebView webView;
    private int toEdit = -1;
    private String content;

    public LightDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public LightDialog(Activity activity, int toEdit) {
        super(activity);
        this.activity = activity;
        this.toEdit = toEdit;
    }

    public static void displayDialog(Activity activity) {
        displayDialog(activity, -1);
    }

    public static void displayDialog(Activity activity, int toEdit) {
        LightDialog dialog = new LightDialog(activity, toEdit);
        dialog.show();

        dialog.getWindow().setLayout((int) (RequestUtil.widthPixels / 1.5f), (int) (RequestUtil.heightPixels / 1.3f));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.editor);
        confirm = findViewById(R.id.confirmBtn);
        cancel = findViewById(R.id.cancelBtn);
        webView = findViewById(R.id.editor);

        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        Document document = HTMLManager.get("light_editor.html").clone();

        if (toEdit >= 0) {
            Light light = Observe.getLightList().get(toEdit);
            float[] location = light.getLocation();

            document.getElementById("locationX").attr("value", "" + (location[0] / location[3]));
            document.getElementById("locationY").attr("value", "" + (location[1] / location[3]));
            document.getElementById("locationZ").attr("value", "" + (location[2] / location[3]));

            String style;
            String ambient = ColorUtil.parseFloat(light.getAmbient());
            document.getElementById("ambient").attr("value", ambient);
            style = document.getElementById("ambientDisplayer").attributes().get("style");
            style = style.replaceAll("background: #[0-9a-fA-F]{8};", "background: " + ambient + ";");
            document.getElementById("ambientDisplayer").attr("style", style);

            String diffuse = ColorUtil.parseFloat(light.getDiffuse());
            document.getElementById("diffuse").attr("value", diffuse);
            style = document.getElementById("diffuseDisplayer").attributes().get("style");
            style = style.replaceAll("background: #[0-9a-fA-F]{8};", "background: " + diffuse + ";");
            document.getElementById("diffuseDisplayer").attr("style", style);

            String specular = ColorUtil.parseFloat(light.getSpecular());
            document.getElementById("specular").attr("value", specular);
            style = document.getElementById("specularDisplayer").attributes().get("style");
            style = style.replaceAll("background: #[0-9a-fA-F]{8};", "background: " + specular + ";");
            document.getElementById("specularDisplayer").attr("style", style);

        }
        webView.loadDataWithBaseURL("file:///android_asset/html/", document.html(), "text/html", "utf-8", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn: {
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
                                    .replaceAll("\\\\n", "\n")
                                    .replaceAll("\\\\\"", "\"");

                            assignValue();
                        });
                this.dismiss();
                break;
            }
            case R.id.cancelBtn: {
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
        float locationX = Float.parseFloat(document.getElementById("locationX").attributes().get("value"));
        float locationY = Float.parseFloat(document.getElementById("locationY").attributes().get("value"));
        float locationZ = Float.parseFloat(document.getElementById("locationZ").attributes().get("value"));

        float[] ambient = ColorUtil.parseRGBA(document.getElementById("ambient").attributes().get("value"));
        float[] diffuse = ColorUtil.parseRGBA(document.getElementById("diffuse").attributes().get("value"));
        float[] specular = ColorUtil.parseRGBA(document.getElementById("specular").attributes().get("value"));

        float[] location = new float[]{locationX, locationY, locationZ, 1f};

        int pos = 0;
        synchronized (Observe.getLightList()) {
            if (toEdit >= 0) {
                Light light = Observe.getLightList().get(toEdit);
                light.setLocation(location);
                light.setAmbient(ambient);
                light.setDiffuse(diffuse);
                light.setSpecular(specular);
                ((MainActivity) activity).notifyLightsChanged(toEdit);
            } else {
                pos = Observe.getLightList().size();
                Observe.getLightList().add(new Light(location, ambient, diffuse, specular));
            }
        }
        if (toEdit < 0) ((MainActivity) activity).notifyLightsAdded(pos);
    }
}
