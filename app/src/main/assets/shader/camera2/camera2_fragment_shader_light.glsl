#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
uniform float uTime; //应用传时间戳过来
void main()
{
  float lightUpValue = abs(sin(uTime / 1000.0)) / 4.0;  //计算变化值，sin函数
  vec4 addColor = vec4(lightUpValue, lightUpValue, lightUpValue, 1.0);
  vec4 vCameraColor = texture2D(uTextureSampler, vTextureCoord);
  gl_FragColor = vCameraColor + addColor;  //不断地添加一个颜色

}
