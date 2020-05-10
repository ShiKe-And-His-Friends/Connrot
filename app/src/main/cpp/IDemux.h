//
// Created by shike on 2/6/2020.
//

#ifndef CONNROT_IDEMUX_H
#define CONNROT_IDEMUX_H

#include "XData.h"
#include "XThread.h"
#include "IObserver.h"
#include "XParameter.h"

class IDemux: public IObserver {
public:
    virtual bool Open(const char *url) = 0;
    virtual bool Seek(double pos) = 0;
    virtual void Close() = 0;
    virtual XParameter GetVPara() = 0;
    virtual XParameter GetAPara() = 0;
    virtual XData Read() = 0;
    int totalMs = 0;

protected:
    int IDemux_DEBUG_LOG = 1;
    virtual void Main();
};


#endif //CONNROT_IDEMUX_H
