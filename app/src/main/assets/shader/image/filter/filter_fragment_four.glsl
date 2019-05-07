//四分镜
precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D vTexture;
void main() {
    //四分镜就是把整张图片缩成四份，然后分别放在左上角、右上角、左下角、右下角等地方。我们可以通过改变纹理坐标（x和y）得到
    //类似两分镜也是同理
    vec2 uv = vTextureCoord;
    if (uv.x <= 0.5) {
        uv.x = uv.x * 2.0;
    } else {
        uv.x = (uv.x - 0.5) * 2.0;
    }

    if (uv.y <= 0.5) {
        uv.y = uv.y * 2.0;
    } else {
        uv.y = (uv.y - 0.5) * 2.0;
    }
    gl_FragColor = texture2D(vTexture, uv);
}