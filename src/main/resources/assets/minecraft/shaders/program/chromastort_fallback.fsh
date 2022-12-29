#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

in vec2 texCoord;

uniform float dispFactor;
uniform float intensity;

out vec4 fragColor;

void main() {
	fragColor = mix(texture(DiffuseSampler, texCoord), vec4(0.0), dispFactor);
}
