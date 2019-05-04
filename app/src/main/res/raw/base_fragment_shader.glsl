#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
void main()
{
//  vec4 vCameraColor = texture2D(uTextureSampler, vTextureCoord);
//  float fGrayColor = (0.3*vCameraColor.r + 0.59*vCameraColor.g + 0.11*vCameraColor.b);
//  gl_FragColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);

  vec2 uv = vTextureCoord;
  if (uv.x <= 0.5) {
    uv.x = uv.x * 2.0;
  } else {
    uv.x = (uv.x - 0.5) * 2.0;
  }
  if (uv.y <= 0.5) {
    uv.y = uv.y * 2.0;
  } else {
    uv.y = (uv.y - 0.5) * 2.0;
  }
  gl_FragColor = texture2D(uTextureSampler, fract(uv));//fract(x): 取小数部分
}
