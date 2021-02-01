//
// Created by shike on 2/6/2020.
//

#include "IDemux.h"
#include "XLog.h"

void IDemux::Main() {
    if (IDemux_DEBUG_LOG) {
        XLOGD("IDemux main methods.");
    }
    while (!isExit) {
        if(IsPause()){
            XSleep(2);
            continue;
        }
        XData d = Read();
        if (IDemux_DEBUG_LOG) {
            XLOGD("IDemux read size is %d" ,d.size);
        }
        if (d.size > 0) {
            Notify(d);
        } else {
            XSleep(2);
        }
    }
}