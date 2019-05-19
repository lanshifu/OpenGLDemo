#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
void main()
{
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
  gl_FragColor = texture2D(uTextureSampler, uv);
}
