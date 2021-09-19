#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
	vec4 diffuseColor = texture(DiffuseSampler, texCoord);
	float rgb = (diffuseColor.x) + (diffuseColor.y) + (diffuseColor.z / 255);
	fragColor = vec4(rgb, rgb, rgb, diffuseColor.w);
}
