package com.example.project_cg.shape;

public enum ShapeType {
    CUBE("Cube"),
    MODEL("Model"),
    BALL("Ball"),
    CIRCLE("Circle"),
    CONE("Cone"),
    CYLINDER("Cylinder"),
    PRISM("Prism"),
    PYRAMID("Pyramid");

    private String name;
    ShapeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ShapeType getShapeType(String name) {
        for(ShapeType st: values()) {
            if(st.getName().compareTo(name) == 0) {
                return st;
            }
        }
        return null;
    }
}
