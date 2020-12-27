package com.example.project_cg.shape;


import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.example.project_cg.texture.TextureManager;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class Shape {
    protected boolean useTexture = false;
    protected float[] translatePara, scalePara, rotatePara;
    protected float[] basePara, shapePara, dirPara;
    protected float[] model, affine;

    protected ArrayList<Integer> textureUsed = new ArrayList<>();
    public void setTextureUsed(ArrayList<Integer> textureUsed) {
        enableTexture();
        this.textureUsed.clear();
        this.textureUsed.addAll(textureUsed);
        updateTexture();
    }
    public void enableTexture() {
        useTexture = true;
    }
    public void disableTexture() {
        useTexture = false;
    }
    public void updateTexture() {
        if(useTexture)
            for(Integer i: textureUsed) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureManager.getTextureID()[i]);
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, TextureManager.getTexture(i).getBitmap(), 0);

                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);
            }
    }

    public void setRotatePara(float[] rotatePara) {
        this.rotatePara = rotatePara;
    }

    public void setScalePara(float[] scalePara) {
        this.scalePara = scalePara;
    }

    public void setTranslatePara(float[] translatePara) {
        this.translatePara = translatePara;
    }

    public void setBasePara(float[] basePara) {
        this.basePara = basePara;
    }

    public void setDirPara(float[] dirPara) {
        this.dirPara = dirPara;
    }

    public void setShapePara(float[] shapePara) {
        this.shapePara = shapePara;
    }

    public void updateModelMatrix() {
        model = new float[16];
        Matrix.setIdentityM(model, 0);

        Matrix.translateM(model, 0, basePara[0] / basePara[3], basePara[1] / basePara[3], basePara[2] / basePara[3]);
        Matrix.rotateM(model, 0, 90, 1, 0, 0);
        Matrix.rotateM(model, 0, dirPara[0], dirPara[1], dirPara[2], dirPara[3]);
        Matrix.scaleM(model, 0, shapePara[0] / shapePara[3], shapePara[1] / shapePara[3], shapePara[2] / shapePara[3]);
    }

    public void updateAffineMatrix() {
        affine = new float[16];
        Matrix.setIdentityM(affine, 0);

        Matrix.translateM(affine, 0, translatePara[0] / translatePara[3], translatePara[1] / translatePara[3], translatePara[2] / translatePara[3]);
        Matrix.rotateM(affine, 0, rotatePara[0], rotatePara[1], rotatePara[2], rotatePara[3]);
        Matrix.scaleM(affine, 0, scalePara[0] / scalePara[3], scalePara[1] / scalePara[3], scalePara[2] / scalePara[3]);
    }

    abstract public void onSurfaceCreated(GL10 gl, EGLConfig config);
    abstract public void onDrawFrame(GL10 gl);
    abstract public void setColor(float[] rgba);
}
