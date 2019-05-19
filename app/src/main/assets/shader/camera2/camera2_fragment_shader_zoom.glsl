#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;
varying vec2 vTextureCoord;
varying vec4 vPosition; //顶点着色器把坐标传过来
void main()
{
  //到中心点距离
  vec4 nColor=texture2D(uTextureSampler,vTextureCoord);
  float dis=distance(vec2(vPosition.x, vPosition.y), vec2(0, 0));

  //距离在这个区块内放大，改变纹理坐标，至于为什么+0.25?暂时没研究
  if (dis<0.4){
    nColor=texture2D(uTextureSampler, vec2(vTextureCoord.x/2.0+0.25, vTextureCoord.y/2.0+0.25));
  }
  gl_FragColor=nColor;

}
