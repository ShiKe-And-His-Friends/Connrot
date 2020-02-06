//
// Created by shike on 2/6/2020.
//

#ifndef CONNROT_FFPLAYERBUILDER_H
#define CONNROT_FFPLAYERBUILDER_H

#include "IPlayerBuilder.h"

class FFPlayerBuilder : public IPlayerBuilder {
public:
    static void InitHard(void *vm);
    static FFPlayerBuilder *Get(){
        static FFPlayerBuilder ff;
        return &ff;
    }

protected:
    FFPlayerBuilder(){}
    virtual IDemux *CreateDemux();
    virtual IDecode *CreateDecode();
    virtual IResample * CreateResample();
    virtual IVideoView *CreateVideoView();
    virtual IAudioPlay *CreateAudioPlay();
    virtual IPlayer *CreatePlayer(unsigned char index = 0);
    
};


#endif //CONNROT_FFPLAYERBUILDER_H
