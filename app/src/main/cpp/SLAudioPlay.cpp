//
// Created by shike on 2/5/2020.
//

#include "SLAudioPlay.h"
#include "XLog.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

static SLObjectItf enginSL = NULL;
static SLEngineItf eng = NULL;
static SLObjectItf mix = NULL;
static SLObjectItf player = NULL;
static SLPlayItf iplayer = NULL;
static SLAndroidSimpleBufferQueueItf pcmQue = NULL;

SLAudioPlay::SLAudioPlay () {
    buf = new unsigned char[1024 * 1024];
}

SLAudioPlay::~SLAudioPlay () {
    delete buf;
    buf = 0;
}

static SLEngineItf CreateSL () {
    SLresult re;
    SLEngineItf en;
    re = slCreateEngine(&enginSL, 0, 0, 0, 0, 0);
    if (re != SL_RESULT_SUCCESS) {
        return NULL;
    }
    re = (*enginSL)->Realize(enginSL, SL_BOOLEAN_FALSE);
    if (re != SL_RESULT_SUCCESS) {
        return NULL;
    }
    re = (*enginSL)->GetInterface(enginSL ,SL_IID_ENGINE ,&en);
    if (re != SL_RESULT_SUCCESS) {
        return NULL;
    }
    return en;
}

void SLAudioPlay::PlayCall(void *bufq) {
    if (!bufq) {
        return;
    }
    SLAndroidSimpleBufferQueueItf bf = (SLAndroidSimpleBufferQueueItf)bufq;
    XData d = GetData();
    if (d.size <= 0) {
        XLOGI("GetData() size is 0.");
        return;
    }
    if (!buf) {
        return;
    }
    memcpy(buf ,d.data ,d.size);
    mux.lock();
    if (pcmQue && (*pcmQue)) {
        (*pcmQue)->Enqueue(pcmQue ,buf ,d.size);
    }
    mux.unlock();
    d.Drop();
}

static void PcmCall (SLAndroidSimpleBufferQueueItf bf ,void *contex) {
    SLAudioPlay *ap = (SLAudioPlay *)contex;
    if (!ap) {
        XLOGI("PcmCall failed contex is null.");
        return;
    }
    ap->PlayCall((void *)bf);
}

void SLAudioPlay::Close() {
    IAudioPlay::Clear();
    mux.lock();
    if (iplayer && (*iplayer)) {
        (*iplayer)->SetPlayState(iplayer ,SL_PLAYSTATE_STOPPED);
    }
    if (pcmQue && (*pcmQue)) {
        (*pcmQue)->Clear(pcmQue);
    }
    if (player && (*player)) {
        (*player)->Destroy(player);
    }
    if (mix && (*mix)) {
        (*mix)->Destroy(mix);
    }
    if (enginSL && (*enginSL)) {
        (*enginSL)->Destroy(enginSL);
    }
    enginSL = NULL;
    eng = NULL;
    mix = NULL;
    player = NULL;
    iplayer = NULL;
    pcmQue = NULL;
    mux.unlock();
}

bool SLAudioPlay::StartPlay(XParameter out) {
    Close();
    mux.lock();
    eng = CreateSL();
    if (eng) {
        XLOGI("CreateSL success.");
    } else {
        mux.unlock();
        XLOGE("CreateSL failed.");
        return false;
    }
    SLresult re = 0;
    re = (*eng)->CreateOutputMix(eng ,&mix ,0 ,0 ,0);
    if (re != SL_RESULT_SUCCESS) {
        mux.unlock();
        XLOGI("(*mix)->Realize failed!");
        return false;
    }
    SLDataLocator_OutputMix outMix = {SL_DATALOCATOR_OUTPUTMIX ,mix};
    SLDataSink audioSink = {&outMix ,0};
    SLDataLocator_AndroidSimpleBufferQueue que = {SL_DATALOCATOR_ANDROIDBUFFERQUEUE ,10};
    SLDataFormat_PCM pcm = {
            SL_DATAFORMAT_PCM,
            (SLuint32) out.channels,
            (SLuint32) out.sample_rate * 1000,
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
            SL_BYTEORDER_LITTLEENDIAN
    };
    SLDataSource ds = {&que ,&pcm};

    const SLInterfaceID ids[] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[] = {SL_BOOLEAN_TRUE};
    re = (*eng)->CreateAudioPlayer(eng ,&player ,&ds ,&audioSink , sizeof(ids)/ sizeof(SLInterfaceID) ,ids ,req);
    if (re != SL_RESULT_SUCCESS) {
        mux.unlock();
        XLOGI("CreateAudioPlay failed!");
        return false;
    } else {
        XLOGI("CreateAudioPlay success!");
    }
    (*player)-> Realize(player ,SL_BOOLEAN_FALSE);
    re = (*player)->GetInterface(player ,SL_IID_PLAY ,&iplayer);
    if (re != SL_RESULT_SUCCESS) {
        mux.unlock();
        XLOGI("GetInterface SL_IID_PLAY failed!");
        return false;
    }
    re = (*player)->GetInterface(player ,SL_IID_BUFFERQUEUE ,&pcmQue);
    if (re != SL_RESULT_SUCCESS) {
        mux.unlock();
        XLOGI("GetInterface SL_IID_BUFFERQUEUE failed.");
        return false;
    }
    (*pcmQue)->RegisterCallback(pcmQue ,PcmCall ,this);
    (*iplayer)->SetPlayState(iplayer ,SL_PLAYSTATE_PLAYING);
    (*pcmQue)->Enqueue(pcmQue ,"" ,1);
    mux.unlock();
    XLOGE("SLAudioPlay::StartPlay success!");
    return true;
}