//
// Created by shike on 2/4/2020.
//

#ifndef CONNROT_XTEXTURE_H
#define CONNROT_XTEXTURE_H

enum XTextureType{
    XTEXTURETYPE_YUV420P = 0,
    XTEXTURETYPE_NV12 = 25,
    XTEXTURETYPE_NV21 = 26
};

class XTexture {
public:
    static XTexture *Create();
    virtual bool Init(void *win ,XTextureType type = XTEXTURETYPE_YUV420P) = 0;
    virtual void Draw(unsigned char *data[] , int width , int height) = 0;
    virtual void Drop() = 0;
    virtual ~XTexture(){};

protected:
    int CXTexture_DEBUG_LOG = 1;
    int XTexture_DEBUG_LOG = 1;
    XTexture(){};
};

#endif //CONNROT_XTEXTURE_H
