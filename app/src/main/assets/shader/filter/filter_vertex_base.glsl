uniform mat4 uMVPMatrix;//接收传入的转换矩阵
attribute vec4 aPosition;//接收传入的顶点
attribute vec2 aTexCoord;//接收传入的顶点纹理位置
varying vec2 vTextureCoord;//增加用于传递给片元着色器的纹理位置变量
varying vec4 vPosition;//传顶点坐标给片元着色器
void main() {
    gl_Position = uMVPMatrix * aPosition;//矩阵变换计算之后的位置
    vPosition = uMVPMatrix * aPosition;//矩阵变换计算之后的位置
    vTextureCoord = aTexCoord;
}


