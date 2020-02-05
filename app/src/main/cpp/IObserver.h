//
// Created by shike on 2/5/2020.
//

#ifndef CONNROT_IOBSERVER_H
#define CONNROT_IOBSERVER_H

#include "XData.h"
#include "XThread.h"
#include <mutex>
#include <vector>

class IObserver :public XThread{
public:
    virtual void Update(XData data) {}
    void AddObs(IObserver *obs);
    void Notify(XData data);
protected:
    std::vector<IObserver *>obss;
    std::mutex mux;
};


#endif //CONNROT_IOBSERVER_H
