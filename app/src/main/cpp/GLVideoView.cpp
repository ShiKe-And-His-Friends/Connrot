//
// Created by shike on 2/6/2020.
//

#include "GLVideoView.h"
#include "XTexture.h"
#include "XLog.h"

void GLVideoView::SetRender (void *win) {
    view = win;
}

void GLVideoView::Close () {
    mux.lock();
    if (txt) {
        txt->Drop();
        txt = 0;
    }
    mux.unlock();
}

void GLVideoView::Render(XData data) {
    if (!view) {
        return;
    }
    if (!txt) {
        txt = XTexture::Create();
        txt->Init(view ,(XTextureType)data ,format);
    }
    txt->Draw(data.data ,data.width ,data.height);
}