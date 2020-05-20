//
// Created by shike on 2/5/2020.
//

#include "IPlayerBuilder.h"
#include "IVideoView.h"
#include "IResample.h"
#include "IDecode.h"
#include "IAudioPlay.h"
#include "IDemux.h"

IPlayer *IPlayerBuilder::BuilderPlayer(unsigned char index) {
    IPlayer *play = CreatePlayer(index);
    IDemux *de = CreateDemux();
    IDecode *vdecode = CreateDecode();
    IDecode *adecode = CreateDecode();

    de->AddObs(vdecode);
    de->AddObs(adecode);
    IVideoView *view = CreateVideoView();
    vdecode->AddObs(view);
    IResample *resample = CreateResample();
    adecode->AddObs(resample);
    IAudioPlay *audioPlay = CreateAudioPlay();
    resample->AddObs(audioPlay);

    play->demux = de;
    play->adecode = adecode;
    play->vdecode = vdecode;
    play->videoView = view;
    play->resample = resample;
    play->audioPlay = audioPlay;

    return play;
}
