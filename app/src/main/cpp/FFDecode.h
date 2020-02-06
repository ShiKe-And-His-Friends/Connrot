//
// Created by shike on 2/6/2020.
//

#ifndef CONNROT_FFDECODE_H
#define CONNROT_FFDECODE_H

#include "XParameter.h"
#include "IDecode.h"

struct AVCodecContext;
struct AVFrame;

class FFDecode : public IDecode{
public:
    static void InitHard(void *vm);
    virtual bool Open(XParameter para ,bool isHard = false);
    virtual void Close();
    virtual bool SendPacket(XData pkt);
    virtual XData RecvFrame();

protected:
    AVCodecContext *codec = 0;
    AVFrame *frame = 0;
    std::mutex mux;
};


#endif //CONNROT_FFDECODE_H
