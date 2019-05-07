#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
void main()
{
  vec4 nColor = texture2D(uTextureSampler, vTextureCoord);//进行纹理采样,拿到当前颜色
  vec4 deltaColor=nColor + vec4(0.0, 0.0, 0.3, 0.0); //冷就是多加点蓝
  gl_FragColor=deltaColor;
}
