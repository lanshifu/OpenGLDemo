precision mediump float;// 声明float类型的精度为中等(精度越高越耗资源)
varying vec2 vTextureCoord;//顶点着色器传过来的纹理坐标向量
uniform sampler2D vTexture;//纹理采样器，代表一副纹理

uniform int vChangeType;//【应用需要传类型过来，对不同类型做不同处理】
uniform vec3 vChangeColor;

uniform float uXY;//屏幕宽高比
varying vec4 vPosition; //顶点着色器把坐标传过来

void modifyColor(vec4 color){
    color.r=max(min(color.r, 1.0), 0.0);
    color.g=max(min(color.g, 1.0), 0.0);
    color.b=max(min(color.b, 1.0), 0.0);
    color.a=max(min(color.a, 1.0), 0.0);
}

void main() {
    vec4 nColor = texture2D(vTexture, vTextureCoord);//进行纹理采样,拿到当前颜色
    if (vChangeType==1){ //黑白处理，两种方式都可以
//        float c=nColor.r*vChangeColor.r+nColor.g*vChangeColor.g+nColor.b*vChangeColor.b;
//        gl_FragColor=vec4(c, c, c, nColor.a);
        float c=(nColor.r + nColor.g + nColor.b)/3.0;
        gl_FragColor=vec4(c, c, c, nColor.a);
    } else if (vChangeType==2){ //简单色彩处理，冷暖色调、增加亮度、降低亮度等
        //颜色相加，冷就是多加点蓝，暖就是多加点红跟绿
        vec4 deltaColor=nColor + vec4(vChangeColor, 0.0);
//        modifyColor(deltaColor);
        gl_FragColor=deltaColor;
    } else if (vChangeType==3){ //模糊处理，取与当前颜色相近的周边12个点的颜色，加上本身的颜色，取平均值
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
    } else if (vChangeType==4){ //放大镜效果,
        //到中心点距离
        float dis=distance(vec2(vPosition.x, vPosition.y), vec2(0, 0));
        //距离在这个区块内放大，改变纹理坐标，为什么+0.25？
        if (dis<vChangeColor.b){
            nColor=texture2D(vTexture, vec2(vTextureCoord.x/2.0+0.25, vTextureCoord.y/2.0+0.25));
        }
        gl_FragColor=nColor;
    } else if (vChangeType==5){ //四分镜就是把整张图片缩成四份，然后分别放在左上角、右上角、左下角、右下角等地方。我们可以通过改变纹理坐标得到
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
        gl_FragColor = texture2D(vTexture, fract(uv));//fract(x): 取小数部分
    } else {
        gl_FragColor=nColor;//不处理
    }
}