//
// Created by shike on 2/4/2020.
//

#include "XTexture.h"
#include "XLog.h"
#include "XEGL.h"
#include "XShader.h"

class CXTexture:public XTexture{
public:
    XShader sh;
    XTextureType type;
    std::mutex mux;
    virtual void Drop(){
        if (CXTexture_DEBUG_LOG) {
            XLOGD("CXTexture Drop methods.");
        }
        mux.lock();
        XEGL::Get()->Close();
        sh.Close();
        mux.unlock();
        delete this;
    }

    virtual bool Init(void *win , XTextureType type){
        if (CXTexture_DEBUG_LOG) {
            XLOGD("CXTexture Init methods.");
        }
        mux.lock();
        XEGL::Get()->Close();
        sh.Close();
        this->type = type;
        if (!win) {
            mux.unlock();
            XLOGE("XTexture Init Failed win is null.");
            return false;
        }
        if (!XEGL::Get()->Init(win)) {
            mux.unlock();
            XLOGE("XTexture Init Failed win init Fail.");
            return false;
        }
        sh.Init((XShaderType)type);
        mux.unlock();
        if (CXTexture_DEBUG_LOG) {
            XLOGD("CXTexture Init success.");
        }
        return true;
    }

    virtual void Draw(unsigned char *data[] , int width , int height){
        if (CXTexture_DEBUG_LOG) {
            XLOGD("CXTexture Draw methods.");
        }
        mux.lock();
        sh.GetTexture(0 ,width ,height ,data[0]);  // Y
        if (type == XTEXTURETYPE_YUV420P) {
            sh.GetTexture(1,width/2,height/2,data[1]);  // U
            sh.GetTexture(2,width/2,height/2,data[2]);  // V
        } else {
            sh.GetTexture(1 , width/2 , height/2 , data[1] , true);  //UV
        }
        sh.Draw();
        XEGL::Get()->Draw();
        mux.unlock();
        if (CXTexture_DEBUG_LOG) {
            XLOGD("CXTexture Draw success.");
        }
    }

};

XTexture *XTexture::Create () {
    XLOGD("CXTexture Create.");
    return new CXTexture();
}