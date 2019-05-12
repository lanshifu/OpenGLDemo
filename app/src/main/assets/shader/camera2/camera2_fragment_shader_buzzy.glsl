#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
void main()
{
  vec4 vCameraColor = texture2D(uTextureSampler, vTextureCoord);

  float dis = 0.01; //距离越大越模糊

//  nColor+=texture2D(vTextureCoord, vec2(vTextureCoord.x-dis, vTextureCoord.y-dis));
//  nColor+=texture2D(vTextureCoord, vec2(vTextureCoord.x-dis, vTextureCoord.y+dis));
//  nColor+=texture2D(vTextureCoord, vec2(vTextureCoord.x+dis, vTextureCoord.y-dis));
//  nColor+=texture2D(vTextureCoord, vec2(vTextureCoord.x+dis, vTextureCoord.y+dis));
//  nColor+=texture2D(vTextureCoord, vec2(vTextureCoord.x-dis, vTextureCoord.y-dis));
//  nColor+=texture2D(vTextureCoord, vec2(vTextureCoord.x-dis, vTextureCoord.y+dis));
//  nColor+=texture2D(vTextureCoord, vec2(vTextureCoord.x+dis, vTextureCoord.y-dis));
//  nColor+=texture2D(vTextureCoord, vec2(vTextureCoord.x+dis, vTextureCoord.y+dis));
//  nColor+=texture2D(vTextureCoord, vec2(vTextureCoord.x-dis, vTextureCoord.y-dis));
//  nColor+=texture2D(vTextureCoord, vec2(vTextureCoord.x-dis, vTextureCoord.y+dis));
//  nColor+=texture2D(vTextureCoord, vec2(vTextureCoord.x+dis, vTextureCoord.y-dis));
//  nColor+=texture2D(vTextureCoord, vec2(vTextureCoord.x+dis, vTextureCoord.y+dis));
//  nColor/=13.0; //周边13个颜色相加，然后取平均，作为这个点的颜色
  gl_FragColor=vCameraColor;
}
