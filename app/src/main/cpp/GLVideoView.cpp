//
// Created by shike on 2/6/2020.
//

#include "GLVideoView.h"
#include "XTexture.h"
#include "XLog.h"

void GLVideoView::SetRender (void *win) {
    if (GLVideoView_DEBUG_LOG) {
        XLOGD("GLVideoView SetRender methods.");
    }
    view = win;
}

void GLVideoView::Close () {
    if (GLVideoView_DEBUG_LOG) {
        XLOGD("GLVideoView Close methods.");
    }
    mux.lock();
    if (txt) {
        txt->Drop();
        txt = 0;
    }
    mux.unlock();
}

void GLVideoView::Render(XData data) {
    if (GLVideoView_DEBUG_LOG) {
        XLOGD("GLVideoView Render methods.XData size is %d" ,data.size);
    }
    if (!view) {
        XLOGE("GLVideoView Render failed,no view.");
        return;
    }
    if (!txt) {
        txt = XTexture::Create();
        if (GLVideoView_DEBUG_LOG) {
            XLOGD("GLVideoView Render date format is %d" ,data.format);
        }
        txt->Init(view ,(XTextureType)data.format);
        if (GLVideoView_DEBUG_LOG) {
            XLOGD("GLVideoView Render create.");
        }
    }
    txt->Draw(data.datas , sizeof(data.datas) ,data.width ,data.height);
}