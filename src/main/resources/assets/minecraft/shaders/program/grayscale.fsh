#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
	vec4 diffuseColor = texture(DiffuseSampler, texCoord);
	float grey = 0.21 * diffuseColor.x + 0.71 * diffuseColor.y + 0.07 * diffuseColor.z;
	fragColor = vec4(grey, grey, grey, diffuseColor.w);
}
