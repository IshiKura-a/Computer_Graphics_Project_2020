#version 320 es
in vec4 Position;
in vec4 Normal;
in vec2 TexCoord;
// The texture containing the results of the first pass
uniform sampler2D RenderTex;
uniform float EdgeThreshold; // The squared threshold value
uniform int Width; // The pixel width
uniform int Height; // The pixel height
// This subroutine is used for selecting the functionality
// of pass1 and pass2.
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
subroutine vec4 RenderPassType();
subroutine uniform RenderPassType RenderPass;
vec3 phongModel( vec3 pos, vec3 norm )
{
	vec4 vDiffuse = uLightDiffuse*uMaterialDiffuse*max(0.0, dot(normal, light));
	vec4 vSpecular = uLightSpecular*uMaterialSpecular*max(0.0, pow(dot(normal, halfV), uShininess));
	vec4 vAmbient = uLightAmbient*uMaterialAmbient;
	return (uUseTexture==1?texture(uTexture, fragTextureCoord):uColor) * (vSpecular + vDiffuse + vAmbient);
// The code for the basic ADS shading model goes here…
}
// Approximates the brightness of a RGB value.
float luma( vec3 color ) {
return 0.2126 * color.r + 0.7152 * color.g +
0.0722 * color.b;
}
// Pass #1
subroutine (RenderPassType)
vec4 pass1()
{
return vec4(phongModel( Position, Normal );
}
// Pass #2
subroutine( RenderPassType )
vec4 pass2()
{
float dx = 1.0 / float(Width);
float dy = 1.0 / float(Height);
float s00 = luma(texture( RenderTex,
TexCoord + vec2(-dx,dy) ).rgb);
float s10 = luma(texture( RenderTex,
TexCoord + vec2(-dx,0.0) ).rgb);
float s20 = luma(texture( RenderTex,
TexCoord + vec2(-dx,-dy) ).rgb);
float s01 = luma(texture( RenderTex,
TexCoord + vec2(0.0,dy) ).rgb);
float s21 = luma(texture( RenderTex,
TexCoord + vec2(0.0,-dy) ).rgb);
float s02 = luma(texture( RenderTex,
TexCoord + vec2(dx, dy) ).rgb);
float s12 = luma(texture( RenderTex,
TexCoord + vec2(dx, 0.0) ).rgb);
float s22 = luma(texture( RenderTex,
TexCoord + vec2(dx, -dy) ).rgb);
	float sx = s00 + 2 * s10 + s20 - (s02 + 2 * s12 + s22);
	float sy = s00 + 2 * s01 + s02 - (s20 + 2 * s21 + s22);
	float dist = sx * sx + sy * sy;
	if( dist>EdgeThreshold )
		return vec4(1.0);
	else
		return vec4(0.0,0.0,0.0,1.0);
}
out vec4 FragColor;
void main()
{
	// This will call either pass1() or pass2()
	FragColor = RenderPass();
}