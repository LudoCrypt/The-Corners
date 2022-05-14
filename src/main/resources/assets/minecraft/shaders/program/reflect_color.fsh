#version 150

uniform sampler2D DiffuseSampler;

uniform vec3 ReflectColor;

in vec2 texCoord;

out vec4 fragColor;

void main() {
	vec4 target = texture(DiffuseSampler, texCoord);
	vec4 monoChrome = vec4(vec3(0.21 * target.x + 0.71 * target.y + 0.07 * target.z), 1.0);
    fragColor = (monoChrome * monoChrome) / (vec4(1) - monoChrome * vec4(ReflectColor.xyz, 1.0));
}
