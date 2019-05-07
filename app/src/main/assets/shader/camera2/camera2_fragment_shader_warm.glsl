#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
void main()
{
  vec4 vCameraColor = texture2D(uTextureSampler, vTextureCoord);
  gl_FragColor=vCameraColor + vec4(0.2, 0.2, 0.0, 0.0); //暖就是多加点红跟绿
}
