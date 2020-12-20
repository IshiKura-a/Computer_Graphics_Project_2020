precision mediump float;

varying vec4 vColor;
varying vec4 vSpecular;
varying vec4 vDiffuse;
varying vec4 vAmbient;

void main() {
    gl_FragColor = vColor*vSpecular + vColor*vDiffuse + vColor*vAmbient;
}