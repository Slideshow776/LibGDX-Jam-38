#ifdef GL_ES
precision highp float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

uniform float u_time;
uniform vec2 u_imageSize;
uniform float u_glowRadius;

void main()
{
    vec4 color = texture2D(u_texture, v_texCoords);
    vec2 pixelToTextureCoords = 1.0 / u_imageSize;

    vec4 averageColor = vec4(.0, .0, .0, .0);

    averageColor += texture2D(u_texture, v_texCoords + vec2(-0.1, -0.1) * pixelToTextureCoords);
    averageColor += texture2D(u_texture, v_texCoords + vec2(-0.1,  0.0) * pixelToTextureCoords);
    averageColor += texture2D(u_texture, v_texCoords + vec2(-0.1,  0.1) * pixelToTextureCoords);
    averageColor += texture2D(u_texture, v_texCoords + vec2( 0.0, -0.1) * pixelToTextureCoords);
//    averageColor += texture2D(u_texture, v_texCoords + vec2( 0.0,  0.0) * pixelToTextureCoords);
    averageColor += texture2D(u_texture, v_texCoords + vec2( 0.0,  0.1) * pixelToTextureCoords);
    averageColor += texture2D(u_texture, v_texCoords + vec2( 0.1, -0.1) * pixelToTextureCoords);
    averageColor += texture2D(u_texture, v_texCoords + vec2( 0.1,  0.0) * pixelToTextureCoords);
    averageColor += texture2D(u_texture, v_texCoords + vec2( 0.1,  0.1) * pixelToTextureCoords);

    averageColor *= pow(2.0 * u_glowRadius + 1.0, -2.0) * 0.125;

    float amount = (sin(6.0 * u_time) + 1.0) * .5;
    // extra factor of 2.0 intensifies glow effect
    vec4 glowFactor = vec4( 2.0 * averageColor.rgb, averageColor.a);

    gl_FragColor = v_color * (color + amount * glowFactor);
}
