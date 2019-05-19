attribute vec4 aPosition;
uniform mat4 uMVPMatrix;
attribute vec4 aTexCoord;
varying vec2 vTextureCoord; //传给片元着色器
varying vec4 vPosition; //传给片元着色器
void main()
{
    vTextureCoord = (uMVPMatrix * aTexCoord).xy;
    gl_Position = aPosition;
    vPosition = aPosition;
}