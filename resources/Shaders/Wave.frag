#import "Common/ShaderLib/GLSLCompat.glsllib"

#ifdef HAS_COLOR
    uniform vec4 m_Color;
#endif

uniform float g_Time;
varying vec3 vertexPosition;


void main() {

    float r = cos(vertexPosition.x - (g_Time * 4)) * 0.5 + 0.5;
    float g = cos(vertexPosition.x - (g_Time * 4) + 2.0) * 0.5 + 0.5; // 120 degrees
    float b = cos(vertexPosition.x - (g_Time * 4) + 4.0) * 0.5 + 0.5; // 240 degrees
    
    vec4 color = vec4(r, g, b, 1.0);

    // vec4 color = vec4(vertexPosition, 1.0);

    #ifdef HAS_COLOR
        color *= m_Color;
    #endif

    gl_FragColor = color;

}