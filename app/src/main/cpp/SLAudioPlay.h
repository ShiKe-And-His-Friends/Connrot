//
// Created by shike on 2/5/2020.
//

#ifndef CONNROT_SLAUDIOPLAY_H
#define CONNROT_SLAUDIOPLAY_H

#include "IAudioPlay.h"

class SLAudioPlay : public IAudioPlay{
public:
    virtual bool StartPlay(XParameter out);
    virtual void Close();
    void PlayCall(void *bufq);

    SLAudioPlay();
    virtual ~SLAudioPlay();

protected:
    unsigned char *buf = 0;
    std::mutex mux;
};


#endif //CONNROT_SLAUDIOPLAY_H
