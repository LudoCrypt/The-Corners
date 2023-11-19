#version 400

#moj_import <fog.glsl>

#define ATLAS_SIZE ivec2(2, 2)
#define BOOK_TEXTURE ivec2(0, 0)
#define TOP_TEXTURE ivec2(0, 1)
#define SIDE_TEXTURE ivec2(1, 0)

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

in vec3 topRow;
in vec3 bottomRow;

out vec4 fragColor;

out float closestDistance;

struct Vertex {
	vec3 pos;
	vec4 color;
	vec2 uv;
	ivec2 atlasSize;
	ivec2 atlasOffset;
	float dist;
};

Vertex vertice(vec3 pos, vec4 color, vec2 uv, ivec2 atlasSize, ivec2 atlasOffset) {
	Vertex v;
	v.pos = pos;
	v.color = color;
	v.uv = uv;
	v.atlasSize = atlasSize;
	v.atlasOffset = atlasOffset;
	v.dist = fog_distance(ModelViewMat, pos - cameraPos, FogShape);
	return v;
}

Vertex vertice(mat4 mat, vec3 pos, vec4 color, vec2 uv, ivec2 atlasSize, ivec2 atlasOffset) {
	vec4 p = mat * vec4(pos, 1.0);
	p /= p.w;
	return vertice(p.xyz, color, uv, atlasSize, atlasOffset);
}

Vertex vertice(mat4 mat, Vertex v) {
	return vertice(mat, v.pos, v.color, v.uv, v.atlasSize, v.atlasOffset);
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
		vec2 uv = ((bary.x * v1.uv) + (bary.y * v2.uv) + (bary.z * v3.uv)) / 16.0;
		float dist = ((bary.x * v1.dist) + (bary.y * v2.dist) + (bary.z * v3.dist));

		if (dist <= closestDistance) {
			closestDistance = dist;
		} else {
			return base;
		}

		vec2 texCoord = (mod(uv, 1.0) / vec2(v1.atlasSize)) + (vec2(v1.atlasOffset) / vec2(v1.atlasSize));
		vec4 color = textureGrad(Sampler1, texCoord, dFdx(uv), dFdy(uv)) * quadColor;

		return linear_fog(color, dist, FogStart, FogEnd, FogColor);
	}
	return base;
}

vec4 drawQuad(vec4 base, Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
	vec4 col = base;
	col = drawTriangle(col, v1, v2, v4);
	col = drawTriangle(col, v1, v4, v3);
	return col;
}

vec4 drawQuad(vec4 base, Vertex v1, Vertex v2, Vertex v3, Vertex v4, mat4 m) {
	return drawQuad(base, vertice(m, v1), vertice(m, v2), vertice(m, v3), vertice(m, v4));
}

vec4 drawBox(
				vec4 base,
				vec3 size,
				vec3 pos,
				mat4 m,
				vec4 vc,
				ivec2 bottomTex,
				vec4 bottomUV,
				ivec2 topTex,
				vec4 topUV,
				ivec2 leftTex,
				vec4 leftUV,
				ivec2 rightTex,
				vec4 rightUV,
				ivec2 frontTex,
				vec4 frontUV,
				ivec2 backTex,
				vec4 backUV
) {
	vec4 col = base;
	size = size / 2.0;

	// Bottom
	col = drawQuad(col,
		vertice(pos + vec3(size.x, -size.y, size.z), vc, bottomUV.xy, ATLAS_SIZE, bottomTex),
		vertice(pos + vec3(-size.x, -size.y, size.z), vc, bottomUV.xw, ATLAS_SIZE, bottomTex),
		vertice(pos + vec3(size.x, -size.y, -size.z), vc, bottomUV.zy, ATLAS_SIZE, bottomTex),
		vertice(pos + vec3(-size.x, -size.y, -size.z), vc, bottomUV.zw, ATLAS_SIZE, bottomTex),
	m);
	// Top
	col = drawQuad(col,
		vertice(pos + vec3(size.x, size.y, size.z), vc, topUV.xy, ATLAS_SIZE, topTex),
		vertice(pos + vec3(size.x, size.y, -size.z), vc, topUV.zy, ATLAS_SIZE, topTex),
		vertice(pos + vec3(-size.x, size.y, size.z), vc, topUV.xw, ATLAS_SIZE, topTex),
		vertice(pos + vec3(-size.x, size.y, -size.z), vc, topUV.zw, ATLAS_SIZE, topTex),
	m);
	// Front
	col = drawQuad(col,
		vertice(pos + vec3(size.x, size.y, size.z), vc, frontUV.xy, ATLAS_SIZE, frontTex),
		vertice(pos + vec3(size.x, -size.y, size.z), vc, frontUV.xw, ATLAS_SIZE, frontTex),
		vertice(pos + vec3(size.x, size.y, -size.z), vc, frontUV.zy, ATLAS_SIZE, frontTex),
		vertice(pos + vec3(size.x, -size.y, -size.z), vc, frontUV.zw, ATLAS_SIZE, frontTex),
	m);
	// Back
	col = drawQuad(col,
		vertice(pos + vec3(-size.x, size.y, size.z), vc, backUV.xy, ATLAS_SIZE, backTex),
		vertice(pos + vec3(-size.x, size.y, -size.z), vc, backUV.xw, ATLAS_SIZE, backTex),
		vertice(pos + vec3(-size.x, -size.y, size.z), vc, backUV.zy, ATLAS_SIZE, backTex),
		vertice(pos + vec3(-size.x, -size.y, -size.z), vc, backUV.zw, ATLAS_SIZE, backTex),
	m);
	// Left
	col = drawQuad(col,
		vertice(pos + vec3(size.x, size.y, size.z), vc, leftUV.xy, ATLAS_SIZE, leftTex),
		vertice(pos + vec3(-size.x, size.y, size.z), vc, leftUV.zy, ATLAS_SIZE, leftTex),
		vertice(pos + vec3(size.x, -size.y, size.z), vc, leftUV.xw, ATLAS_SIZE, leftTex),
		vertice(pos + vec3(-size.x, -size.y, size.z), vc, leftUV.zw, ATLAS_SIZE, leftTex),
	m);
	// Right
	col = drawQuad(col,
		vertice(pos + vec3(size.x, size.y, -size.z), vc, rightUV.xy, ATLAS_SIZE, rightTex),
		vertice(pos + vec3(size.x, -size.y, -size.z), vc, rightUV.xw, ATLAS_SIZE, rightTex),
		vertice(pos + vec3(-size.x, size.y, -size.z), vc, rightUV.zy, ATLAS_SIZE, rightTex),
		vertice(pos + vec3(-size.x, -size.y, -size.z), vc, rightUV.zw, ATLAS_SIZE, rightTex),
	m);
	
	return col;
}

void main() {
	closestDistance = FogEnd + 100;
	vec3 worldPos = floor(vertexPos + cameraPos + vec3(0.00003));

	vec4 color = texture(Sampler0, texCoord0) * vertexColor;

	mat4 rotateMatrix = mat4(
		vec4(0, 0, 1, 0),
		vec4(0, 1, 0, 0),
		vec4(-1, 0, 0, 0),
		vec4(worldPos + vec3(1.0, 0.0, 0.0), 1)
	);

	if (abs(normal.x - 1.0) < 0.1) {
		rotateMatrix = mat4(
			vec4(1, 0, 0, 0),
			vec4(0, 1, 0, 0),
			vec4(0, 0, 1, 0),
			vec4(worldPos, 1)
		);
	} else if (abs(normal.x + 1.0) < 0.1) {
		rotateMatrix = mat4(
			vec4(-1, 0, 0, 0),
			vec4(0, 1, 0, 0),
			vec4(0, 0, -1, 0),
			vec4(worldPos + vec3(0.0, 0.0, 1.0), 1)
		);
	} else if (abs(normal.z + 1.0) < 0.1) {
		rotateMatrix = mat4(
			vec4(0, 0, -1, 0),
			vec4(0, 1, 0, 0),
			vec4(1, 0, 0, 0),
			vec4(worldPos, 1)
		);
	}

	mat4 faceMat = rotateMatrix;

	color = drawBox(color,
						-vec3(FogEnd, 6.0 / 16.0, 14.0 / 16.0),
						vec3(-(FogEnd / 2.0), 12.0 / 16.0, 0.5),
						rotateMatrix,
						vertexColor,
						TOP_TEXTURE,
						vec4(1.0, FogEnd * 16.0, 15.0, 0.0),
						TOP_TEXTURE,
						vec4(1.0, FogEnd * 16.0, 15.0, 0.0),
						SIDE_TEXTURE,
						vec4(0.0, 1.0, -FogEnd * 16.0, 7.0),
						SIDE_TEXTURE,
						vec4(0.0, 1.0, -FogEnd * 16.0, 7.0),
						SIDE_TEXTURE,
						vec4(0.0, 1.0, -FogEnd * 16.0, 7.0),
						SIDE_TEXTURE,
						vec4(0.0, 1.0, -FogEnd * 16.0, 7.0)
	);

	color = drawBox(color,
						-vec3(FogEnd, 6.0 / 16.0, 14.0 / 16.0),
						vec3(-(FogEnd / 2.0), 4.0 / 16.0, 0.5),
						rotateMatrix,
						vertexColor,
						TOP_TEXTURE,
						vec4(1.0, FogEnd * 16.0, 15.0, 0.0),
						TOP_TEXTURE,
						vec4(1.0, FogEnd * 16.0, 15.0, 0.0),
						SIDE_TEXTURE,
						vec4(0.0, 1.0, -FogEnd * 16.0, 7.0),
						SIDE_TEXTURE,
						vec4(0.0, 1.0, -FogEnd * 16.0, 7.0),
						SIDE_TEXTURE,
						vec4(0.0, 1.0, -FogEnd * 16.0, 7.0),
						SIDE_TEXTURE,
						vec4(0.0, 1.0, -FogEnd * 16.0, 7.0)
	);


	// 100 000
	vec4 boxUV = vec4(1.0, 2.0, 5.0, 7.0);
	if (abs(topRow.x - 1.0) < 0.1) {
		color = drawBox(color,
							vec3(FogEnd, 5.0 / 16.0, 0.25),
							vec3(-(FogEnd / 2.0), 11.5 / 16.0, 13.0 / 16.0),
							rotateMatrix,
							vertexColor,
							BOOK_TEXTURE,
							boxUV.xwzy,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV.zyxw,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV
		);
	}

	// 010 000
	if (abs(topRow.y - 1.0) < 0.1) {
		boxUV = vec4(6.0, 1.0, 10.0, 7.0);
		color = drawBox(color,
							vec3(FogEnd, 6.0 / 16.0, 0.25),
							vec3(-(FogEnd / 2.0), 12.0 / 16.0, 0.5),
							rotateMatrix,
							vertexColor,
							BOOK_TEXTURE,
							boxUV.xwzy,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV.zyxw,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV
		);
	}

	// 001 000
	if (abs(topRow.z - 1.0) < 0.1) {
		boxUV = vec4(11.0, 1.0, 15.0, 7.0);
		color = drawBox(color,
							vec3(FogEnd, 6.0 / 16.0, 0.25),
							vec3(-(FogEnd / 2.0), 12.0 / 16.0, 3.0 / 16.0),
							rotateMatrix,
							vertexColor,
							BOOK_TEXTURE,
							boxUV.xwzy,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV.zyxw,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV
		);
	}

	// 000 100
	if (abs(bottomRow.x - 1.0) < 0.1) {
		boxUV = vec4(1.0, 9.0, 5.0, 15.0);
		color = drawBox(color,
							vec3(FogEnd, 6.0 / 16.0, 0.25),
							vec3(-(FogEnd / 2.0), 4.0 / 16.0, 13.0 / 16.0),
							rotateMatrix,
							vertexColor,
							BOOK_TEXTURE,
							boxUV.xwzy,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV.zyxw,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV
		);
	}

	// 000 010
	if (abs(bottomRow.y - 1.0) < 0.1) {
		boxUV = vec4(6.0, 10.0, 10.0, 15.0);
		color = drawBox(color,
							vec3(FogEnd, 5.0 / 16.0, 0.25),
							vec3(-(FogEnd / 2.0), 3.5 / 16.0, 8.0 / 16.0),
							rotateMatrix,
							vertexColor,
							BOOK_TEXTURE,
							boxUV.xwzy,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV.zyxw,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV
		);
	}

	// 000 001
	if (abs(bottomRow.z - 1.0) < 0.1) {
		boxUV = vec4(11.0, 9.0, 15.0, 15.0);
		color = drawBox(color,
							vec3(FogEnd, 6.0 / 16.0, 0.25),
							vec3(-(FogEnd / 2.0), 4.0 / 16.0, 3.0 / 16.0),
							rotateMatrix,
							vertexColor,
							BOOK_TEXTURE,
							boxUV.xwzy,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV.zyxw,
							BOOK_TEXTURE,
							boxUV,
							BOOK_TEXTURE,
							boxUV
		);
	}

	fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
	if (fragColor.a < 0.1) {
		discard;
	}
}
