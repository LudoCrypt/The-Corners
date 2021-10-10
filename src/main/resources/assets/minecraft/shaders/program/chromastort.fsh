#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

in vec2 texCoord;

uniform float dispFactor;
uniform float intensity;

out vec4 fragColor;

void main() {
	fragColor = mix(texture(DiffuseSampler, vec2(texCoord.x, texCoord.y + dispFactor * (texture(PrevSampler, texCoord).r * intensity))), texture(PrevSampler, vec2(texCoord.x, texCoord.y + (1.0 - dispFactor) * (texture(DiffuseSampler, texCoord).r * intensity))), dispFactor);
}
