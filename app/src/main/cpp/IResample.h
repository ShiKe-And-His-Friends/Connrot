//
// Created by shike on 2/5/2020.
//

#ifndef CONNROT_IRESAMPLE_H
#define CONNROT_IRESAMPLE_H

#include "XParameter.h"
#include "IObserver.h"

class IResample : public IObserver{
public:
    virtual bool Open(XParameter in ,XParameter out = XParameter()) = 0;
    virtual XData Resample(XData indata) = 0;
    virtual void Close() = 0;
    virtual void Update(XData data);
    int outChannels = 2;
    int outFormat = 1;
};


#endif //CONNROT_IRESAMPLE_H
