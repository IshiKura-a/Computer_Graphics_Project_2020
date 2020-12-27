package com.example.project_cg;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.TextView;

import com.example.project_cg.shader.ShaderMap;
import com.example.project_cg.texture.TextureManager;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShaderMap.readShaders(getApplicationContext());
        TextureManager.readTextures(getApplicationContext());
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new MainRender());
        setContentView(glSurfaceView);
    }
}