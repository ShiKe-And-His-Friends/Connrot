//
// Created by shike on 2/4/2020.
//

#ifndef CONNROT_IPLAYER_H
#define CONNROT_IPLAYER_H

#include <mutex>
#include "XThread.h"
#include "XParameter.h"

class IDemux;
class IAudioPlay;
class IVideoView;
class IResample;
class IDecode;

class IPlayer : public XThread{
public:
    static IPlayer *Get(unsigned char index = 0);
    virtual bool Open(const char *path);
    virtual void Close();

    virtual bool Start();
    virtual void InitView(void *win);

    virtual double PlayPos();
    virtual bool Seek(double pos);
    virtual void SetPause(bool isP);

    bool isHardDecode = false;
    int IPLAYER_DEBUG_LOG = 1;

    XParameter outPara;

    IDemux *demux = 0;
    IDecode *vdecode = 0;
    IDecode *adecode = 0;
    IResample *resample = 0;
    IVideoView *videoView = 0;
    IAudioPlay *audioPlay = 0;

protected:
    void Main();
    std::mutex mux;
    IPlayer(){}

};


#endif //CONNROT_IPLAYER_H
