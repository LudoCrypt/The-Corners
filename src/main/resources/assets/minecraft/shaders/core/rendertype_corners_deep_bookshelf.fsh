#version 400

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform int FogShape;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;
uniform mat4 BobMat;
uniform mat4 BasicMat;

uniform vec3 cameraPos;

uniform vec2 ScreenSize;

uniform float GameTime;

uniform vec3 ChunkOffset;

in vec4 vertexColor;
in vec3 vertexPos;
in vec2 texCoord0;
in float vertexDistance;

in vec4 glPos;

in mat4 BoblessMat;

in vec3 normal;

out vec4 fragColor;

out float closestDistance;

struct Vertex {
	vec3 pos;
	vec4 color;
	vec2 uv;
	float dist;
	bool isTop;
};

Vertex vertice(vec3 pos, vec4 color, vec2 uv, bool isTop) {
	Vertex v;
	v.pos = pos;
	v.color = color;
	v.uv = uv;
	v.dist = fog_distance(ModelViewMat, pos - cameraPos, FogShape);
	v.isTop = isTop;
	return v;
}

float cross2(vec2 a, vec2 b) {
	return a.x * b.y - a.y * b.x;
}

vec3 barycentric(vec4 v1, vec4 v2, vec4 v3) {
	vec4 ndcv1 = vec4(v1.xyz / v1.w, 1.0 / v1.w);
	vec2 sv1 = mix(vec2(0.0), ScreenSize, 0.5 * (ndcv1.xy + 1.0));

	vec4 ndcv2 = vec4(v2.xyz / v2.w, 1.0 / v2.w);
	vec2 sv2 = mix(vec2(0.0), ScreenSize, 0.5 * (ndcv2.xy + 1.0));

	vec4 ndcv3 = vec4(v3.xyz / v3.w, 1.0 / v3.w);
	vec2 sv3 = mix(vec2(0.0), ScreenSize, 0.5 * (ndcv3.xy + 1.0));

	float denom = cross2(sv2 - sv1, sv3 - sv1);

	if (v1.w < 0.0 && v2.w < 0.0 && v3.w < 0.0) {
		return vec3(-1.0);
	}

	vec3 bary = vec3(cross2(sv2 - gl_FragCoord.xy, sv3 - gl_FragCoord.xy), cross2(gl_FragCoord.xy - sv1, sv3 - sv1), cross2(sv2 - sv1, gl_FragCoord.xy - sv1)) / denom;

	vec3 persp = 1.0 / ((bary.x * ndcv1.w) + (bary.y * ndcv2.w) + (bary.z * ndcv3.w)) * bary * vec3(ndcv1.w, ndcv2.w, ndcv3.w);

	return persp;
}

vec4 toScreen(vec3 vertex) {
	return ProjMat * ModelViewMat * vec4(vertex - cameraPos, 1.0);
}

bool isInTriangle(vec3 bary) {
	return bary.x >= 0 && bary.y >= 0 && bary.z >= 0;
}

vec4 unbob(vec4 pos) {
	return BoblessMat * pos;
}

vec3 unbob(vec3 pos) {
	return unbob(vec4(pos, 1.0)).xyz / unbob(vec4(pos, 1.0)).w;
}

vec3 calculateTriangleNormal(vec3 v0, vec3 v1, vec3 v2) {
	vec3 edge1 = v1 - v0;
	vec3 edge2 = v2 - v0;
	vec3 normal = cross(edge1, edge2);
	return normalize(normal);
}

vec4 drawTriangle(vec4 base, Vertex v1, Vertex v2, Vertex v3) {
	vec3 bary = barycentric(toScreen(v1.pos), toScreen(v2.pos), toScreen(v3.pos));
	if (isInTriangle(bary) && dot(normalize(unbob(vertexPos)), calculateTriangleNormal(v1.pos, v2.pos, v3.pos)) <= 0.0) {
		vec4 quadColor = ((bary.x * v1.color) + (bary.y * v2.color) + (bary.z * v3.color));
		vec2 uv = ((bary.x * v1.uv) + (bary.y * v2.uv) + (bary.z * v3.uv));
		float dist = ((bary.x * v1.dist) + (bary.y * v2.dist) + (bary.z * v3.dist));

		if (dist <= closestDistance) {
			closestDistance = dist;
		} else {
			return base;
		}

		vec2 texCoord = mod(uv / 2.0, vec2(0.5)) + (v1.isTop ? vec2(0.5, 0.0) : vec2(0.0));
		vec2 lod = textureQueryLod(Sampler1, texCoord);
		vec4 color = textureGrad(Sampler1, texCoord, dFdx(texCoord), dFdy(texCoord)) * quadColor;
//		color = vec4(max(dFdx(texCoord), dFdy(texCoord)), 0.0, 1.0);

		return linear_fog(color, dist, FogStart, FogEnd, FogColor);
	}
	return base;
}

vec4 drawQuad(vec4 base, Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
	vec4 color = drawTriangle(base, v1, v2, v4);
	color = drawTriangle(color, v1, v4, v3);
	return color;
}

bool basicallyIs(vec3 a, vec3 b) {
  vec3 difference = a - b;
  float distanceSquared = dot(difference, difference);
  return distanceSquared <= 0.1;
}

void main() {
	closestDistance = FogEnd + 100;
	vec3 worldPos = floor(vertexPos + cameraPos + vec3(0.000003));

	vec4 color = texture(Sampler0, texCoord0) * vertexColor;

	if (basicallyIs(normal, vec3(-1.0, 0.0, 0.0))) {
		for (int i = 1; i >= 0; i--) {
			float off = i * 0.5;

			// Left
			Vertex v1 = vertice(vec3(FogEnd, 0.5 - (1.0 / 16.0) + off, (1.0 / 16.0)) + worldPos, vertexColor, vec2(FogEnd, 7.0 / 16.0), false);
			Vertex v2 = vertice(vec3(0.0, 0.5 - (1.0 / 16.0) + off, (1.0 / 16.0)) + worldPos, vertexColor, vec2(0.0, 7.0 / 16.0), false);
			Vertex v3 = vertice(vec3(FogEnd, 1.0 / 16.0 + off, (1.0 / 16.0)) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), false);
			Vertex v4 = vertice(vec3(0.0, 1.0 / 16.0 + off, (1.0 / 16.0)) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), false);

			color = drawQuad(color, v1, v2, v3, v4);

			// Right
			v1 = vertice(vec3(0.0, 1.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), false);
			v2 = vertice(vec3(0.0, 0.5 - (1.0 / 16.0) + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(0.0, 7.0 / 16.0), false);
			v3 = vertice(vec3(FogEnd, 1.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), false);
			v4 = vertice(vec3(FogEnd, 0.5 - (1.0 / 16.0) + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(FogEnd, 7.0 / 16.0), false);

			color = drawQuad(color, v1, v2, v3, v4);

			// Top
			v1 = vertice(vec3(0.0, 7.0 / 16.0 + off, 1.0 / 16.0) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), true);
			v2 = vertice(vec3(FogEnd, 7.0 / 16.0 + off, 1.0 / 16.0) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), true);
			v3 = vertice(vec3(0.0, 7.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(0.0, 15.0 / 16.0), true);
			v4 = vertice(vec3(FogEnd, 7.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(FogEnd, 15.0 / 16.0), true);

			color = drawQuad(color, v1, v2, v3, v4);

			// Bottom
			v1 = vertice(vec3(FogEnd, 1.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(FogEnd, 15.0 / 16.0), true);
			v2 = vertice(vec3(FogEnd, 1.0 / 16.0 + off, 1.0 / 16.0) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), true);
			v3 = vertice(vec3(0.0, 1.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(0.0, 15.0 / 16.0), true);
			v4 = vertice(vec3(0.0, 1.0 / 16.0 + off, 1.0 / 16.0) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), true);

			color = drawQuad(color, v1, v2, v3, v4);

			// Back
			v1 = vertice(vec3(FogEnd, 7.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(1.0 - (1.0 / 16.0), 6.0 / 16.0), false);
			v2 = vertice(vec3(FogEnd, 7.0 / 16.0 + off, 1.0 / 16.0) + worldPos, vertexColor, vec2(1.0 / 16.0, 6.0 / 16.0), false);
			v3 = vertice(vec3(FogEnd, 1.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(1.0 - (1.0 / 16.0), 1.0 / 16.0), false);
			v4 = vertice(vec3(FogEnd, 1.0 / 16.0 + off, 1.0 / 16.0) + worldPos, vertexColor, vec2(1.0 / 16.0, 1.0 / 16.0), false);

			color = drawQuad(color, v1, v2, v3, v4);
		}
	} else if (basicallyIs(normal, vec3(0.0, 0.0, -1.0))) {
		for (int i = 1; i >= 0; i--) {
			float off = i * 0.5;

			// Left
			Vertex v1 = vertice(vec3((1.0 / 16.0), 1.0 / 16.0 + off, 0.0) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), false);
			Vertex v2 = vertice(vec3((1.0 / 16.0), 0.5 - (1.0 / 16.0) + off, 0.0) + worldPos, vertexColor, vec2(0.0, 7.0 / 16.0), false);
			Vertex v3 = vertice(vec3((1.0 / 16.0), 1.0 / 16.0 + off, FogEnd) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), false);
			Vertex v4 = vertice(vec3((1.0 / 16.0), 0.5 - (1.0 / 16.0) + off, FogEnd) + worldPos, vertexColor, vec2(FogEnd, 7.0 / 16.0), false);

			color = drawQuad(color, v1, v2, v3, v4);

			// Right
			v1 = vertice(vec3(1.0 - (1.0 / 16.0), 0.5 - (1.0 / 16.0) + off, FogEnd) + worldPos, vertexColor, vec2(FogEnd, 7.0 / 16.0), false);
			v2 = vertice(vec3(1.0 - (1.0 / 16.0), 0.5 - (1.0 / 16.0) + off, 0.0) + worldPos, vertexColor, vec2(0.0, 7.0 / 16.0), false);
			v3 = vertice(vec3(1.0 - (1.0 / 16.0), 1.0 / 16.0 + off, FogEnd) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), false);
			v4 = vertice(vec3(1.0 - (1.0 / 16.0), 1.0 / 16.0 + off, 0.0) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), false);

			color = drawQuad(color, v1, v2, v3, v4);

			// Top
			v1 = vertice(vec3(1.0 - (1.0 / 16.0), 7.0 / 16.0 + off, FogEnd) + worldPos, vertexColor, vec2(FogEnd, 15.0 / 16.0), true);
			v2 = vertice(vec3(1.0 / 16.0, 7.0 / 16.0 + off, FogEnd) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), true);
			v3 = vertice(vec3(1.0 - (1.0 / 16.0), 7.0 / 16.0 + off, 0.0) + worldPos, vertexColor, vec2(0.0, 15.0 / 16.0), true);
			v4 = vertice(vec3(1.0 / 16.0, 7.0 / 16.0 + off, 0.0) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), true);

			color = drawQuad(color, v1, v2, v3, v4);

			// Bottom
			v1 = vertice(vec3(1.0 / 16.0, 1.0 / 16.0 + off, 0.0) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), true);
			v2 = vertice(vec3(1.0 / 16.0, 1.0 / 16.0 + off, FogEnd) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), true);
			v3 = vertice(vec3(1.0 - (1.0 / 16.0), 1.0 / 16.0 + off, 0.0) + worldPos, vertexColor, vec2(0.0, 15.0 / 16.0), true);
			v4 = vertice(vec3(1.0 - (1.0 / 16.0), 1.0 / 16.0 + off, FogEnd) + worldPos, vertexColor, vec2(FogEnd, 15.0 / 16.0), true);

			color = drawQuad(color, v1, v2, v3, v4);

			// Back
			v1 = vertice(vec3(1.0 / 16.0, 1.0 / 16.0 + off, FogEnd) + worldPos, vertexColor, vec2(1.0 / 16.0, 1.0 / 16.0), false);
			v2 = vertice(vec3(1.0 / 16.0, 7.0 / 16.0 + off, FogEnd) + worldPos, vertexColor, vec2(1.0 / 16.0, 6.0 / 16.0), false);
			v3 = vertice(vec3(1.0 - (1.0 / 16.0), 1.0 / 16.0 + off, FogEnd) + worldPos, vertexColor, vec2(1.0 - (1.0 / 16.0), 1.0 / 16.0), false);
			v4 = vertice(vec3(1.0 - (1.0 / 16.0), 7.0 / 16.0 + off, FogEnd) + worldPos, vertexColor, vec2(1.0 - (1.0 / 16.0), 6.0 / 16.0), false);

			color = drawQuad(color, v1, v2, v3, v4);
		}
	} else if (basicallyIs(normal, vec3(1.0, 0.0, 0.0))) {
		for (int i = 1; i >= 0; i--) {
			float off = i * 0.5;

			// Left
			Vertex v1 = vertice(vec3(0.0, 1.0 / 16.0 + off, (1.0 / 16.0)) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), false);
			Vertex v2 = vertice(vec3(0.0, 0.5 - (1.0 / 16.0) + off, (1.0 / 16.0)) + worldPos, vertexColor, vec2(0.0, 7.0 / 16.0), false);
			Vertex v3 = vertice(vec3(-FogEnd, 1.0 / 16.0 + off, (1.0 / 16.0)) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), false);
			Vertex v4 = vertice(vec3(-FogEnd, 0.5 - (1.0 / 16.0) + off, (1.0 / 16.0)) + worldPos, vertexColor, vec2(FogEnd, 7.0 / 16.0), false);

			color = drawQuad(color, v1, v2, v3, v4);

			// Right
			v1 = vertice(vec3(-FogEnd, 0.5 - (1.0 / 16.0) + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(FogEnd, 7.0 / 16.0), false);
			v2 = vertice(vec3(0.0, 0.5 - (1.0 / 16.0) + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(0.0, 7.0 / 16.0), false);
			v3 = vertice(vec3(-FogEnd, 1.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), false);
			v4 = vertice(vec3(0.0, 1.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), false);

			color = drawQuad(color, v1, v2, v3, v4);

			// Top
			v1 = vertice(vec3(-FogEnd, 7.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(FogEnd, 15.0 / 16.0), true);
			v2 = vertice(vec3(-FogEnd, 7.0 / 16.0 + off, 1.0 / 16.0) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), true);
			v3 = vertice(vec3(0.0, 7.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(0.0, 15.0 / 16.0), true);
			v4 = vertice(vec3(0.0, 7.0 / 16.0 + off, 1.0 / 16.0) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), true);

			color = drawQuad(color, v1, v2, v3, v4);

			// Bottom
			v1 = vertice(vec3(0.0, 1.0 / 16.0 + off, 1.0 / 16.0) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), true);
			v2 = vertice(vec3(-FogEnd, 1.0 / 16.0 + off, 1.0 / 16.0) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), true);
			v3 = vertice(vec3(0.0, 1.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(0.0, 15.0 / 16.0), true);
			v4 = vertice(vec3(-FogEnd, 1.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(FogEnd, 15.0 / 16.0), true);

			color = drawQuad(color, v1, v2, v3, v4);

			// Back
			v1 = vertice(vec3(-FogEnd, 1.0 / 16.0 + off, 1.0 / 16.0) + worldPos, vertexColor, vec2(1.0 / 16.0, 1.0 / 16.0), false);
			v2 = vertice(vec3(-FogEnd, 7.0 / 16.0 + off, 1.0 / 16.0) + worldPos, vertexColor, vec2(1.0 / 16.0, 6.0 / 16.0), false);
			v3 = vertice(vec3(-FogEnd, 1.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(1.0 - (1.0 / 16.0), 1.0 / 16.0), false);
			v4 = vertice(vec3(-FogEnd, 7.0 / 16.0 + off, 1.0 - (1.0 / 16.0)) + worldPos, vertexColor, vec2(1.0 - (1.0 / 16.0), 6.0 / 16.0), false);

			color = drawQuad(color, v1, v2, v3, v4);
		}
	} else if (basicallyIs(normal, vec3(0.0, 0.0, 1.0))) {
		for (int i = 1; i >= 0; i--) {
			float off = i * 0.5;

			// Left
			Vertex v1 = vertice(vec3((1.0 / 16.0), 0.5 - (1.0 / 16.0) + off, -FogEnd) + worldPos, vertexColor, vec2(FogEnd, 7.0 / 16.0), false);
			Vertex v2 = vertice(vec3((1.0 / 16.0), 0.5 - (1.0 / 16.0) + off, 0.0) + worldPos, vertexColor, vec2(0.0, 7.0 / 16.0), false);
			Vertex v3 = vertice(vec3((1.0 / 16.0), 1.0 / 16.0 + off, -FogEnd) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), false);
			Vertex v4 = vertice(vec3((1.0 / 16.0), 1.0 / 16.0 + off, 0.0) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), false);

			color = drawQuad(color, v1, v2, v3, v4);

			// Right
			v1 = vertice(vec3(1.0 - (1.0 / 16.0), 1.0 / 16.0 + off, 0.0) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), false);
			v2 = vertice(vec3(1.0 - (1.0 / 16.0), 0.5 - (1.0 / 16.0) + off, 0.0) + worldPos, vertexColor, vec2(0.0, 7.0 / 16.0), false);
			v3 = vertice(vec3(1.0 - (1.0 / 16.0), 1.0 / 16.0 + off, -FogEnd) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), false);
			v4 = vertice(vec3(1.0 - (1.0 / 16.0), 0.5 - (1.0 / 16.0) + off, -FogEnd) + worldPos, vertexColor, vec2(FogEnd, 7.0 / 16.0), false);

			color = drawQuad(color, v1, v2, v3, v4);

			// Top
			v1 = vertice(vec3(1.0 / 16.0, 7.0 / 16.0 + off, 0.0) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), true);
			v2 = vertice(vec3(1.0 / 16.0, 7.0 / 16.0 + off, -FogEnd) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), true);
			v3 = vertice(vec3(1.0 - (1.0 / 16.0), 7.0 / 16.0 + off, 0.0) + worldPos, vertexColor, vec2(0.0, 15.0 / 16.0), true);
			v4 = vertice(vec3(1.0 - (1.0 / 16.0), 7.0 / 16.0 + off, -FogEnd) + worldPos, vertexColor, vec2(FogEnd, 15.0 / 16.0), true);

			color = drawQuad(color, v1, v2, v3, v4);

			// Bottom
			v1 = vertice(vec3(1.0 - (1.0 / 16.0), 1.0 / 16.0 + off, -FogEnd) + worldPos, vertexColor, vec2(FogEnd, 15.0 / 16.0), true);
			v2 = vertice(vec3(1.0 / 16.0, 1.0 / 16.0 + off, -FogEnd) + worldPos, vertexColor, vec2(FogEnd, 1.0 / 16.0), true);
			v3 = vertice(vec3(1.0 - (1.0 / 16.0), 1.0 / 16.0 + off, 0.0) + worldPos, vertexColor, vec2(0.0, 15.0 / 16.0), true);
			v4 = vertice(vec3(1.0 / 16.0, 1.0 / 16.0 + off, 0.0) + worldPos, vertexColor, vec2(0.0, 1.0 / 16.0), true);

			color = drawQuad(color, v1, v2, v3, v4);

			// Back
			v1 = vertice(vec3(1.0 - (1.0 / 16.0), 7.0 / 16.0 + off, -FogEnd) + worldPos, vertexColor, vec2(1.0 - (1.0 / 16.0), 6.0 / 16.0), false);
			v2 = vertice(vec3(1.0 / 16.0, 7.0 / 16.0 + off, -FogEnd) + worldPos, vertexColor, vec2(1.0 / 16.0, 6.0 / 16.0), false);
			v3 = vertice(vec3(1.0 - (1.0 / 16.0), 1.0 / 16.0 + off, -FogEnd) + worldPos, vertexColor, vec2(1.0 - (1.0 / 16.0), 1.0 / 16.0), false);
			v4 = vertice(vec3(1.0 / 16.0, 1.0 / 16.0 + off, -FogEnd) + worldPos, vertexColor, vec2(1.0 / 16.0, 1.0 / 16.0), false);

			color = drawQuad(color, v1, v2, v3, v4);
		}
	}

	fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
