package com.example.project_cg;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.project_cg.asynctask.CheckStorage;
import com.example.project_cg.dialog.ShapeDialog;
import com.example.project_cg.html.HTMLManager;
import com.example.project_cg.layout.LightRecyclerAdapter;
import com.example.project_cg.layout.LinearItemDecoration;
import com.example.project_cg.layout.ObjectRecyclerAdapter;
import com.example.project_cg.menu.Menu;
import com.example.project_cg.observe.Light;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shader.ShaderMap;
import com.example.project_cg.shader.ShaderType;
import com.example.project_cg.shape.Model;
import com.example.project_cg.shape.MtlInfo;
import com.example.project_cg.shape.Shape;
import com.example.project_cg.shape.ShapeType;
import com.example.project_cg.texture.TextureManager;
import com.example.project_cg.util.FontUtil;
import com.example.project_cg.util.RenderUtil;
import com.example.project_cg.util.RequestUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity implements LightRecyclerAdapter.LightOnItemClickListener,
        ObjectRecyclerAdapter.ObjectOnItemClickListener {
    private GLSurfaceView mGlSurfaceView;
    private MainRender mRender;
    private Menu mMenu;
    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private RecyclerView mLightRecyclerView, mObjectRecyclerView;
    private LightRecyclerAdapter mLightRecyclerAdapter;
    private ObjectRecyclerAdapter mObjectRecyclerAdapter;

    private Float prevSpan = null;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    /**
     * The scale listener, used for handling multi-finger scale gestures.
     */
    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            float scale = detector.getScaleFactor();
            if(prevSpan == null) {
                Observe.getCamera().moveEye(0,0,scale > 1?-0.1f:0.1f);

            }
            else {
                if(detector.getCurrentSpan() > prevSpan + 1) {
                    Observe.getCamera().moveEye(0,0,-0.1f);
                }
                else if(detector.getCurrentSpan() < prevSpan - 1) {
                    Observe.getCamera().moveEye(0,0,0.1f);
                }
            }
            prevSpan = detector.getCurrentSpan();
            Log.i("Scale", scale+":"+detector.getCurrentSpan()+":"+detector.getPreviousSpan());
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    };

    /**
     * The gesture listener, used for handling simple gestures such as double touches, scrolls,
     * and flings.
     */
    private final GestureDetector.SimpleOnGestureListener mGestureListener
            = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i("Scroll",distanceX+":"+distanceY);
            //Observe.getCamera().moveLookAt(distanceX > 0?0.1f:-0.1f,distanceY > 0?0.1f:-0.1f);
            Observe.getCamera().moveLookAt(distanceX/100f,distanceY/100f);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };


    private void initView() {
        ((JoystickView)findViewById(R.id.joystick)).setOnMoveListener(
                (angle, strength) -> {
                    double theta = Math.toRadians(angle);
                    float step = 0.1f;
                    Observe.getCamera().moveEye((float)(step*Math.cos(theta)),(float)(step*Math.sin(theta)),0);
                });

        findViewById(R.id.img_0).setOnClickListener(notUsed -> {
                // Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();

            ValueAnimator animator = mMenu.isOpen() ? ValueAnimator.ofFloat(300f, 0f) : ValueAnimator.ofFloat(0f, 250f);
                animator.setDuration(600);
                animator.addUpdateListener(a -> {
                    Float animateVal = (Float)a.getAnimatedValue();
                    for(int i = 0; i< mMenu.getMenuList().size(); i++) {
                        View v = mMenu.getMenuList().get(i);

                        v.setVisibility(mMenu.isOpen()?View.VISIBLE:View.GONE);

                        float degree = 120.0f / mMenu.getMenuList().size() * i;

                        v.setTranslationX((float) (animateVal * Math.cos(Math.toRadians(degree))));
                        v.setTranslationY((float) (animateVal * Math.sin(Math.toRadians(degree))));

                        v.setRotation(360f * a.getAnimatedFraction());

                        if(animateVal > 0) {
                            v.setScaleX(animateVal / 250f);
                            v.setScaleY(animateVal / 250f);

                            v.setAlpha(animateVal / 250f);
                        }
                    }
                });
                mMenu.setOpen(!mMenu.isOpen());

                animator.start();
            });

        mLightRecyclerAdapter.setOnItemClickListener(this);

        mLightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLightRecyclerView.addItemDecoration(new LinearItemDecoration(Color.rgb(168, 165, 181)));
        mLightRecyclerView.swapAdapter(mLightRecyclerAdapter, true);

        mObjectRecyclerAdapter.setOnItemClickListener(this);

        mObjectRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mObjectRecyclerView.addItemDecoration(new LinearItemDecoration(Color.rgb(168, 165, 181)));
        mObjectRecyclerView.swapAdapter(mObjectRecyclerAdapter, true);

    }

    @Override
    public void onItemCLick(int position, Light light) {
        Toast.makeText(this, "light"+(position+1), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongCLick(int position, Light light) {
        // ignore
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this;
        new CheckStorage().execute(activity);

        ShaderMap.readShaders(getApplicationContext());
        TextureManager.readTextures(getApplicationContext());
        HTMLManager.readHTMLs(getApplicationContext());

        FontUtil.init(this);

        setContentView(R.layout.activity_main);

        mGlSurfaceView = findViewById(R.id.glSurfaceView);

        while(ShaderMap.get("object", ShaderType.FRAG) == null);
        mRender = new MainRender(this);
        mGlSurfaceView.setEGLContextClientVersion(2);
        mGlSurfaceView.setRenderer(mRender);

        mMenu = new Menu(this);

        mScaleGestureDetector = new ScaleGestureDetector(this, mScaleGestureListener);
        mGestureDetector = new GestureDetectorCompat(this, mGestureListener);

        mLightRecyclerView = findViewById(R.id.lightRecycler);
        mLightRecyclerAdapter = new LightRecyclerAdapter();

        mObjectRecyclerView = findViewById(R.id.objectRecycler);
        mObjectRecyclerAdapter = new ObjectRecyclerAdapter(mRender.getShapes());

        initView();
    }

    @Override
    public void onItemCLick(int position, Shape shape) {
        Toast.makeText(this, "Edit Object "+ position, Toast.LENGTH_SHORT).show();
        if (shape.getType() != ShapeType.MODEL) ShapeDialog.displayDialog(this, position);
    }

    @Override
    public void onItemLongCLick(int position, Shape shape) {
        Toast.makeText(this, "Delete Object "+ position, Toast.LENGTH_SHORT).show();
        mObjectRecyclerAdapter.remove(position);
        // do nothing
    }


    public GLSurfaceView getmGlSurfaceView() {
        return mGlSurfaceView;
    }

    public MainRender getmRender() {
        return mRender;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == RequestUtil.REQUEST_OBJ) {
                    // Get the Uri of the selected file
                    String path = Environment.getExternalStorageDirectory().getCanonicalPath()
                            + "/" + data.getData().getPath().split(":", 2)[1];
                    Log.i("Request File", path);
                    if (path.matches(".*\\.(obj)")) {
                        RenderUtil.type = ShapeType.MODEL;
                        RenderUtil.mtlInfo = new MtlInfo(new float[]{0.2f, 0.2f, 0.2f, 1},
                                new float[]{0.8f, 0.8f, 0.8f, 1}, new float[]{0.65f, 0.65f, 0.65f, 1}, 30);
                        RenderUtil.color = new float[]{0.8f, 0.5f, 0.3f, 1.0f};
                        RenderUtil.base = new float[]{0, 0, 0, 1};
                        RenderUtil.dir = new float[]{0, 0, 0};
                        RenderUtil.shape = new float[]{0.5f, 0.5f, 0.5f, 1};
                        RenderUtil.path = path;
                        mRender.addShape();
                    }

                } else if (requestCode == RequestUtil.REQUEST_PNG) {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));

                    FileOutputStream fos = new FileOutputStream("file://android_asset/png/tmp.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void notifyObjectsChanged(int position) {
        mObjectRecyclerAdapter.add(position);
    }
}
