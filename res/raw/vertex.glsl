attribute vec4 aPosition;
attribute vec3 normal;
attribute vec2 aTexCord;
uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

varying vec2 vTexCord;

void main() {
	
	vTexCord = aTexCord;
	gl_Position = projection*view*model*aPosition;
}