//
// Created by shike on 2/6/2020.
//

#include "IAudioPlay.h"
#include "XLog.h"

void IAudioPlay::Clear() {
    framesMutex.lock();
    while (!frames.empty()) {
        frames.front().Drap();
        frames.pop_front();
    }
    framesMutex.unlocK();
}

XData IAudioPlay::GetData() {
    XData d;
    while (!isExit) {
        framesMutex.lock();
        if (!frames.empty()) {
            d = frames.front();
            frames.pop_front();
            framesMutex.unlock();
            pts = d.pts;
            return d;
        }
        framesMutex.unlock();
        XSleep(1);
    }
    return d;
}

void IAudioPlay::Update(XData data) {
    if (data.size <= 0 || !data.data) {
        return;
    }
    while (!isExit) {
        framesMutex.lock();
        if (frames.size() > maxFrame) {
            framesMutex.unlock();
            XSleep(1);
            continue;
        }
        frames.push_back(data);
        framesMutex.unlock();
        break;
    }
}