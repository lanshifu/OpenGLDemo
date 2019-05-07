//发光
precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D vTexture;
uniform float uTime; //应用传时间戳过来
void main() {
    float lightUpValue = abs(sin(uTime / 1000.0)) / 4.0;  //计算变化值，sin函数
    vec4 src = texture2D(vTexture, vTextureCoord);
    vec4 addColor = vec4(lightUpValue, lightUpValue, lightUpValue, 1.0);
    gl_FragColor = src + addColor;  //不断地添加一个颜色
}