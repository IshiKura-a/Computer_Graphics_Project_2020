package com.example.project_cg;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.*;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.util.Log;

import com.example.project_cg.shape.Ball;
import com.example.project_cg.shape.Cone;
import com.example.project_cg.shape.Cylinder;
import com.example.project_cg.shape.Frustum;
import com.example.project_cg.shape.Model;
import com.example.project_cg.shape.MtlInfo;
import com.example.project_cg.observe.Light;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shape.Cube;
import com.example.project_cg.shape.Prism;
import com.example.project_cg.shape.Pyramid;
import com.example.project_cg.shape.Shape;
import com.example.project_cg.shape.ShapeType;
import com.example.project_cg.texture.Texture;
import com.example.project_cg.texture.TextureManager;
import com.example.project_cg.util.RenderUtil;
import com.example.project_cg.util.ScreenShotUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MainRender implements Renderer {
    int cnt = 250;
    float tra = -0.5f;
    private LinkedList<Shape> shapes = new LinkedList<>();
    int inc = 0;
    private MainActivity activity;
    int dir = 2;
    int used = 0;
    float traflag=0.005f;
    int flag=0;

    static {
        System.loadLibrary("MainRender");
    }

    public MainRender(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        TextureManager.readTextures(activity);
        synchronized (TextureManager.bitmapBuffer) {
            while(TextureManager.bitmapBuffer.size() > 0) {
                Texture texture = TextureManager.bitmapBuffer.removeFirst();

                if (TextureManager.cnt >= 32) throw new IndexOutOfBoundsException("Exceed maximum texture counts!");

                int[] tmp = new int[1];
                GLES20.glGenTextures(1, TextureManager.textureIds, TextureManager.cnt);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureManager.textureIds[TextureManager.cnt]);

                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);

                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture.getBitmap(), 0);
                TextureManager.textureNames[TextureManager.cnt] = texture.getName();
                TextureManager.textureNameMap.put(texture.getName(), TextureManager.cnt++);
                texture.getBitmap().recycle();
            }
        }
        // Observe.getCamera().setEye(0,5, 15, 1);
        Observe.setOrtho(false);
        Observe.getLightList().add(new Light()
                .setAmbient(new float[]{1f, 1f, 1f, 1f})
                .setDiffuse(new float[]{1f, 1f, 1f, 1f})
                .setSpecular(new float[]{1f, 1f, 1f, 1f})
                .setLocation(new float[]{5, 5, 20, 1}));

        MtlInfo tmpMtl = new MtlInfo(new float[]{0.2f, 0.2f, 0.2f, 1},
                new float[]{0.8f, 0.8f, 0.8f, 1}, new float[]{0.65f, 0.65f, 0.65f, 1}, 100);
        shapes.add(new Cube(new float[]{0, 1, 0, 1}, new float[]{3, 2, 1, 1}, new float[]{0, 0, 0},
                new float[]{1, 0, 0, 1}, tmpMtl));


        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(TextureManager.getAssetManager().open("object/Chair.obj")));
            Model model = Model.readObject(new Model(), br);
            model.setMtl(new MtlInfo(new float[]{0.2f, 0.2f, 0.2f, 1},
                    new float[]{0.8f, 0.8f, 0.8f, 1}, new float[]{0.65f, 0.65f, 0.65f, 1}, 30));
            model.setColor(new float[]{0.9f, 0.1f, 0.3f, 1.0f});
            model.setBasePara(new float[]{0, -5, 0, 1});
            model.setShapePara(new float[]{0.5f, 0.5f, 0.5f, 1});
            // model.setRotateX(90);
            shapes.add(model);


        } catch (IOException e) {
            e.printStackTrace();
        }

        LinkedList<Integer> al = new LinkedList<>();
        al.add(0);
        shapes.get(0).setTextureUsed(al);

        LinkedList<Integer> a2 = new LinkedList<>();
        a2.add(2);
        //shapes.get(1).setTextureUsed(a2);

        flushScreen(gl, config);
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
        synchronized (TextureManager.bitmapBuffer) {
            while(TextureManager.bitmapBuffer.size() > 0) {
                Texture texture = TextureManager.bitmapBuffer.removeFirst();

                if (TextureManager.cnt >= 32) throw new IndexOutOfBoundsException("Exceed maximum texture counts!");

                int[] tmp = new int[1];
                GLES20.glGenTextures(1, TextureManager.textureIds, TextureManager.cnt);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureManager.textureIds[TextureManager.cnt]);

                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);

                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture.getBitmap(), 0);
                TextureManager.textureNames[TextureManager.cnt] = texture.getName();
                TextureManager.textureNameMap.put(texture.getName(), TextureManager.cnt++);
                texture.getBitmap().recycle();
            }
        }

        synchronized (Observe.getCamera()) {
            onDrawFrameCPP();

            // Observe.getLightList().get(0).setLocation(new float[]{-20 + 0.1f * (cnt % 360), 5, 5, 1});

            if(shapes.size() > 0) {
                shapes.get(0).setTranslatePara(new float[]{-tra,0,0,1});
                shapes.get(0).setRotateX(cnt % 360);
            }
            if(shapes.size() > 1) {
                shapes.get(1).setRotateY(cnt % 360);
            }
            if(shapes.size() > 2) {
                shapes.get(2).setRotateY(cnt % 360);
            }

            if(used == 1) {
                Shape s = null;
                if (RenderUtil.type == ShapeType.CUBE) {
                    shapes.add(s = new Cube(RenderUtil.base, RenderUtil.shape, RenderUtil.dir, RenderUtil.color, RenderUtil.mtlInfo));
                } else if (RenderUtil.type == ShapeType.BALL) {
                    shapes.add(s = new Ball(RenderUtil.base, RenderUtil.shape, RenderUtil.dir, RenderUtil.color, RenderUtil.mtlInfo));
                } else if (RenderUtil.type == ShapeType.CONE) {
                    shapes.add(s = new Cone(RenderUtil.base, RenderUtil.shape, RenderUtil.dir, RenderUtil.color, RenderUtil.mtlInfo));
                } else if (RenderUtil.type == ShapeType.CYLINDER) {
                    shapes.add(s = new Cylinder(RenderUtil.base, RenderUtil.shape, RenderUtil.dir, RenderUtil.color, RenderUtil.mtlInfo));
                } else if (RenderUtil.type == ShapeType.PRISM) {
                    shapes.add(s = new Prism(RenderUtil.base, RenderUtil.shape, RenderUtil.dir, RenderUtil.color, RenderUtil.mtlInfo, RenderUtil.edges));
                } else if (RenderUtil.type == ShapeType.PYRAMID) {
                    shapes.add(s = new Pyramid(RenderUtil.base, RenderUtil.shape, RenderUtil.dir, RenderUtil.color, RenderUtil.mtlInfo, RenderUtil.edges));
                } else if (RenderUtil.type == ShapeType.FRUSTUM) {
                    shapes.add(s = new Frustum(RenderUtil.base, RenderUtil.shape, RenderUtil.dir, RenderUtil.color, RenderUtil.mtlInfo,
                            RenderUtil.fraction, RenderUtil.edges));
                } else if (RenderUtil.type == ShapeType.MODEL) {
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new InputStreamReader(new FileInputStream(RenderUtil.path)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    s = Model.readObject(new Model(), br);
                    s.setMtl(RenderUtil.mtlInfo);
                    s.setColor(RenderUtil.color);
                    s.setBasePara(RenderUtil.base);
                    s.setShapePara(RenderUtil.shape);
                    s.setRotateX(RenderUtil.dir[0]);
                    s.setRotateY(RenderUtil.dir[1] - 90);
                    s.setRotateZ(RenderUtil.dir[2]);
                    shapes.add(s);
                }

                if (s != null) {
                    if (RenderUtil.texture == -1) {
                        s.disableTexture();
                    } else {
                        LinkedList<Integer> tmp = new LinkedList<>();
                        tmp.add(RenderUtil.texture);
                        s.setTextureUsed(tmp);
                    }
                }

                used--;
            }

            // Shapes should be drawn after the canvus
            for (Shape s : shapes) {
                s.onDrawFrame(gl);
            }

            cnt += dir * inc;
            inc = (inc + 1) % 2;
            if (cnt % 360 == 0) dir = -dir;

            tra+=traflag;
            flag+=1;
            if(flag%100==0)
            {
                traflag=-traflag;
            }


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

    public LinkedList<Shape> getShapes() {
        return shapes;
    }

    public void flushScreen(GL10 gl, EGLConfig config) {
        onSurfaceCreatedCPP();
        for (Shape s : shapes) {
            s.onSurfaceCreated(gl, config);
        }
    }

    public void addShape() {
        used++;
    }

    public boolean addDone() {
        return used == 0;
    }
}