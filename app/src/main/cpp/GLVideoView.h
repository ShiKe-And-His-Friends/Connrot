//
// Created by shike on 2/6/2020.
//

#ifndef CONNROT_GLVIDEOVIEW_H
#define CONNROT_GLVIDEOVIEW_H

#include "XData.h"
#include "IVideoView.h"

class XTexture;
class GLVideoView :public IVideoView{
public:
    virtual void SetRender(void *win);
    virtual void Render(XData data);
    virtual void Close();

protected:
    void *view = 0;
    XTexture *txt = 0;
    std::mutex mux;
};

#endif //CONNROT_GLVIDEOVIEW_H
