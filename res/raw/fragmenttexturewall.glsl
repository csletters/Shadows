precision mediump float;
uniform sampler2D uTexture;
varying vec3 normalizednormal;
varying vec3 viewVector;
varying vec3 lightVector;
varying vec4 ShadowCoord;
void main() {

	
	vec3 v = normalize(viewVector);
	vec3 l = normalize(lightVector);
	vec3 N = normalize(normalizednormal);
	
	vec3 R = reflect(-l,N);
	
	vec3 diffuse = 3.0*max(dot(N,l)*vec3(1.0),0.0);
	vec3 specular = max(pow(dot(R,v),10.0),0.0)*vec3(0.0);
	float visibility = 1.0;
	if ( vec4(texture2D( uTexture, ShadowCoord.xy )).z  <  ShadowCoord.z - 0.005){
    	visibility = 0.1;
	}
	gl_FragColor = vec4(0.5,0.5,0.5,1.0)+vec4(vec3(diffuse+specular+0.1)*visibility,0.0);
}