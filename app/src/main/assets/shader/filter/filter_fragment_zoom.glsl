//放大
precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D vTexture;
varying vec4 vPosition; //顶点着色器把坐标传过来
void main() {

    //到中心点距离
    float dis=distance(vec2(vPosition.x, vPosition.y), vec2(0, 0));
    vec4 nColor=texture2D(vTexture,vTextureCoord);
    //距离在这个区块内放大，改变纹理坐标，至于为什么+0.25?暂时没研究
    if (dis<0.4){
        nColor=texture2D(vTexture, vec2(vTextureCoord.x/2.0+0.25, vTextureCoord.y/2.0+0.25));
    }
    gl_FragColor=nColor;
}