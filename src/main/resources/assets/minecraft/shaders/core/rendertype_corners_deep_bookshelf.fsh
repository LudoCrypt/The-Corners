#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;
uniform mat4 BobMat;

uniform vec3 cameraPos;

uniform vec2 ScreenSize;

uniform float GameTime;

in vec4 vertexColor;
in vec3 vertexPos;
in vec2 texCoord0;
in float vertexDistance;

in vec4 glPos;

in mat4 BoblessMat;

in vec4 normal;




#define leftPlane vec4(ProjMat[3] + ProjMat[0])
#define rightPlane vec4(ProjMat[3] - ProjMat[0])
#define bottomPlane vec4(ProjMat[3] + ProjMat[1])
#define topPlane vec4(ProjMat[3] - ProjMat[1])
#define nearPlane vec4(ProjMat[3] + ProjMat[2])
#define farPlane vec4(ProjMat[3] - ProjMat[2])

















out vec4 fragColor;

mat4 pack(vec3 pos, vec3 faceNormal, vec3 perpendicularNormal) {
  vec3 tangent = normalize(cross(faceNormal, perpendicularNormal));
  vec3 bitangent = normalize(cross(faceNormal, tangent));

  mat4 matrix = mat4(
    vec4(tangent, 0.0),
    vec4(bitangent, 0.0),
    vec4(faceNormal, 0.0),
    vec4(pos, 1.0)
  );
 
  return matrix;
}

vec2 calculateUV(vec3 p) {
		if (abs(normal.x) < 0.003 && abs(normal.y - 1.0) < 0.003 && abs(normal.z) < 0.003) {
			return p.xz;
		}
	
	  // Compute the tangent and bitangent vectors of the quad
	  vec3 tangent = normalize(cross(vec3(0.0, 1.0, 0.0), normal.xyz));
	  vec3 bitangent = normalize(cross(normal.xyz, tangent));
	
	  // Calculate the UV coordinates based on the quad's orientation
	  vec2 uv;
	  uv.x = dot(p, tangent);
	  uv.y = dot(p, bitangent);
	
	  return uv;
}

float distanceToBackPlane(vec3 rayOrigin, vec3 rayNormal, vec3 boxPos, vec3 boxSize) {
    vec3 invRayDir = 1.0 / rayNormal;
    vec3 tMin = (boxPos - rayOrigin) * invRayDir;
    vec3 tMax = (boxPos + boxSize - rayOrigin) * invRayDir;
    vec3 t1 = min(tMin, tMax);
    vec3 t2 = max(tMin, tMax);
    float tNear = max(max(t1.x, t1.y), t1.z);
    float tFar = min(min(t2.x, t2.y), t2.z);
    
    if (tNear > tFar || tFar < 0.0)
        return -1.0; // No intersection
    
    return tFar;
}

vec4 unbob(vec4 pos) {
	return BoblessMat * pos;
}

vec3 unbob(vec3 pos) {
	return unbob(vec4(pos, 1.0)).xyz / unbob(vec4(pos, 1.0)).w;
}

vec3 perpendicularNormalPos(vec3 pos) {
	return normalize(vec3(-pos.z, -pos.y, pos.x));
}











bool behindPlane(vec4 position, vec4 clipPlane) {
    float dotProduct = dot(position.xyz, clipPlane.xyz);
    float signedDistance = dotProduct + clipPlane.w;
    return signedDistance < -0.003;
}

bool isInsideAllPlanes(vec4 position) {
	return (behindPlane(position, leftPlane) && behindPlane(position, rightPlane) && behindPlane(position, topPlane) && behindPlane(position, bottomPlane));
}




vec4 clipSpaceIntersection(vec4 vertexA, vec4 vertexB) {
  if (isInsideAllPlanes(vertexA) && isInsideAllPlanes(vertexB)) {
    return vertexA;
  }

  if (isInsideAllPlanes(vertexA)) {
    vec4 intersectionPoint;

    if (dot(vertexA, topPlane) < 0.0) {
      intersectionPoint = mix(vertexA, vertexB, dot(topPlane, vertexA) / dot(topPlane, vertexA - vertexB));
      vertexA = intersectionPoint;
    }

    if (dot(vertexA, bottomPlane) < 0.0) {
      intersectionPoint = mix(vertexA, vertexB, dot(bottomPlane, vertexA) / dot(bottomPlane, vertexA - vertexB));
      vertexA = intersectionPoint;
    }

    if (dot(vertexA, leftPlane) < 0.0) {
      intersectionPoint = mix(vertexA, vertexB, dot(leftPlane, vertexA) / dot(leftPlane, vertexA - vertexB));
      vertexA = intersectionPoint;
    }

    if (dot(vertexA, rightPlane) < 0.0) {
      intersectionPoint = mix(vertexA, vertexB, dot(rightPlane, vertexA) / dot(rightPlane, vertexA - vertexB));
      vertexA = intersectionPoint;
    }

    return vertexA;
  }

  vec4 intersection = vec4(-2.0);

  if (dot(vertexA, topPlane) < 0.0 && dot(vertexB, topPlane) >= 0.0) {
    intersection = mix(vertexA, vertexB, dot(topPlane, vertexA) / dot(topPlane, vertexA - vertexB));
  }

  if (dot(vertexA, bottomPlane) < 0.0 && dot(vertexB, bottomPlane) >= 0.0) {
    vec4 newIntersection = mix(vertexA, vertexB, dot(bottomPlane, vertexA) / dot(bottomPlane, vertexA - vertexB));
    if (intersection == vec4(-2.0) || dot(newIntersection, topPlane) > 0.0) {
      intersection = newIntersection;
    }
  }

  if (dot(vertexA, leftPlane) < 0.0 && dot(vertexB, leftPlane) >= 0.0) {
    vec4 newIntersection = mix(vertexA, vertexB, dot(leftPlane, vertexA) / dot(leftPlane, vertexA - vertexB));
    if (intersection == vec4(-2.0) || dot(newIntersection, topPlane) > 0.0 || dot(newIntersection, bottomPlane) > 0.0) {
      intersection = newIntersection;
    }
  }

  if (dot(vertexA, rightPlane) < 0.0 && dot(vertexB, rightPlane) >= 0.0) {
    vec4 newIntersection = mix(vertexA, vertexB, dot(rightPlane, vertexA) / dot(rightPlane, vertexA - vertexB));
    if (intersection == vec4(-2.0) || dot(newIntersection, topPlane) > 0.0 || dot(newIntersection, bottomPlane) > 0.0 || dot(newIntersection, leftPlane) > 0.0) {
      intersection = newIntersection;
    }
  }

  return intersection;
}



void inAgainstOut(vec4 vertexA, vec4 vertexB, out vec4 intersectionAB, out vec4 intersectionBA) {
	intersectionAB = clipSpaceIntersection(vertexA, vertexB);
	intersectionBA = clipSpaceIntersection(vertexB, vertexA);
}



void clipIntersections(vec4 vertexA, vec4 vertexB, vec4 vertexC, out vec4 intersectionAB, out vec4 intersectionAC, out vec4 intersectionBA, out vec4 intersectionBC, out vec4 intersectionCA, out vec4 intersectionCB) {
  bool isAInsideAllPlanes = isInsideAllPlanes(vertexA);
  bool isBInsideAllPlanes = isInsideAllPlanes(vertexB);
  bool isCInsideAllPlanes = isInsideAllPlanes(vertexC);

  if (isAInsideAllPlanes && isBInsideAllPlanes && isCInsideAllPlanes) {
    return;
  }

  if (
    (behindPlane(vertexA, leftPlane) && behindPlane(vertexB, leftPlane) && behindPlane(vertexC, leftPlane)) ||
    (behindPlane(vertexA, rightPlane) && behindPlane(vertexB, rightPlane) && behindPlane(vertexC, rightPlane)) ||
    (behindPlane(vertexA, topPlane) && behindPlane(vertexB, topPlane) && behindPlane(vertexC, topPlane)) ||
    (behindPlane(vertexA, bottomPlane) && behindPlane(vertexB, bottomPlane) && behindPlane(vertexC, bottomPlane))
  ) {
    return;
  }

  inAgainstOut(vertexA, vertexB, intersectionAB, intersectionBA);
  inAgainstOut(vertexA, vertexC, intersectionAC, intersectionCA);
  inAgainstOut(vertexB, vertexC, intersectionBC, intersectionCB);
}

vec2 toScreen(vec4 clip) {
  return ((clip.xyz / (abs(clip.w) + 0.003)).xy + 1.0) * 0.5 * ScreenSize;
}

vec4 toClip(vec3 cartesian) {
  return ProjMat * ModelViewMat * vec4(cartesian, 1.0);
}

bool isInTriangle(vec3 bary) {
  return bary.x >= 0.0 && bary.y >= 0.0 && bary.z >= 0.0;
}

vec3 barycentric(vec2 A, vec2 B, vec2 C) {
  vec2 v0 = C - A;
  vec2 v1 = B - A;
  vec2 v2 = gl_FragCoord.xy - A;

  float dot00 = dot(v0, v0);
  float dot01 = dot(v0, v1);
  float dot02 = dot(v0, v2);
  float dot11 = dot(v1, v1);
  float dot12 = dot(v1, v2);

  float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
  float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
  float v = (dot00 * dot12 - dot01 * dot02) * invDenom;
  float w = 1 - u - v;

  return vec3(u, v, w);
}

vec3 barycentric(vec4 A, vec4 B, vec4 C) {
	return barycentric(toScreen(A), toScreen(B), toScreen(C));
}

vec3 draw(vec3 v1, vec3 v2, vec3 v3) {
  vec4 clipSpaceV1 = toClip(v1);
  vec4 clipSpaceV2 = toClip(v2);
  vec4 clipSpaceV3 = toClip(v3);

  vec4 intersectionAB = clipSpaceV1;
  vec4 intersectionAC = clipSpaceV1;
  vec4 intersectionBA = clipSpaceV2;
  vec4 intersectionBC = clipSpaceV2;
  vec4 intersectionCA = clipSpaceV3;
  vec4 intersectionCB = clipSpaceV3;

  clipIntersections(clipSpaceV1, clipSpaceV2, clipSpaceV3, intersectionAB, intersectionAC, intersectionBA, intersectionBC, intersectionCA, intersectionCB);



  vec3 clip1 = vec3(-2.0);
  vec3 clip2 = vec3(-2.0);
  vec3 clip3 = vec3(-2.0);
  vec3 clip4 = vec3(-2.0);

	if (!(any(equal(intersectionAB, vec4(-2.0))) ||
	    any(equal(intersectionBA, vec4(-2.0))) ||
	    any(equal(intersectionBC, vec4(-2.0))))) {
	  clip1 = barycentric(intersectionAB, intersectionBA, intersectionBC);
	}
	if (!(any(equal(intersectionAB, vec4(-2.0))) ||
	    any(equal(intersectionBC, vec4(-2.0))) ||
	    any(equal(intersectionCB, vec4(-2.0))))) {
	  vec3 clip2 = barycentric(intersectionAB, intersectionBC, intersectionCB);
	}
	if (!(any(equal(intersectionAB, vec4(-2.0))) ||
	    any(equal(intersectionCB, vec4(-2.0))) ||
	    any(equal(intersectionCA, vec4(-2.0))))) {
	  vec3 clip3 = barycentric(intersectionAB, intersectionCB, intersectionCA);
	}
	if (!(any(equal(intersectionAB, vec4(-2.0))) ||
	    any(equal(intersectionCA, vec4(-2.0))) ||
	    any(equal(intersectionAC, vec4(-2.0))))) {
	  vec3 clip4 = barycentric(intersectionAB, intersectionCA, intersectionAC);
	}

	if (isInTriangle(clip1)) {
		return clip1;
	} else if (isInTriangle(clip2)) {
		return clip2;
	}else if (isInTriangle(clip3)) {
		return clip3;
	}else if (isInTriangle(clip4)) {
		return clip4;
	}


  return vec3(-1.0);
}

void main() {

	vec3 v0 = vec3(1.0, 0.0, 0.0) - cameraPos;
	vec3 v1 = vec3(1.0, 1.0, 0.0) - cameraPos;
	vec3 v2 = vec3(0.0, 1.0, 0.0) - cameraPos;
	vec3 v3 = vec3(0.0, 0.0, 0.0) - cameraPos;

//	vec3 drawnTriangle = draw(v0, v1, v3);
//
//	vec4 color = vec4(1.0, 1.0, 1.0, 1.0);
//	if (drawnTriangle != vec3(-1.0)) {
//		color = vec4(drawnTriangle, 1.0);
//	}
vec4 color = vec4(1.0, 1.0, 1.0, 1.0);

if (isInsideAllPlanes(glPos)){
color = vec4(1.0, 0.0, 0.0, 1.0);
}

	fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
