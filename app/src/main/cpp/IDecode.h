//
// Created by shike on 2/6/2020.
//

#ifndef CONNROT_IDECODE_H
#define CONNROT_IDECODE_H

#include "XParameter.h"
#include "IObserver.h"
#include <list>

class IDecode : public IObserver{
public:
    virtual bool Open(XParameter para ,bool isHard = false) = 0;
    virtual void Close() = 0;
    virtual void Clear();
    virtual bool SendPacket(XData pkt) = 0;
    virtual XData RecvFrame() = 0;
    virtual void Update(XData pkt);
    bool isAudio = false;
    int maxList = 100;
    int synPts = 0;
    int pts = 0;

protected:
    int IDecode_DEBUG_LOG = 1;
    virtual void Main();
    std::list<XData> packs;
    std::mutex packsMutex;
};


#endif //CONNROT_IDECODE_H
