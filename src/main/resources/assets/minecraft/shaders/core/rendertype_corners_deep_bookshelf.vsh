#version 150

#moj_import <projection.glsl>
#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;
in vec4 State;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 BobMat;

uniform vec3 ChunkOffset;
uniform int FogShape;

uniform vec3 cameraPos;

out vec4 vertexColor;
out vec3 vertexPos;
out vec2 texCoord0;
out float vertexDistance;

out vec4 glPos;

out mat4 BoblessMat;

out vec3 normal;

out vec3 topRow;
out vec3 bottomRow;

void main() {
	vec3 pos = Position + ChunkOffset;
	vertexDistance = fog_distance(ModelViewMat, pos, FogShape);
	vertexColor = Color * minecraft_sample_lightmap(Sampler2, UV2);
	vertexPos = pos;

	BoblessMat = inverse(((BobMat * inverse(BobMat) * ProjMat) * inverse(BobMat)) * ModelViewMat) * (BobMat * inverse(BobMat) * ProjMat * ModelViewMat);

	gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
	glPos = gl_Position;

	texCoord0 = UV0;
	normal = vec3(Normal);
	
	int top = int(State.x);
	int bottom = int(State.y);
	topRow = vec3((top & 1), (top & 2) / 2, (top & 4) / 4);
	bottomRow = vec3((bottom & 1), (bottom & 2) / 2, (bottom & 4) / 4);
}
