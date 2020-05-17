//
// Created by shike on 2/6/2020.
//

#ifndef CONNROT_FFDEMUX_H
#define CONNROT_FFDEMUX_H

#include "IDemux.h"

struct AVFormatContext;

class FFDemux : public IDemux {
public:
    virtual bool Open(const char *url);
    virtual bool Seek(double pos);
    virtual void Close();
    virtual XParameter GetVPara();
    virtual XParameter GetAPara();
    virtual XData Read();
    FFDemux();

private:
    int DEBUG = 1;
    AVFormatContext *ic = 0;
    std::mutex mux;
    int audioStream = 1;
    int videoStream = 0;
};


#endif //CONNROT_FFDEMUX_H
