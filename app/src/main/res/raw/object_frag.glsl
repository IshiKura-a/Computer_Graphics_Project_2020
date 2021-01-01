#version 320 es

precision mediump float;

in vec4 fragPosition;
in vec4 fragNormal;
in vec2 fragTextureCoord;

out vec4 oColor;

uniform int uUseTexture;
uniform sampler2D uTexture;

uniform vec4 uColor;
uniform float uShininess;
uniform vec4 uLightSpecular;
uniform vec4 uLightDiffuse;
uniform vec4 uLightAmbient;
uniform vec4 uMaterialSpecular;
uniform vec4 uMaterialDiffuse;
uniform vec4 uMaterialAmbient;
uniform vec4 uLightPosition;
uniform vec4 uCameraPosition;

void main() {
    vec4 vAmbient = uLightAmbient*uMaterialAmbient;

    vec3 normal = normalize(fragNormal.xyz/fragNormal.w);
    vec3 eye = normalize(uCameraPosition.xyz/uCameraPosition.w-fragPosition.xyz/fragPosition.w);
    vec3 light = normalize(uLightPosition.xyz/uLightPosition.w-fragPosition.xyz/fragPosition.w);

    vec4 vDiffuse = uLightDiffuse*uMaterialDiffuse*max(0.0, dot(normal, light));
    vec3 halfV = normalize(light+eye);
    vec4 vSpecular = uLightSpecular*uMaterialSpecular*max(0.0, pow(dot(normal, halfV), uShininess));

    oColor = (uUseTexture==1?texture(uTexture, fragTextureCoord):uColor) * (vSpecular + vDiffuse + vAmbient);
}