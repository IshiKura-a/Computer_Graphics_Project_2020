package com.example.project_cg.shader;

public enum ShaderType {
    VERT("vert"), FRAG("frag");

    private final String type;
    ShaderType(String type) {this.type = type;}

    public static ShaderType get(String type) {
        for(ShaderType st :values()) {
            if(st.type.compareTo(type) == 0) {
                return st;
            }
        }
        throw new ClassCastException("No type = "+type+" in ShaderType");
    }
}
