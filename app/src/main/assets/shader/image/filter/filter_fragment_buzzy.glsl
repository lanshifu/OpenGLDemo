//模糊滤镜
precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D vTexture;
void main() {
    vec4 nColor = texture2D(vTexture, vTextureCoord);//进行纹理采样,拿到当前颜色

    vec3 vChangeColor = vec3(0.002, 0.002, 0.002); //距离越大越模糊

    nColor+=texture2D(vTexture, vec2(vTextureCoord.x-vChangeColor.r, vTextureCoord.y-vChangeColor.r));
    nColor+=texture2D(vTexture, vec2(vTextureCoord.x-vChangeColor.r, vTextureCoord.y+vChangeColor.r));
    nColor+=texture2D(vTexture, vec2(vTextureCoord.x+vChangeColor.r, vTextureCoord.y-vChangeColor.r));
    nColor+=texture2D(vTexture, vec2(vTextureCoord.x+vChangeColor.r, vTextureCoord.y+vChangeColor.r));
    nColor+=texture2D(vTexture, vec2(vTextureCoord.x-vChangeColor.g, vTextureCoord.y-vChangeColor.g));
    nColor+=texture2D(vTexture, vec2(vTextureCoord.x-vChangeColor.g, vTextureCoord.y+vChangeColor.g));
    nColor+=texture2D(vTexture, vec2(vTextureCoord.x+vChangeColor.g, vTextureCoord.y-vChangeColor.g));
    nColor+=texture2D(vTexture, vec2(vTextureCoord.x+vChangeColor.g, vTextureCoord.y+vChangeColor.g));
    nColor+=texture2D(vTexture, vec2(vTextureCoord.x-vChangeColor.b, vTextureCoord.y-vChangeColor.b));
    nColor+=texture2D(vTexture, vec2(vTextureCoord.x-vChangeColor.b, vTextureCoord.y+vChangeColor.b));
    nColor+=texture2D(vTexture, vec2(vTextureCoord.x+vChangeColor.b, vTextureCoord.y-vChangeColor.b));
    nColor+=texture2D(vTexture, vec2(vTextureCoord.x+vChangeColor.b, vTextureCoord.y+vChangeColor.b));
    nColor/=13.0;
    gl_FragColor=nColor;
}