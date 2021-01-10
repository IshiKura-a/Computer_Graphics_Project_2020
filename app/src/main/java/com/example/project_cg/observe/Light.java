package com.example.project_cg.observe;

public class Light {
    private float[] location;
    private float[] ambient, diffuse, specular;

    public Light(float[] location, float[] ambient, float[] diffuse, float[] specular) {
        setLocation(location).setAmbient(ambient).setDiffuse(diffuse).setSpecular(specular);
    }

    public Light(){}

    public Light setAmbient(float[] ambient) {
        this.ambient = ambient.clone();
        return this;
    }

    public Light setDiffuse(float[] diffuse) {
        this.diffuse = diffuse.clone();
        return this;
    }

    public Light setSpecular(float[] specular) {
        this.specular = specular.clone();
        return this;
    }

    public Light setLocation(float[] location) {
        this.location = location.clone();
        return this;
    }

    public float[] getAmbient() {
        return ambient;
    }

    public float[] getDiffuse() {
        return diffuse;
    }

    public float[] getLocation() {
        return location;
    }

    public float[] getSpecular() {
        return specular;
    }
}
