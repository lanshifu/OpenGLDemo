//暖色滤镜
precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D vTexture;
void main() {
    vec4 nColor = texture2D(vTexture, vTextureCoord);//进行纹理采样,拿到当前颜色

    gl_FragColor=nColor + vec4(0.2, 0.2, 0.0, 0.0); //暖就是多加点红跟绿
}