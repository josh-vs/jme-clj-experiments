// #import "Common/ShaderLib/GLSLCompat.glsllib"
// #import "Common/ShaderLib/Skinning.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"
// #import "Common/ShaderLib/MorphAnim.glsllib"

attribute vec3 inPosition;
uniform float g_Time;

#ifdef HAS_SINFREQ
    uniform float m_SinFreq;
#endif

#ifdef HAS_WAVESPEED
    uniform float m_WaveSpeed;
#endif

#ifdef HAS_WAVEQUANT
    uniform float m_WaveQuant;
#endif

varying vec3 vertexPosition;

void main() {

    float SinFreq = 1.0;
    #ifdef HAS_SINFREQ
        SinFreq *= m_SinFreq;
    #endif
    
    float WaveQuant = 1.0;
    #ifdef HAS_WAVEQUANT
        WaveQuant *= m_WaveQuant;
    #endif

    float WaveTime = g_Time;
    #ifdef HAS_WAVESPEED
        WaveTime = (g_Time * m_WaveSpeed);
    #endif

    vec4 modelSpacePos = vec4(inPosition, 1.0);
    
    float waveZ = sin(inPosition.z * WaveQuant + WaveTime) * SinFreq;
    float waveX = cos(inPosition.x * WaveQuant + WaveTime) * SinFreq;
    
    modelSpacePos.y += waveZ;
    modelSpacePos.y += waveX;

    // float sinTimeX = sin(inPosition.x + WaveTime);
    // float sinTimeY = sin(inPosition.y + WaveTime);
    // float sinX = sin(inPosition.x);
    // float sinY = sin(inPosition.y);

    // modelSpacePos.y = sinTimeX * sinX * SinFreq;
    // modelSpacePos.x += sinTimeY * sinY * SinFreq;

    // modelSpacePos.y = sin(inPosition.x + WaveTime) * SinFreq;
    // modelSpacePos.x += sin(inPosition.y + WaveTime) * SinFreq;

    vertexPosition = inPosition;

    gl_Position = TransformWorldViewProjection(modelSpacePos);

}