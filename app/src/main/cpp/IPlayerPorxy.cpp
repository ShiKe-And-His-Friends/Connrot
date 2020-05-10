//
// Created by shike on 2/5/2020.
//

#include "IPlayerPorxy.h"
#include "FFPlayerBuilder.h"
#include "XLog.h"

void IPlayerPorxy::Close () {
    if (IPlayerPorxy_DEBUG_LOG) {
        XLOGD("IPlayerPorxy Close methods.");
    }
    mux.lock();
    if (player) {
        player->Close();
    }
    mux.unlock();
}

void IPlayerPorxy::Init (void *vm) {
    if (IPlayerPorxy_DEBUG_LOG) {
        XLOGD("IPlayerPorxy Init methods.");
    }
    mux.lock();
    if (vm) {
        FFPlayerBuilder::InitHard(vm);
    }
    if (!player) {
        player = FFPlayerBuilder::Get()->BuilderPlayer();
    }
    if (!player) {
        player = FFPlayerBuilder::Get()->BuilderPlayer();
    }
    mux.unlock();
}

double IPlayerPorxy::PlayPos () {
    if (IPlayerPorxy_DEBUG_LOG) {
        XLOGD("IPlayerPorxy PlayPos methods.");
    }
    double pos = 0.0;
    mux.lock();
    if (player) {
        pos = player->PlayPos();
    }
    mux.unlock();
    return pos;
}

void IPlayerPorxy::SetPause(bool isP)
{
    if (IPlayerPorxy_DEBUG_LOG) {
        XLOGD("IPlayerPorxy SetPause methods.");
    }
    mux.lock();
    if(player)
        player->SetPause(isP);
    mux.unlock();
}

bool IPlayerPorxy::Seek (double pos) {
    if (IPlayerPorxy_DEBUG_LOG) {
        XLOGD("IPlayerPorxy Seek methods.");
    }
    bool re = false;
    mux.lock();
    if (player) {
        re = player->Seek(pos);
    }
    mux.unlock();
    return re;
}

bool IPlayerPorxy::Open (const char *path) {
    if (IPlayerPorxy_DEBUG_LOG) {
        XLOGD("IPlayerPorxy Open methods.");
    }
    XLOGI("IplayerPorxy is %s" ,path);
    bool re = false;
    mux.lock();
    if (player) {
        player->isHardDecode = isHardDecode;
        re = player->Open(path);
    }
    mux.unlock();
    return re;
}

bool IPlayerPorxy::Start() {
    if (IPlayerPorxy_DEBUG_LOG) {
        XLOGD("IPlayerPorxy Start methods.");
    }
    bool re = false;
    mux.lock();
    if (player) {
        XLOGD("shikeDebug player start...");
        re = player->Start();
    }
    mux.unlock();
    return re;
}

void IPlayerPorxy::InitView(void *win) {
    if (IPlayerPorxy_DEBUG_LOG) {
        XLOGD("IPlayerPorxy InitView methods.");
    }
    mux.lock();
    if (player) {
        player->InitView(win);
    }
    mux.unlock();
}