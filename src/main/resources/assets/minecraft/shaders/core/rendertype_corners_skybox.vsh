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

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform int FogShape;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;
uniform mat3 IViewRotMat;

uniform vec4 FullUV;
uniform float renderAsEntity;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out vec4 normal;

out vec4 lightMapColor;
out vec4 texProj0;
out vec4 glPos;

vec2 getNormalUV(int i)
{
	if (i == 0) {
		return vec2(0.0, 0.0);
	} else if (i == 1) {
		return vec2(0.0, 1.0);
	} else if (i == 2) {
		return vec2(1.0, 1.0);
	} else {
		return vec2(1.0, 0.0);
	}
}

void main() {
	if (renderAsEntity == 1.0) {
		gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
		vertexDistance = fog_distance(ModelViewMat, IViewRotMat * Position, FogShape);
		vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color) * minecraft_sample_lightmap(Sampler2, UV2);
	} else {
		vec3 pos = Position + ChunkOffset;
		gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
		vertexDistance = fog_distance(ModelViewMat, pos, FogShape);
		vertexColor = Color * minecraft_sample_lightmap(Sampler2, UV2);
	}

	glPos = gl_Position;
	texProj0 = projection_from_position(gl_Position);

	texCoord0 = getNormalUV(int(mod(float(gl_VertexID), 4.0)));
	normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
