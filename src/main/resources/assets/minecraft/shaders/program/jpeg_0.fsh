#version 150
#define lofi(i,j) floor((i)/(j)+.5)*(j)
#define PI 3.14159265

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

uniform vec2 OutSize;

// 1.0 - 256.0; 8.0
uniform float blockSize;

// ? - 4.0; 0.0
uniform float highFreqMultiplier;

// ? - 0.2; 0.0
uniform float quantizeY;

// ? - 0.2; 0.0
uniform float quantizeYf;

// ? - 0.2; 0.0
uniform float quantizeC;

// ? - 0.2; 0.0
uniform float quantizeCf;

// ? - 0.2; 0.0
uniform float quantizeA;

// ? - 0.2; 0.0
uniform float quantizeAf;

out vec4 fragColor;

vec3 rgb2yuv(vec3 rgb) {
	return vec3(
		0.299 * rgb.x + 0.587 * rgb.y + 0.114 * rgb.z,
		-0.148736 * rgb.x - 0.331264 * rgb.y + 0.5 * rgb.z,
		0.5 * rgb.x - 0.418688 * rgb.y - 0.081312 * rgb.z
	);
}

vec3 yuv2rgb(vec3 yuv) {
	return vec3(
		yuv.x + 1.402 * yuv.z,
		yuv.x - 0.344136 * yuv.y - 0.714136 * yuv.z,
		yuv.x + 1.772 * yuv.y
	);
}

vec4 cosine(vec2 coordUV) {
	vec2 bv = vec2(1.0, 0.0);
	vec2 block = bv * float(blockSize - 1) + vec2(1.0);
	vec2 blockOrigin = 0.5 + floor(coordUV.xy / block) * block;
	int bs = int(min(float(blockSize), dot(bv, OutSize - blockOrigin + 0.5)));

	float freq = floor(mod(dot(bv, coordUV.xy), float(blockSize))) / float(bs) * PI;
	float factor = (freq == 0.0 ? 1.0 : 2.0) / float(bs);

	vec4 sum = vec4(0.0);
	for (int i = 0; i < 1024; i++) {
		if (bs <= i) {
			break;
		}

		vec2 delta = float(i) * bv;
		float wave = cos((float(i) + 0.5) * freq);

		vec4 val = texture2D(DiffuseSampler, (blockOrigin + delta) / OutSize);
		val.xyz = rgb2yuv(val.xyz);

		sum += wave * factor * val;
	}

	return sum;
}

void main() {
	vec2 bv = vec2(0.0, 1.0);
	vec2 block = bv * float(blockSize - 1) + vec2(1.0);
	vec2 blockOrigin = 0.5 + floor(gl_FragCoord.xy / block) * block;
	int bs = int(min(float(blockSize), dot(bv, OutSize - blockOrigin + 0.5)));

	float freq = floor(mod(dot(bv, gl_FragCoord.xy), float(blockSize))) / float(bs) * PI;
	float factor = (freq == 0.0 ? 1.0 : 2.0) / float(bs);

	vec4 sum = vec4(0.0);
	for (int i = 0; i < 1024; i++) {
		if (bs <= i) {
			break;
		}

		vec2 delta = float(i) * bv;
		float wave = cos((float(i) + 0.5) * freq);

		vec4 val = cosine((blockOrigin + delta));

		sum += wave * factor * val;
	}

	float len = length(floor(mod(gl_FragCoord.xy, float(blockSize))));

	float qY = quantizeY + quantizeYf * len;
	sum.x = 0.0 < qY ? lofi(sum.x, qY) : sum.x;

	float qC = quantizeC + quantizeCf * len;
	sum.yz = 0.0 < qC ? lofi(sum.yz, qC) : sum.yz;

	float qA = quantizeA + quantizeAf * len;
	sum.w = 0.0 < qA ? lofi(sum.w, qA) : sum.w;

	sum *= 1.0 + len * highFreqMultiplier;

	sum.xyz = yuv2rgb(sum.xyz);

	fragColor = sum;
}
