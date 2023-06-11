#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

in vec2 texCoord;

uniform float dispFactor;
uniform float intensity;

out vec4 fragColor;

void main() {
	vec4 prevSampler = texture(PrevSampler, texCoord) / (1.0 - dispFactor);
	vec4 prevSampler2 = texture(PrevSampler, vec2(texCoord.x, texCoord.y + (1.0 - dispFactor) * (texture(DiffuseSampler, texCoord).r * intensity))) / (1.0 - dispFactor);
	vec4 col = mix(texture(DiffuseSampler, vec2(texCoord.x, texCoord.y + dispFactor * (prevSampler.r * intensity))), prevSampler2, dispFactor);
	fragColor = mix(col, vec4(0.0), dispFactor);
}
