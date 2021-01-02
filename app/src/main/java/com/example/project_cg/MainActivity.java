package com.example.project_cg;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_cg.observe.Observe;
import com.example.project_cg.shader.ShaderMap;
import com.example.project_cg.shape.Model;
import com.example.project_cg.shape.Shape;
import com.example.project_cg.texture.TextureManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private GLSurfaceView glSurfaceView;
    private MainRender render;

    private void initView() {
        // findViewById(R.id.btn_1).setOnClickListener(this);
        ((JoystickView)findViewById(R.id.joystick)).setOnMoveListener(
                (angle, strength) -> {
                    float theta = (float)(angle / 180f * Math.PI);
                    float step = 0.1f;
                    Observe.getCamera().moveEye((float)(step*Math.cos(theta)),(float)(step*Math.sin(theta)),0);
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this;
        new CheckStorage().execute(activity);

        ShaderMap.readShaders(getApplicationContext());
        TextureManager.readTextures(getApplicationContext());

        setContentView(R.layout.activity_main);

        glSurfaceView = (GLSurfaceView) findViewById(R.id.glSurfaceView);

        render = new MainRender();
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(render);

        initView();
    }

    @Override
    @SuppressLint("StaticFieldLeak")
    public void onClick(View v) {
        /*
        new CheckStorage() {
                    @Override
                    protected void onPostExecute(MainActivity activity) {
                        super.onPostExecute(activity);
                        new ExportObj().execute(activity, new ArrayList<>(render.getShapes().subList(0, 3)));
                    }
                }.execute(this);
         */
    }

    class ExportObj extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... voids) {
            Activity activity = (Activity) voids[0];
            ArrayList<Shape> list = (ArrayList<Shape>) voids[1];
            if (ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE")
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    FileOutputStream fos = openFileOutput("res.obj", MODE_PRIVATE);

                    Model.writeObject(list, fos);
                    Log.i("Storage", getFilesDir().getAbsolutePath());
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}

class CheckStorage extends AsyncTask<Object, Void, MainActivity> {
    @Override
    protected MainActivity doInBackground(Object... params) {
        int permission;
        MainActivity activity = (MainActivity) params[0];
        try {
            //检测是否有写的权限
            permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, new String[]{
                        "android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activity;
    }
}
