precision mediump float;// 声明float类型的精度为中等(精度越高越耗资源)
varying vec2 vTextureCoord;//顶点着色器传过来的纹理坐标向量
uniform sampler2D uTexture;//纹理采样器，代表一副纹理
varying vec4 vPosition; //顶点着色器把坐标传过来

void main() {
    vec4 color = texture2D(uTexture, vTextureCoord);//进行纹理采样,拿到当前颜色
    gl_FragColor = color;//不处理
}