#version 320 es

in vec4 iVertexPosition;
in vec4 iNormal;
in vec4 iColor;
in vec2 iTextureCoord;

out vec4 fragPosition;
out vec4 fragNormal;
out vec4 fragColor;
out vec2 fragTextureCoord;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;
uniform mat4 uAffine;

void main() {
    fragPosition = uModel * iVertexPosition;
    fragNormal = transpose(inverse(uModel)) * iNormal;
    gl_Position = uAffine * uProjection * uView * fragPosition;
    fragColor = iColor;
    fragTextureCoord = iTextureCoord;
}