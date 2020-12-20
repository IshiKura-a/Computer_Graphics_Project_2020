package com.example.project_cg.shader;

import android.animation.TypeEvaluator;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.project_cg.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;

public class ShaderMap {
    private static final HashMap<String, String> vertex = new HashMap<>();
    private static final HashMap<String, String> fragment = new HashMap<>();

    public static void readShaders(Context context) {
        Field[] fields = R.raw.class.getFields();
        for (Field f : fields) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    String name = f.getName();
                    String header = name.substring(0, name.length() - 5);
                    String type = name.substring(name.length() - 4);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(context.getResources()
                                    .openRawResource(context.getResources()
                                            .getIdentifier(name, "raw", context.getPackageName()))));

                    StringBuffer sb = new StringBuffer();
                    String line;
                    try {
                        while ((line = in.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                    } catch (IOException ioe) {
                        // do nothing
                    }

                    ShaderType shaderType = ShaderType.get(type);
                    if (shaderType == ShaderType.FRAG) {
                        Log.i("TAG", "frag: " + header);
                        fragment.put(header.toLowerCase(), sb.toString());
                    } else if (shaderType == ShaderType.VERT) {
                        Log.i("TAG", "vert: " + header);
                        vertex.put(header.toLowerCase(), sb.toString());
                    }
                    // Log.i("TAG", sb.toString());
                    return null;
                }
            }.execute();
        }
    }

    public static String get(String shaderName, ShaderType type) {
        if(type == ShaderType.FRAG) {
            return fragment.get(shaderName.toLowerCase());
        }
        else if(type == ShaderType.VERT) {
            return vertex.get(shaderName.toLowerCase());
        }
        else return null;
    }
}
