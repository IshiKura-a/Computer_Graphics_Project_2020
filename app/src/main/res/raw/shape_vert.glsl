#version 320 es

layout (location = 0) in vec4 iVertexPosition;
in vec4 iNormal;
in vec4 iColor;

out vec4 fragPosition;
out vec4 fragNormal;
out vec4 fragColor;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

void main() {
    fragPosition = uModel * iVertexPosition;
    fragNormal = transpose(inverse(uModel)) * iNormal;
    gl_Position = uProjection * uView * fragPosition;
    fragColor = iColor;
}
