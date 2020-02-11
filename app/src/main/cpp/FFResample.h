//
// Created by shike on 2/6/2020.
//

#ifndef CONNROT_FFRESAMPLE_H
#define CONNROT_FFRESAMPLE_H

#include "IResample.h"

struct SwrContext;

class FFResample : public IResample{
public:
    virtual bool Open(XParameter in ,XParameter out = XParameter());
    virtual void Close();
    virtual XData Resample(XData indata);

protected:
    SwrContext *actx = 0;
    std::mutex mux;
};


#endif //CONNROT_FFRESAMPLE_H
