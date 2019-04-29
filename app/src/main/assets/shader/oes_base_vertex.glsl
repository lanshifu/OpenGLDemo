attribute vec4 vPosition; //顶点坐标
attribute vec2 vCoord;
uniform mat4 vMatrix;
uniform mat4 vCoordMatrix; //纹理矩阵
varying vec2 textureCoordinate;

void main(){
    gl_Position = vMatrix*vPosition;
    textureCoordinate = (vCoordMatrix*vec4(vCoord,0,1)).xy;
}