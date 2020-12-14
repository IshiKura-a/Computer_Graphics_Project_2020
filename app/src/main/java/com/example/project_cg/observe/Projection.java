package com.example.project_cg.observe;

public abstract class Projection {
    protected float ratio;
    protected float left, right;
    protected float bottom, top;
    protected float near, far;
    protected float[] mProjectionMatrix = new float[16];

    public Projection setNear(float near) {
        this.near = near;
        return this;
    }

    public Projection setFar(float far) {
        this.far = far;
        return this;
    }

    public Projection setLeft(float left) {
        this.left = left;
        return this;
    }

    public Projection setRight(float right) {
        this.right = right;
        return this;
    }

    public Projection setTop(float top) {
        this.top = top;
        return this;
    }

    public Projection setBottom(float bottom) {
        this.bottom = bottom;
        return this;
    }

    public Projection setRatio(float width, float height) {
        this.ratio = width / height;
        return this;
    }
    abstract public float[] getProjectionMatrix();
}
