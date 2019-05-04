//黑白滤镜
precision mediump float;// 声明float类型的精度为中等(精度越高越耗资源)
varying vec2 vTextureCoord;//顶点着色器传过来的纹理坐标向量
uniform sampler2D vTexture;//纹理采样器，代表一副纹理


uniform float uXY;//屏幕宽高比
varying vec4 vPosition; //顶点着色器把坐标传过来


//黑白滤镜比较简单，改变RBG通道值即可

void main() {
    vec4 nColor = texture2D(vTexture, vTextureCoord);//进行纹理采样,拿到当前颜色
    //        float c=nColor.r*vChangeColor.r+nColor.g*vChangeColor.g+nColor.b*vChangeColor.b;
    //        gl_FragColor=vec4(c, c, c, nColor.a);
    float c=(nColor.r + nColor.g + nColor.b)/3.0;
    gl_FragColor=vec4(c, c, c, nColor.a);
}