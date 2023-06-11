#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform sampler2D Light0;
uniform sampler2D Light1;
uniform sampler2D Light2;
uniform sampler2D Light3;
uniform sampler2D Light4;
uniform sampler2D Light5;

uniform sampler2D Twinkle0;
uniform sampler2D Twinkle1;
uniform sampler2D Twinkle2;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

uniform mat4 RotMat;

uniform vec2 ScreenSize;

uniform int christmas;
uniform vec4 leftTint0;
uniform vec4 rightTint0;
uniform vec4 leftTint1;
uniform vec4 rightTint1;
uniform vec4 leftTint2;
uniform vec4 rightTint2;
uniform vec4 leftTint3;
uniform vec4 rightTint3;
uniform vec4 leftTint4;
uniform vec4 rightTint4;
uniform vec4 leftTint5;
uniform vec4 rightTint5;

uniform float gaze;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

in vec4 texProj0;
in vec4 glPos;

out vec4 fragColor;

const float PI = 3.14159265359;
const float HALF_PI = PI * 0.5;

vec4 permute(vec4 x){return mod(((x*34.0)+1.0)*x, 289.0);}
vec4 taylorInvSqrt(vec4 r){return 1.79284291400159 - 0.85373472095314 * r;}
vec3 fade(vec3 t) {return t*t*t*(t*(t*6.0-15.0)+10.0);}

float cnoise(vec3 P) {
	vec3 Pi0 = floor(P);
	vec3 Pi1 = Pi0 + vec3(1.0); // Integer part + 1
	Pi0 = mod(Pi0, 289.0);
	Pi1 = mod(Pi1, 289.0);
	vec3 Pf0 = fract(P); // Fractional part for interpolation
	vec3 Pf1 = Pf0 - vec3(1.0); // Fractional part - 1.0
	vec4 ix = vec4(Pi0.x, Pi1.x, Pi0.x, Pi1.x);
	vec4 iy = vec4(Pi0.yy, Pi1.yy);
	vec4 iz0 = Pi0.zzzz;
	vec4 iz1 = Pi1.zzzz;

	vec4 ixy = permute(permute(ix) + iy);
	vec4 ixy0 = permute(ixy + iz0);
	vec4 ixy1 = permute(ixy + iz1);

	vec4 gx0 = ixy0 / 7.0;
	vec4 gy0 = fract(floor(gx0) / 7.0) - 0.5;
	gx0 = fract(gx0);
	vec4 gz0 = vec4(0.5) - abs(gx0) - abs(gy0);
	vec4 sz0 = step(gz0, vec4(0.0));
	gx0 -= sz0 * (step(0.0, gx0) - 0.5);
	gy0 -= sz0 * (step(0.0, gy0) - 0.5);

	vec4 gx1 = ixy1 / 7.0;
	vec4 gy1 = fract(floor(gx1) / 7.0) - 0.5;
	gx1 = fract(gx1);
	vec4 gz1 = vec4(0.5) - abs(gx1) - abs(gy1);
	vec4 sz1 = step(gz1, vec4(0.0));
	gx1 -= sz1 * (step(0.0, gx1) - 0.5);
	gy1 -= sz1 * (step(0.0, gy1) - 0.5);

	vec3 g000 = vec3(gx0.x,gy0.x,gz0.x);
	vec3 g100 = vec3(gx0.y,gy0.y,gz0.y);
	vec3 g010 = vec3(gx0.z,gy0.z,gz0.z);
	vec3 g110 = vec3(gx0.w,gy0.w,gz0.w);
	vec3 g001 = vec3(gx1.x,gy1.x,gz1.x);
	vec3 g101 = vec3(gx1.y,gy1.y,gz1.y);
	vec3 g011 = vec3(gx1.z,gy1.z,gz1.z);
	vec3 g111 = vec3(gx1.w,gy1.w,gz1.w);

	vec4 norm0 = taylorInvSqrt(vec4(dot(g000, g000), dot(g010, g010), dot(g100, g100), dot(g110, g110)));
	g000 *= norm0.x;
	g010 *= norm0.y;
	g100 *= norm0.z;
	g110 *= norm0.w;
	vec4 norm1 = taylorInvSqrt(vec4(dot(g001, g001), dot(g011, g011), dot(g101, g101), dot(g111, g111)));
	g001 *= norm1.x;
	g011 *= norm1.y;
	g101 *= norm1.z;
	g111 *= norm1.w;

	float n000 = dot(g000, Pf0);
	float n100 = dot(g100, vec3(Pf1.x, Pf0.yz));
	float n010 = dot(g010, vec3(Pf0.x, Pf1.y, Pf0.z));
	float n110 = dot(g110, vec3(Pf1.xy, Pf0.z));
	float n001 = dot(g001, vec3(Pf0.xy, Pf1.z));
	float n101 = dot(g101, vec3(Pf1.x, Pf0.y, Pf1.z));
	float n011 = dot(g011, vec3(Pf0.x, Pf1.yz));
	float n111 = dot(g111, Pf1);

	vec3 fade_xyz = fade(Pf0);
	vec4 n_z = mix(vec4(n000, n100, n010, n110), vec4(n001, n101, n011, n111), fade_xyz.z);
	vec2 n_yz = mix(n_z.xy, n_z.zw, fade_xyz.y);
	float n_xyz = mix(n_yz.x, n_yz.y, fade_xyz.x); 
	return 2.2 * n_xyz;
}

void main() {

	float near = 0.05;
	float far = (ProjMat[2][2]-1.)/(ProjMat[2][2]+1.) * near;
	vec3 projected = normalize((inverse(ProjMat * RotMat) * vec4(glPos.xy / glPos.w * (far - near), far + near, far - near)).xyz);
	projected = vec3(-projected.x, -projected.y, -projected.z);
	
	float longitude = atan(projected.z, projected.x);
	float latitude = asin(projected.y);

	float u = (longitude + PI) / (2.0 * PI);
	float v = (latitude + HALF_PI) / PI;

	vec2 tex = vec2(u, v);

	float stretchedLatitude = pow(abs(latitude) / HALF_PI, 1.0 / 1.7) * sign(latitude) * HALF_PI;
	float q = ((stretchedLatitude) + HALF_PI) / PI;

	vec2 bloatedTex = vec2(u, q);

	vec4 color = texture(Sampler0, tex);

	if (christmas == 1) {
		vec4 additiveColor = texture(Light0, tex);
		vec4 tintColor = tex.x > 0.5 ? leftTint0 : rightTint0;
		color.rgb += (additiveColor.rgb * tintColor.rgb) * (tintColor.a) * (additiveColor.a);
		
		additiveColor = texture(Light1, tex);
		tintColor = tex.x > 0.5 ? leftTint1 : rightTint1;
		color.rgb += (additiveColor.rgb * tintColor.rgb) * (tintColor.a) * (additiveColor.a);
		
		additiveColor = texture(Light2, tex);
		tintColor = tex.x > 0.5 ? leftTint2 : rightTint2;
		color.rgb += (additiveColor.rgb * tintColor.rgb) * (tintColor.a) * (additiveColor.a);
		
		additiveColor = texture(Light3, tex);
		tintColor = tex.x > 0.5 ? leftTint3 : rightTint3;
		color.rgb += (additiveColor.rgb * tintColor.rgb) * (tintColor.a) * (additiveColor.a);
		
		additiveColor = texture(Light4, tex);
		tintColor = tex.x > 0.5 ? leftTint4 : rightTint4;
		color.rgb += (additiveColor.rgb * tintColor.rgb) * (tintColor.a) * (additiveColor.a);
		
		additiveColor = texture(Light5, tex);
		tintColor = tex.x > 0.5 ? leftTint5 : rightTint5;
		color.rgb += (additiveColor.rgb * tintColor.rgb) * (tintColor.a) * (additiveColor.a);
	}
	
	vec2 ntex = vec2(projected.x + GameTime * 80, projected.z) * 2;
	vec2 ntex2 = vec2(projected.x, projected.z + GameTime * 100) * 2;
	float twinkeOpacity0 = clamp(cnoise(vec3(ntex, GameTime * 200)) - (cnoise(vec3(ntex2, GameTime * 500) / 4)), 0.0, 1.0) * gaze;
	
	ntex = vec2(projected.x, projected.z + GameTime * 100) * 2;
	ntex2 = vec2(projected.x + GameTime * 80, projected.z) * 2;
	float twinkeOpacity1 = clamp(cnoise(vec3(ntex, (GameTime + 40) * 150)) - (cnoise(vec3(ntex2, (GameTime + 17) * 450) / 4)), 0.0, 1.0) * gaze;
	
	ntex = vec2(projected.x + GameTime * 90, projected.z) * 2;
	ntex2 = vec2(projected.x, projected.z + GameTime * 150) * 2;
	float twinkeOpacity2 = clamp(cnoise(vec3(ntex, (GameTime + 27) * 300)) - (cnoise(vec3(ntex2, GameTime * 400) / 4)), 0.0, 1.0) * gaze;
	
	vec4 twinkleColor = texture(Twinkle0, bloatedTex);
	vec4 twinkleTint = vec4(0.78, 0.698, 0.6, twinkeOpacity0);
	color.rgb += (twinkleColor.rgb * twinkleTint.rgb) * (twinkleTint.a) * (twinkleColor.a);

	twinkleColor = texture(Twinkle1, bloatedTex);
	twinkleTint = vec4(0.874, 0.8, 0.651, twinkeOpacity1);
	color.rgb += (twinkleColor.rgb * twinkleTint.rgb) * (twinkleTint.a) * (twinkleColor.a);
	
	twinkleColor = texture(Twinkle2, bloatedTex);
	twinkleTint = vec4(0.788, 0.698, 0.494, twinkeOpacity2);
	color.rgb += (twinkleColor.rgb * twinkleTint.rgb) * (twinkleTint.a) * (twinkleColor.a);

	fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
