//
// Created by shike on 2/5/2020.
//

#ifndef CONNROT_IPLAYERPORXY_H
#define CONNROT_IPLAYERPORXY_H

#include "IPlayer.h"
#include <mutex>

class IPlayerPorxy: public IPlayer {
public:
    static IPlayerPorxy *Get() {
        static IPlayerPorxy px;
        return &px;
    }
    void Init(void *vm = 0);

    virtual bool Open(const char *path);
    virtual bool Seek(double ps);
    virtual void Close();
    virtual bool Start();
    virtual void InitView(void *win);

    virtual double PlayPos();

protected:
    IPlayerPorxy(){}
    IPlayer *player = 0;
    std::mutex mux;
};


#endif //CONNROT_IPLAYERPORXY_H
