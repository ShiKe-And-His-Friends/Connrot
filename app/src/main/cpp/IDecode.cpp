//
// Created by shike on 2/6/2020.
//

#include "IDecode.h"
#include "XLog.h"

void IDecode::Update (XData pkt) {
    if (IDecode_DEBUG_LOG) {
        XLOGI("IDecode Update methods. XData type is %d ,size is %d" ,pkt.isAudio ,pkt.size);
    }
    if (pkt.isAudio != isAudio) {
        return;
    }
    while (!isExit) {
        packsMutex.lock();
        if (packs.size() < maxList) {
            if (IDecode_DEBUG_LOG) {
                XLOGI("IDecode stack push size is %d" ,pkt.size);
            }
            packs.push_back(pkt);
            packsMutex.unlock();
            break;
        }
        packsMutex.unlock();
        XSleep(1);
    }
}

void IDecode::Clear () {
    if (IDecode_DEBUG_LOG) {
        XLOGI("IDecode Clear methods.");
    }
    packsMutex.lock();
    while (!packs.empty()) {
        packs.front().Drop();
        packs.pop_front();
        if (IDecode_DEBUG_LOG) {
            XLOGI("IDecode stack pop a data.");
        }
    }
    pts = 0;
    synPts = 0;
    packsMutex.unlock();
}

void IDecode::Main () {
    if (IDecode_DEBUG_LOG) {
        XLOGI("IDecode Main methods.");
    }
    while (!isExit) {
        packsMutex.lock();
        if (!isAudio && synPts > 0) {
            if (synPts <pts) {
                packsMutex.unlock();
                XSleep(1);
                continue;
            }
        }
        if (packs.empty()) {
            packsMutex.unlock();
            XSleep(1);
            continue;
        }
        XData pack = packs.front();
        packs.pop_front();

        if (this->SendPacket(pack)) {
            while (!isExit) {
                XData frame = RecvFrame();
                if (!frame.data) {
                    break;
                }
                pts = frame.pts;
                if (IDecode_DEBUG_LOG) {
                    XLOGI("IDecode notify a data. XData size is %d ",frame.size);
                }
                this->Notify(frame);
                if (IDecode_DEBUG_LOG) {
                    XLOGI("IDecode notify a data.");
                }
            }
        }
        pack.Drop();
        packsMutex.unlock();
    }
}
