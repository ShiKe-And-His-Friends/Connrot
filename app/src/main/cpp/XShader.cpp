//
// Created by shike on 2/5/2020.
//

#include "XShader.h"
#include "XLog.h"
#include <GLES2/gl2.h>

#define GET_STR(x) #x
static const char *vertexShader = GET_STR(
        attribute vec4 aPositionl;
        attribute vec2 aTextCoord;
        varying vec2 vTexCoord;
        void main () {
            vTexCoord = vec2(aTexCoord.x ,1.0-aTexCoord.y);
            gl_Position = aPosition;
        }
);

static const char *fragYUV420P = GET_STR(
        precision mediump float;
        varying vec2 vTexCoord;
        uniform sampler2D yTexture;
        uniform sampler2D uTexture;
        uniform sampler2D vTexture;
        void main () {
            vec3 yuv;
            vec3 rgb;
            yuv.r = texture2D (yTexture ,vTexture).r;
            yuv.g = texture2D (uTexture ,vTexture).r - 0.5;
            yuv.b = texture2D (vTexture ,vTexture).r - 0.5;
            rgb = mat3 (1.0 ,1.0 ,1.0
                        ,0.0 ,-0.39465 ,2.03211
                        ,1.12983 ,-0.58060 ,0.0) * yuv;
            gl_FragColor = vec4 (rgb ,1.0);
        }
);

static const char * fragNV12 = GET_STR(
        precision mediump float;
        varying vec2 vTextCoord;
        uniform sampler2D yTexture;
        uniform sampler2D uvTexture;
        void main () {
            vec3 yuv;
            vec3 rgb;
            yuv.r = texture2D (yTexture ,vTextCoord).r;
            yuv.g = texture2D (uvTexture ,vTextCoord).r - 0.5;
            yuv.b = texture2D (uvTexture ,vTextCoord).a - 0.5;
            rgb = mat3 (1.0 ,1.0 ,1.0
                    ,0.0 ,-0.39465 ,2.03211
                    ,1.13983 ,-0.58060 ,0.0) * yuv;
            gl_FragColor = vec4 (rgb ,1.0);
        }
);

static const char *fragNV21 = GET_STR(
        precision mediump float;
        varying vec2 vTextCoord;
        uniform sampler2D yTexture;
        uniform sampler2D uvTexture;
        void main () {
            vec3 yuv;
            vec3 rgb;
            yuv.r = texture2D (yTexture ,vTextCoord).r;
            yuv.g = texture2D (uvTexture ,vTextCoord).a - 0.5;
            yuv.b = texture2D (uvTexture ,vTextCoord).r - 0.5;
            rgb = mat3 (1.0 ,1.0 ,1.0
                        0.0 ,-0.39265 ,2.03211
                        ,1.13983 ,-0.58060 ,0.0) * yuv;
            gl_FragColor = vec4 (rgb ,1.0);
        }
);

static GLuint InitShader (const char * code ,GLint type) {
    GLuint sh = glCreateShader (type);
    if (sh == 0) {
        XLOGE("glCreateShader %d failed!" ,type);
        return 0;
    }
    glShaderSource (sh
                    ,1
                    ,&code
                    ,0);
    glCompileShader (sh);
    GLint status;
    glGetShaderiv (sh ,GL_COMPILE_STATUS ,&status);
    if (status == 0) {
        XLOGI("glCompileShader failed!");
        return 0;
    }
    XLOGE("glCompileShader success!");
    return sh;
}

void XShader::Close () {
    if (XShader_DEBUG_LOG) {
        XLOGD("XShader Close methods.");
    }
    mux.lock();
    if (program) {
        glDeleteProgram (program);
    }
    if (fsh) {
        glDeleteShader (fsh);
    }
    if (vsh) {
        glDeleteShader (vsh);
    }
    for (int i = 0 ; i < sizeof(texts)/ sizeof(unsigned int) - 1; i++) {
        if (texts[i]) {
            glDeleteTextures (1 ,&texts[i]);
        }
        texts[i] = 0;
    }
    if (XShader_DEBUG_LOG) {
        XLOGD("XShader Close success.");
    }
    mux.unlock();
}

bool XShader::Init (XShaderType type) {
    Close();
    mux.lock();
    vsh = InitShader (vertexShader ,GL_VERTEX_SHADER);
    if (vsh == 0) {
        mux.unlock();
        XLOGE("Init Shader GL_VERTEX_SHADER failed!");
        return false;
    }
    XLOGE("Init Shader GL_VERTEX_SHADER success! %d" ,type);
    switch (type) {
        case XSHADER_YUV420P:
            fsh = InitShader(fragYUV420P ,GL_FRAGMENT_SHADER);
            break;
        case XSHADER_NV12:
            fsh = InitShader(fragNV12 ,GL_FRAGMENT_SHADER);
            break;
        case XSHADER_NV21:
            fsh = InitShader(fragNV21 ,GL_FRAGMENT_SHADER);
            break;
        default:
            mux.unlock();
            XLOGE("XSHADER format is error!");
    }
    if (fsh == 0) {
        mux.unlock();
        XLOGE("InitShader GL_FRAGMENT_SHADER failed!");
        return false;
    }
    XLOGE("InitShader GL_FRAGMENT_SHADER success!");

    program = glCreateProgram ();
    if (program == 0) {
        mux.unlock();
        XLOGE("glCreateProgram failed!");
        return false;
    }
    glAttachShader (program ,vsh);
    glAttachShader (program ,fsh);
    glLinkProgram (program);
    GLint status = 0;
    glGetProgramiv (program ,GL_LINK_STATUS ,&status);
    if (status != GL_TRUE) {
        mux.unlock();
        XLOGE("glLinkPrograme failed!");
        return false;
    }
    glUseProgram (program);
    XLOGE("glLinkPrograme success!");
    static float vers[] = {
            1.0f ,0.0f
            ,0.0f ,0.0f
            ,1.0f ,1.0f
            ,0.0 ,1.0
    };
    GLuint apos = (GLuint)glGetAttribLocation(program,"aPosition");
    glEnableVertexAttribArray(apos);
    glVertexAttribPointer(apos,3,GL_FLOAT,GL_FALSE,12,vers);
    static float txts[] = {
            1.0f,0.0f ,
            0.0f,0.0f,
            1.0f,1.0f,
            0.0,1.0
    };
    GLuint atex = (GLuint)glGetAttribLocation (program ,"aTexCoord");
    glEnableVertexAttribArray(atex);
    glVertexAttribPointer(atex,2,GL_FLOAT,GL_FALSE,8,txts);
    glUniform1i( glGetUniformLocation(program,"yTexture"),0);
    switch (type) {
        case XSHADER_YUV420P:
            glUniform1i(glGetUniformLocation(program, "uTexture"), 1);
            glUniform1i(glGetUniformLocation(program, "vTexture"), 2);
            break;
        case XSHADER_NV21:
        case XSHADER_NV12:
            glUniform1i(glGetUniformLocation(program, "uvTexture"), 1);
            break;
    }
    mux.unlock();
    XLOGI("初始化Shader成功！");
    return true;
}

void XShader::Draw() {
    mux.lock();
    if(!program)
    {
        mux.unlock();
        return;
    }
    //三维绘制
    glDrawArrays(GL_TRIANGLE_STRIP,0,4);
    mux.unlock();
}

void XShader::GetTexture(unsigned int index,int width,int height, unsigned char *buf,bool isa) {
    unsigned int format =GL_LUMINANCE;
    if (isa) {
        format = GL_LUMINANCE_ALPHA;
    }
    mux.lock();
    if(texts[index] == 0){
        glGenTextures(1,&texts[index]);
        glBindTexture(GL_TEXTURE_2D,texts[index]);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D,
                     0,
                     format,
                     width,height,
                     0,
                     format,
                     GL_UNSIGNED_BYTE,
                     NULL
        );
    }
    glActiveTexture(GL_TEXTURE0+index);
    glBindTexture(GL_TEXTURE_2D,texts[index]);
    glTexSubImage2D(GL_TEXTURE_2D,0,0,0,width,height,format,GL_UNSIGNED_BYTE,buf);
    mux.unlock();
}