uniform mat4 uMVPMatrix;
uniform vec4 uLightPosition;
uniform vec4 uCameraPosition;

uniform float uShininess;
uniform vec4 uSpecular;
uniform vec4 uDiffuse;
uniform vec4 uAmbient;

attribute vec4 aColor;
attribute vec4 aNormal;
attribute vec4 aVertexPosition;

varying vec4 vColor;
varying vec4 vSpecular;
varying vec4 vDiffuse;
varying vec4 vAmbient;

void main() {
    gl_Position = uMVPMatrix*aVertexPosition;
    vColor = aColor;

    vAmbient = uAmbient;
    vec3 normal = normalize(aNormal.xyz/aNormal.w);
    vec3 eye = normalize(uCameraPosition.xyz/uCameraPosition.w-aVertexPosition.xyz/aVertexPosition.w);
    vec3 light = normalize(uLightPosition.xyz/uLightPosition.w-aVertexPosition.xyz/aVertexPosition.w);

    vDiffuse = uDiffuse*max(0.0, dot(normal, light));
    vec3 halfV = normalize(light+eye);
    vSpecular = uSpecular*max(0.0, pow(dot(normal, halfV), uShininess));
}
