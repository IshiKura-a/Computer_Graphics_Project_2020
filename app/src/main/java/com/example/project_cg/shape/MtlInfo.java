package com.example.project_cg.shape;

public class MtlInfo {
    public float[] kAmbient;
    public float[] kDiffuse;
    public float[] kSpecular;
    public float shininess;

    public MtlInfo(float[] ambient, float[] diffuse, float[] specular, float shininess) {
        kAmbient = ambient.clone();
        kDiffuse = diffuse.clone();
        kSpecular = specular.clone();
        this.shininess = shininess;
    }


}
