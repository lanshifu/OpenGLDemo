#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
varying vec4 vPosition; //顶点着色器把坐标传过来
uniform float uAlpha;
void main()
{

  vec4 defaultColor = texture2D(uTextureSampler,vTextureCoord);
  gl_FragColor = vec4(defaultColor.rgb,uAlpha); //透明度传过来的

}
