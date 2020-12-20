uniform mat4 uMVPMatrix;
uniform vec4 uLightPosition;
uniform vec4 uCameraPosition;

uniform float uShininess;
uniform vec4 uLightSpecular;
uniform vec4 uLightDiffuse;
uniform vec4 uLightAmbient;
uniform vec4 uMaterialSpecular;
uniform vec4 uMaterialDiffuse;
uniform vec4 uMaterialAmbient;

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

    vAmbient = uLightAmbient*uMaterialAmbient;
    vec3 normal = normalize(aNormal.xyz/aNormal.w);
    vec3 eye = normalize(uCameraPosition.xyz/uCameraPosition.w-aVertexPosition.xyz/aVertexPosition.w);
    vec3 light = normalize(uLightPosition.xyz/uLightPosition.w-aVertexPosition.xyz/aVertexPosition.w);

    vDiffuse = uLightDiffuse*uMaterialDiffuse*max(0.0, dot(normal, light));
    vec3 halfV = normalize(light+eye);
    vSpecular = uLightSpecular*uMaterialSpecular*max(0.0, pow(dot(normal, halfV), uShininess));
}
