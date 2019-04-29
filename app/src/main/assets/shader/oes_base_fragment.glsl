#extension GL_OES_EGL_image_external : require
//头部增加使用扩展纹理的声明#extension GL_OES_EGL_image_external : require。
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES vTexture;  //不再用sampler2D采样，需要samplerExternalOES 纹理采样器
void main() {
    gl_FragColor = texture2D( vTexture, textureCoordinate );
}