//
// Created by shike on 2/4/2020.
//

#include <libavutil/error.h>
#include "IPlayer.h"
#include "XLog.h"
#include "IAudioPlay.h"
#include "IDecode.h"
#include "IDemux.h"
#include "IResample.h"
#include "IVideoView.h"

IPlayer *IPlayer::Get(unsigned char index) {
    static IPlayer p[256];
    return &p[index];
}

void IPlayer::Main() {
    while (!isExit) {
        mux.lock();
        if (!audioPlay || !vdecode) {
            mux.unlock();
            XSleep(2);
            continue;
        }
        int apts = audioPlay->pts;
        vdecode->synPts = apts;

         mux.unlock();
         XSleep(2);
    }
}

void IPlayer::Close()
{
    mux.lock();
    XThread::Stop();
    if (demux) {
        demux->Stop();
    }
    if (vdecode) {
        vdecode->Stop();
    }
    if (adecode) {
        adecode->Stop();
    }

    if (vdecode) {
        vdecode->Clear();
    }
    if (adecode) {
        adecode->Clear();
    }
    if (audioPlay) {
        audioPlay->Clear();
    }

    if (audioPlay) {
        audioPlay->Close();
    }
    if (videoView) {
        videoView->Close();
    }
    if (vdecode) {
        vdecode->Close();
    }
    if (adecode) {
        adecode->Close();
    }
    if (demux) {
        demux->Close();
    }
}

void IPlayer::SetPause(bool isP)
{
    mux.lock();
    XThread::SetPause(isP);
    if(demux)
        demux->SetPause(isP);
    if(vdecode)
        vdecode->SetPause(isP);
    if(adecode)
        adecode->SetPause(isP);
    if(audioPlay)
        audioPlay->SetPause(isP);
    mux.unlock();
}

double IPlayer::PlayPos() 
{
    double pos = 0.0;
    mux.lock();
    int total = 0;
    if (demux) {
        total = demux->totalMs;
    }
    if (total > 0) {
        if (vdecode){
            pos = (double)vdecode->pts/(double)total;
        }
    }
    mux.unlock();
    return pos;
}

bool IPlayer::Seek(double pos) {
    bool re = false;
    mux.lock();
    if (demux) {
        re = demux->Seek(pos);
    }
    mux.unlock();
    return re;
}

bool IPlayer::Open(const char *path) {
    //Close();  //Debug for process go
    mux.lock();
    if (!demux || !demux->Open(path)) {
        mux.unlock();
        XLOGD("demux->Open %s failed!",path);
        return false;
    }
    if (!vdecode || !vdecode->Open(demux->GetVPara(),isHardDecode)) {
        XLOGE("vdecode->Open %s failed!",path);
    }
    if (!adecode || !adecode->Open(demux->GetAPara(),path)) {
        XLOGE("adecode->Open %s failed!",path);
    }
    //if (outPara.sample_rate <= 0)
    outPara = demux->GetAPara();
    if (!resample || !resample->Open(demux->GetAPara(),outPara)) {
        XLOGE("resmaple->Open %s failed!",path);
    }
    mux.unlock();
    return true;
}

bool IPlayer::Start(){
    mux.lock();
    if(vdecode){
        vdecode->Start();
    }

    if (!demux || !demux->Start()) {
        mux.unlock();
    }
    return true;
}

void IPlayer::InitView(void *win) {
    if (videoView) {
        videoView->Close();
        videoView->SetRender(win);
    }
}