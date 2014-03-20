attribute vec4 aPosition;
attribute vec3 normal;
uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;
uniform mat4 biasMatrix;

varying vec3 normalizednormal;
varying vec3 viewVector;
varying vec3 lightVector;
varying vec4 ShadowCoord;
void main() {
	vec3 light_pos = vec3(0.0,5.0,10.0);
	vec4 vPosition = view*model*aPosition;
	//view vector
	viewVector = vec3(-vPosition.xyz);
	
	//vector from point to light source
	lightVector = vec3(light_pos - (view*model*aPosition).xyz);
	
	normalizednormal = vec3(normalize((view*model)*vec4(normal,0.0)));

	gl_Position = projection*view*model*aPosition;
	// Same, but with the light's view matrix
	ShadowCoord = biasMatrix* projection*view*model * aPosition;
}