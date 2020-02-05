//
// Created by shike on 2/5/2020.
//

#ifndef CONNROT_IVIDEOVIEW_H
#define CONNROT_IVIDEOVIEW_H

#include "XData.h"
#include "IObserver.h"

class IVideoView {
public:
    virtual void SetRender(void *win) = 0;
    virtual void Render(XData data) = 0;
    virtual void Update(XData data);
    virtual void Close() = 0;
};


#endif //CONNROT_IVIDEOVIEW_H
