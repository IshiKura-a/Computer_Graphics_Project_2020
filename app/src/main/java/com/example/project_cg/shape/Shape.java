package com.example.project_cg.shape;


import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.example.project_cg.texture.TextureManager;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class Shape {
    enum DrawMethod {
        SIMPLE, STRIPE, FAN;
    }
    protected float rotateX = 0f, rotateY = 0f, rotateZ = 0f;
    protected FloatBuffer vertexBuffer;
    protected FloatBuffer textureBuffer;
    protected FloatBuffer normalBuffer;
    protected DrawMethod method = DrawMethod.SIMPLE;
    protected ShapeType type;
    protected boolean isChosen = false;

    protected int mProgram;

    protected MtlInfo mtl;
    protected boolean useTexture = false;
    protected float[] translatePara, scalePara, rotatePara;
    protected float[] basePara, shapePara;
    protected float[] model, affine, color;

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

    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        Log.e("Compile Error: ", GLES20.glGetShaderInfoLog(shader));
        return shader;
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

    public void setShapePara(float[] shapePara) {
        this.shapePara = shapePara;
    }

    public void updateModelMatrix() {
        model = new float[16];
        Matrix.setIdentityM(model, 0);

        Matrix.translateM(model, 0, basePara[0] / basePara[3], basePara[1] / basePara[3], basePara[2] / basePara[3]);
        Matrix.rotateM(model, 0, rotateX, 1, 0, 0);
        Matrix.rotateM(model, 0, rotateY, 0, 1, 0);
        Matrix.rotateM(model, 0, rotateZ, 0, 0, 1);
        Matrix.scaleM(model, 0, shapePara[0] / shapePara[3], shapePara[1] / shapePara[3], shapePara[2] / shapePara[3]);
    }

    public void setRotateX(float rotateX) {
        this.rotateX = rotateX;
    }

    public void setRotateY(float rotateY) {
        this.rotateY = rotateY;
    }

    public void setRotateZ(float rotateZ) {
        this.rotateZ = rotateZ;
    }

    public void updateAffineMatrix() {
        affine = new float[16];
        Matrix.setIdentityM(affine, 0);

        Matrix.translateM(affine, 0, translatePara[0] / translatePara[3], translatePara[1] / translatePara[3], translatePara[2] / translatePara[3]);
        Matrix.rotateM(affine, 0, rotatePara[0], rotatePara[1], rotatePara[2], rotatePara[3]);
        Matrix.scaleM(affine, 0, scalePara[0] / scalePara[3], scalePara[1] / scalePara[3], scalePara[2] / scalePara[3]);
    }

    public void updateTexture() {
        if(useTexture)
            for(Integer i: textureUsed) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, i);
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, TextureManager.getTexture(i).getBitmap(), 0);

                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);
            }
    }

    public void setMtl(MtlInfo mtl) {
        this.mtl = mtl;
    }

    abstract public void onSurfaceCreated(GL10 gl, EGLConfig config);

    abstract public void onDrawFrame(GL10 gl);

    public void setColor(float[] rgba) {
        color = rgba.clone();
    }

    public ShapeType getType() {
        return type;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }
}
