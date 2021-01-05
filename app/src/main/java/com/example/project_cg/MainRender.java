package com.example.project_cg;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.*;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.util.Log;

import com.example.project_cg.shape.Model;
import com.example.project_cg.shape.MtlInfo;
import com.example.project_cg.observe.Light;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shape.Cube;
import com.example.project_cg.shape.Shape;
import com.example.project_cg.texture.TextureManager;
import com.example.project_cg.util.ScreenShotUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MainRender implements Renderer {
    private ArrayList<Shape> shapes = new ArrayList<>();
    int inc = 0;
    int cnt= 250;
    int dir = 2;

    static {
        System.loadLibrary("MainRender");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Observe.getCamera().setEye(0,5, 15, 1);
        Observe.setOrtho(false);
        Observe.getLightList().add(new Light()
                .setAmbient(new float[]{1f, 1f, 1f, 1f})
                .setDiffuse(new float[]{1f, 1f, 1f, 1f})
                .setSpecular(new float[]{1f, 1f, 1f, 1f})
                .setLocation(new float[]{5, 5, 20, 1}));

        MtlInfo tmpMtl = new MtlInfo(new float[]{0.2f, 0.2f, 0.2f, 1},
                new float[]{0.8f, 0.8f, 0.8f, 1}, new float[]{0.65f, 0.65f, 0.65f, 1}, 100);
        shapes.add(new Cube(new float[]{0, -10, 0, 1}, new float[]{3, 2, 1, 1}, new float[]{0, 0, 0},
                new float[]{1, 0, 0, 1}, tmpMtl));


        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(TextureManager.getAssetManager().open("object/Cube.obj")));
            Model model = Model.readObject(br);
            model.setMtl(new MtlInfo(new float[]{0.2f, 0.2f, 0.2f, 1},
                    new float[]{0.8f, 0.8f, 0.8f, 1}, new float[]{0.65f, 0.65f, 0.65f, 1}, 30));
            model.setColor(new float[]{0.9f, 0.1f, 0.3f, 1.0f});
            model.setBasePara(new float[]{0, -5, 0, 1});
            model.setShapePara(new float[]{1, 1, 1, 1});
            model.setRotateY(-90);
            shapes.add(model);

            br = new BufferedReader(new InputStreamReader(TextureManager.getAssetManager().open("object/Chair.obj")));
            model = Model.readObject(br);
            model.setMtl(new MtlInfo(new float[]{0.2f, 0.2f, 0.2f, 1},
                    new float[]{0.8f, 0.8f, 0.8f, 1}, new float[]{0.65f, 0.65f, 0.65f, 1}, 30));
            model.setColor(new float[]{0.3f, 0.3f, 0.3f, 1.0f});
            model.setBasePara(new float[]{0, 1, 0, 1});
            model.setShapePara(new float[]{0.5f, 0.5f, 0.5f, 1});
            model.setRotateY(-90);
            shapes.add(model);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ArrayList<Integer> al = new ArrayList<>();
        al.add(0);
        shapes.get(0).setTextureUsed(al);

        ArrayList<Integer> a2 = new ArrayList<>();
        a2.add(2);
        shapes.get(1).setTextureUsed(a2);
        onSurfaceCreatedCPP();

        for (Shape s : shapes) {
            s.onSurfaceCreated(gl, config);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // GLES20.glViewport(0,0,width,height);
        onSurfaceChangedCPP(width, height);
        Log.i("TAG", "Width:" + width + ",height:" + height);
        Observe.getPerspective().setRatio(width, height);
        Observe.getOrtho().setRatio(width, height);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (Observe.getCamera()) {
            // GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT)

            // Observe.getLightList().get(0).setLocation(new float[]{-20 + 0.1f * (cnt % 360), 5, 5, 1});

            shapes.get(0).setRotateX(cnt % 360);
            shapes.get(1).setRotateX(cnt % 360);
            shapes.get(1).setRotateY(cnt % 360);
            shapes.get(2).setRotateY(cnt % 360);
            onDrawFrameCPP();
            // Shapes should be drawn after the canvus
            for (Shape s : shapes) {
                s.onDrawFrame(gl);
            }

            cnt += dir * inc;
            inc = (inc + 1) % 2;
            if (cnt == 0 || cnt == 360) dir = -dir;


            if (ScreenShotUtil.toScreenShot) {
                ScreenShotUtil.toScreenShot = false;
                int width = ScreenShotUtil.width;
                int height = ScreenShotUtil.height;
                int screenshotSize = ScreenShotUtil.width * ScreenShotUtil.height;
                ScreenShotUtil.buf = ByteBuffer.allocateDirect(screenshotSize * 4);
                ScreenShotUtil.buf.order(ByteOrder.nativeOrder());
                gl.glReadPixels(0, 0, width, height, GL10.GL_RGBA,
                        GL10.GL_UNSIGNED_BYTE, ScreenShotUtil.buf);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {

                        int pixelsBuffer[] = new int[screenshotSize];
                        ScreenShotUtil.buf.asIntBuffer().get(pixelsBuffer);
                        ScreenShotUtil.buf = null;
                        Bitmap bitmap = Bitmap.createBitmap(width, height,
                                Bitmap.Config.RGB_565);
                        bitmap.setPixels(pixelsBuffer, screenshotSize - width, -width, 0,
                                0, width, height);
                        pixelsBuffer = null;

                        short sBuffer[] = new short[screenshotSize];
                        ShortBuffer sb = ShortBuffer.wrap(sBuffer);
                        bitmap.copyPixelsToBuffer(sb);

                        // Making created bitmap (from OpenGL points) compatible with
                        // Android bitmap
                        for (int i = 0; i < screenshotSize;++i) {
                            short v = sBuffer[i];
                            sBuffer[i] = (short) (((v & 0x1f) << 11) | (v & 0x7e0) | ((v & 0xf800) >> 11));
                        }
                        sb.rewind();
                        bitmap.copyPixelsFromBuffer(sb);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ScreenShotUtil.bos);
                        Log.i("Render", "SetFalse");

                        return null;
                    }
                }.execute();
            }
        }
    }

    private static native void onSurfaceCreatedCPP();
    private static native void onSurfaceChangedCPP(int width, int height);
    private static native void onDrawFrameCPP();

    public ArrayList<Shape> getShapes() {
        return shapes;
    }
}
