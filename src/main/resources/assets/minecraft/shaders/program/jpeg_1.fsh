#version 150
#define lofi(i,j) floor((i)/(j)+.5)*(j)
#define PI 3.14159265

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

uniform vec2 OutSize;

// 1.0 - 256.0; 8.0
uniform float blockSize;

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

vec4 render(vec2 coordUV) {
	vec2 bv = vec2(1.0, 0.0);
	vec2 block = bv * float(blockSize - 1) + vec2(1.0);
	vec2 blockOrigin = 0.5 + floor(coordUV.xy / block) * block;
	int bs = int(min(float(blockSize), dot(bv, OutSize - blockOrigin + 0.5)));

	float delta = mod(dot(bv, coordUV.xy), float(blockSize));

	vec4 sum = vec4(0.0);
	for (int i = 0; i < 1024; i++) {
		if (bs <= i) {
			break;
		}

		float fdelta = float(i);

		vec4 val = texture2D(DiffuseSampler, (blockOrigin + bv * fdelta) / OutSize);
		val.xyz = rgb2yuv(val.xyz);

		float wave = cos(delta * fdelta / float(bs) * PI);
		sum += wave * val;
	}

	return sum;
}

void main() {
	vec2 bv = vec2(0.0, 1.0);
	vec2 block = bv * float(blockSize - 1) + vec2(1.0);
	vec2 blockOrigin = 0.5 + floor(gl_FragCoord.xy / block) * block;
	int bs = int(min(float(blockSize), dot(bv, OutSize - blockOrigin + 0.5)));

	float delta = mod(dot(bv, gl_FragCoord.xy), float(blockSize));

	vec4 sum = vec4(0.0);
	for (int i = 0; i < 1024; i++) {
		if (bs <= i) {
			break;
		}

		float fdelta = float(i);

		vec4 val = render((blockOrigin + bv * fdelta));

		float wave = cos(delta * fdelta / float(bs) * PI);
		sum += wave * val;
	}

	sum.xyz = yuv2rgb(sum.xyz);

	fragColor = sum;
}
