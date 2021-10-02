#version 150

#moj_import <matrix.glsl>
#moj_import <projection.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;
uniform sampler2D Sampler4;
uniform sampler2D Sampler5;

in vec4 texProj0;
in vec4 glPos;

uniform mat4 ProjMat;
uniform mat4 TransformMatrix;

out vec4 fragColor;

vec2 sampleCube(vec3 v, out float faceIndex) {
	vec3 vAbs = abs(v);
	float ma;	
	vec2 uv;
	if(vAbs.z >= vAbs.x && vAbs.z >= vAbs.y) {
		faceIndex = v.z < 0.0 ? 3.0 : 1.0;
		ma = 0.5 / vAbs.z;
		uv = vec2(v.z < 0.0 ? -v.x : v.x, -v.y);
	} else if(vAbs.y >= vAbs.x) {
		faceIndex = v.y < 0.0 ? 5.0 : 0.0;
		ma = 0.5 / vAbs.y;
		uv = vec2(v.x, v.y < 0.0 ? -v.z : v.z);
	} else {
		faceIndex = v.x < 0.0 ? 4.0 : 2.0;
		ma = 0.5 / vAbs.x;
		uv = vec2(v.x < 0.0 ? v.z : -v.z, -v.y);
	}
	return uv * ma + 0.5;
}

void main() {
	vec4 texPos = vec4(texProj0.x, -texProj0.y, texProj0.z, texProj0.w);

	float near = 0.05;
	float far = (ProjMat[2][2]-1.)/(ProjMat[2][2]+1.) * near;
	vec3 rd = normalize((inverse(ProjMat * TransformMatrix) * vec4(glPos.xy / glPos.w * (far - near), far + near, far - near)).xyz);
	float faceIndex = 0.0;
	texPos = vec4(sampleCube(rd, faceIndex), 1.0, 1.0);

	vec3 color = textureProj(Sampler0, texPos).xyz;

	if (faceIndex == 0.0) {
		color = textureProj(Sampler0, texPos).xyz;
	} else if (faceIndex == 1.0) {
		color = textureProj(Sampler1, texPos).xyz;
	} else if (faceIndex == 2.0) {
		color = textureProj(Sampler2, texPos).xyz;
	} else if (faceIndex == 3.0) {
		color = textureProj(Sampler3, texPos).xyz;
	} else if (faceIndex == 4.0) {
		color = textureProj(Sampler4, texPos).xyz;
	} else if (faceIndex == 5.0) {
		color = textureProj(Sampler5, texPos).xyz;
	}

    fragColor = vec4(color, 1.0);
}
