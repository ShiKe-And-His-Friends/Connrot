//
// Created by shike on 2/6/2020.
//

#ifndef CONNROT_IAUDIOPLAY_H
#define CONNROT_IAUDIOPLAY_H

#include <list>
#include "IObserver.h"
#include "XParameter.h"

class IAudioPlay : public IObserver{
public:
    virtual void Update(XData data);
    virtual XData GetData();
    virtual bool StartPlay(XParameter out) = 0;
    virtual void Close() = 0;
    virtual void Clear();
    int maxFrame = 100;
    int pts = 0;

protected:
    std::list <XData> frames;
    std::mutex framesMutex;
};


#endif //CONNROT_IAUDIOPLAY_H
