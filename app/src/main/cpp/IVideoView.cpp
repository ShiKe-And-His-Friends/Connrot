//
// Created by shike on 2/5/2020.
//

#include "IVideoView.h"
#include "XLog.h"

void IVideoView::Update (XData data) {
    this->Render(data);
}
