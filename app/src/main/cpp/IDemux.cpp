//
// Created by shike on 2/6/2020.
//

#include "IDemux.h"
#include "XLog.h"

void IDemux::Main() {
    while (!isExit) {
        XData d = Read();
        if (d.size > 0) {
            Notify(d);
        }
    }
}