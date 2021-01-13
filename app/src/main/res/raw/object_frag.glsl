#version 320 es

precision mediump float;

in vec4 fragPosition;
in vec4 fragNormal;
in vec2 fragTextureCoord;
out vec4 oColor;
uniform float ischosen;
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

uniform vec4 uLightSpecular2;
uniform vec4 uLightDiffuse2;
uniform vec4 uLightAmbient2;
uniform vec4 uLightPosition2;

uniform vec4 uLightSpecular3;
uniform vec4 uLightDiffuse3;
uniform vec4 uLightAmbient3;
uniform vec4 uLightPosition3;

uniform vec4 uLightSpecular4;
uniform vec4 uLightDiffuse4;
uniform vec4 uLightAmbient4;
uniform vec4 uLightPosition4;

uniform vec4 uLightSpecular5;
uniform vec4 uLightDiffuse5;
uniform vec4 uLightAmbient5;
uniform vec4 uLightPosition5;

uniform vec4 uLightSpecular6;
uniform vec4 uLightDiffuse6;
uniform vec4 uLightAmbient6;
uniform vec4 uLightPosition6;

uniform vec4 uLightSpecular7;
uniform vec4 uLightDiffuse7;
uniform vec4 uLightAmbient7;
uniform vec4 uLightPosition7;


uniform vec4 uLightSpecular8;
uniform vec4 uLightDiffuse8;
uniform vec4 uLightAmbient8;
uniform vec4 uLightPosition8;

uniform vec4 uLightSpecular9;
uniform vec4 uLightDiffuse9;
uniform vec4 uLightAmbient9;
uniform vec4 uLightPosition9;

uniform vec4 uLightSpecular10;
uniform vec4 uLightDiffuse10;
uniform vec4 uLightAmbient10;
uniform vec4 uLightPosition10;
void main() {

    vec3 normal = normalize(fragNormal.xyz/fragNormal.w);
    vec3 eye = normalize(uCameraPosition.xyz/uCameraPosition.w-fragPosition.xyz/fragPosition.w);
    //1
    vec3 light = normalize(uLightPosition.xyz/uLightPosition.w-fragPosition.xyz/fragPosition.w);
    vec3 halfV = normalize(light+eye);
    vec4 vSpecular = uLightSpecular*uMaterialSpecular*max(0.0, pow(dot(normal, halfV), uShininess));
    vec4 vAmbient = uLightAmbient*uMaterialAmbient;
    vec4 vDiffuse = uLightDiffuse*uMaterialDiffuse*max(0.0, dot(normal, light));
    //2
    vec3 light2 = normalize(uLightPosition2.xyz/uLightPosition2.w-fragPosition.xyz/fragPosition.w);
    vec3 halfV2 = normalize(light2+eye);
    vec4 vSpecular2 = uLightSpecular2*uMaterialSpecular*max(0.0, pow(dot(normal, halfV2), uShininess));
    vec4 vAmbient2 = uLightAmbient2*uMaterialAmbient;
    vec4 vDiffuse2 = uLightDiffuse2*uMaterialDiffuse*max(0.0, dot(normal, light2));
    //3
    vec3 light3 = normalize(uLightPosition3.xyz/uLightPosition3.w-fragPosition.xyz/fragPosition.w);
    vec3 halfV3 = normalize(light3+eye);
    vec4 vSpecular3 = uLightSpecular3*uMaterialSpecular*max(0.0, pow(dot(normal, halfV3), uShininess));
    vec4 vAmbient3 = uLightAmbient3*uMaterialAmbient;
    vec4 vDiffuse3 = uLightDiffuse3*uMaterialDiffuse*max(0.0, dot(normal, light3));
    //4
    vec3 light4 = normalize(uLightPosition4.xyz/uLightPosition4.w-fragPosition.xyz/fragPosition.w);
    vec3 halfV4 = normalize(light4+eye);
    vec4 vSpecular4 = uLightSpecular4*uMaterialSpecular*max(0.0, pow(dot(normal, halfV4), uShininess));
    vec4 vAmbient4 = uLightAmbient4*uMaterialAmbient;
    vec4 vDiffuse4 = uLightDiffuse4*uMaterialDiffuse*max(0.0, dot(normal, light4));
    //5
    vec3 light5 = normalize(uLightPosition5.xyz/uLightPosition5.w-fragPosition.xyz/fragPosition.w);
    vec3 halfV5 = normalize(light5+eye);
    vec4 vSpecular5 = uLightSpecular5*uMaterialSpecular*max(0.0, pow(dot(normal, halfV5), uShininess));
    vec4 vAmbient5 = uLightAmbient5*uMaterialAmbient;
    vec4 vDiffuse5 = uLightDiffuse5*uMaterialDiffuse*max(0.0, dot(normal, light5));
    //6
    vec3 light6 = normalize(uLightPosition6.xyz/uLightPosition6.w-fragPosition.xyz/fragPosition.w);
    vec3 halfV6 = normalize(light6+eye);
    vec4 vSpecular6 = uLightSpecular6*uMaterialSpecular*max(0.0, pow(dot(normal, halfV6), uShininess));
    vec4 vAmbient6 = uLightAmbient6*uMaterialAmbient;
    vec4 vDiffuse6 = uLightDiffuse6*uMaterialDiffuse*max(0.0, dot(normal, light6));
    //7
    vec3 light7 = normalize(uLightPosition7.xyz/uLightPosition7.w-fragPosition.xyz/fragPosition.w);
    vec3 halfV7 = normalize(light7+eye);
    vec4 vSpecular7 = uLightSpecular7*uMaterialSpecular*max(0.0, pow(dot(normal, halfV7), uShininess));
    vec4 vAmbient7 = uLightAmbient7*uMaterialAmbient;
    vec4 vDiffuse7 = uLightDiffuse7*uMaterialDiffuse*max(0.0, dot(normal, light7));
    //8
    vec3 light8 = normalize(uLightPosition8.xyz/uLightPosition8.w-fragPosition.xyz/fragPosition.w);
    vec3 halfV8 = normalize(light8+eye);
    vec4 vSpecular8 = uLightSpecular8*uMaterialSpecular*max(0.0, pow(dot(normal, halfV8), uShininess));
    vec4 vAmbient8 = uLightAmbient8*uMaterialAmbient;
    vec4 vDiffuse8 = uLightDiffuse8*uMaterialDiffuse*max(0.0, dot(normal, light8));
    //9
    vec3 light9 = normalize(uLightPosition9.xyz/uLightPosition9.w-fragPosition.xyz/fragPosition.w);
    vec3 halfV9 = normalize(light9+eye);
    vec4 vSpecular9 = uLightSpecular9*uMaterialSpecular*max(0.0, pow(dot(normal, halfV9), uShininess));
    vec4 vAmbient9 = uLightAmbient9*uMaterialAmbient;
    vec4 vDiffuse9 = uLightDiffuse9*uMaterialDiffuse*max(0.0, dot(normal, light9));
    //10
    vec3 light10 = normalize(uLightPosition10.xyz/uLightPosition10.w-fragPosition.xyz/fragPosition.w);
    vec3 halfV10 = normalize(light10+eye);
    vec4 vSpecular10 = uLightSpecular10*uMaterialSpecular*max(0.0, pow(dot(normal, halfV10), uShininess));
    vec4 vAmbient10 = uLightAmbient10*uMaterialAmbient;
    vec4 vDiffuse10 = uLightDiffuse10*uMaterialDiffuse*max(0.0, dot(normal, light10));

    if(ischosen==1.0f){
        oColor = 1.0-(uUseTexture==1?texture(uTexture, fragTextureCoord):uColor) * (vSpecular + vDiffuse + vAmbient +vSpecular2+vDiffuse2+vAmbient2+vSpecular3+vDiffuse3+vAmbient3+vSpecular4+vDiffuse4+vAmbient4+vSpecular5+vDiffuse5+vAmbient5+vSpecular6+vDiffuse6+vAmbient6+vSpecular7+vDiffuse7+vAmbient7+vSpecular8+vDiffuse8+vAmbient8+vSpecular9+vDiffuse9+vAmbient9+vSpecular10+vDiffuse10+vAmbient10);
    }
    else{
        oColor = (uUseTexture==1?texture(uTexture, fragTextureCoord):uColor) * (vSpecular + vDiffuse + vAmbient +vSpecular2+vDiffuse2+vAmbient2+vSpecular3+vDiffuse3+vAmbient3+vSpecular4+vDiffuse4+vAmbient4+vSpecular5+vDiffuse5+vAmbient5+vSpecular6+vDiffuse6+vAmbient6+vSpecular7+vDiffuse7+vAmbient7+vSpecular8+vDiffuse8+vAmbient8+vSpecular9+vDiffuse9+vAmbient9+vSpecular10+vDiffuse10+vAmbient10);
    }
}