//
// Created by shike on 2/5/2020.
//

#ifndef CONNROT_XSHADER_H
#define CONNROT_XSHADER_H

#include "mutex"

enum XShaderType {
    XSHADER_YUV420P = 0,
    XSHADER_NV12 = 25,
    XSHADER_NV21 = 26
};

class XShader {
public:
    virtual bool Init (XShaderType type = XSHADER_YUV420P);
    virtual void Close ();

    virtual void GetTexture (unsigned int index ,int width ,int height ,unsigned char *buf , bool ise = false);
    virtual void Draw();

protected:
    int XShader_DEBUG_LOG = 1;

    unsigned int vsh = 0;
    unsigned int fsh = 0;
    unsigned int program = 0;
    unsigned int texts[100] = {0};
    std::mutex mux;
};


#endif //CONNROT_XSHADER_H
