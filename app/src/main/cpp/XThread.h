//
// Created by shike on 2/4/2020.
//

#ifndef CONNROT_XTHREAD_H
#define CONNROT_XTHREAD_H

void XSleep(int mis);

class XThread {
public:
    virtual bool Start();
    virtual void Stop();
    virtual void SetPause(bool isP);
    virtual bool IsPause()
    {
        isPausing = isPause;
        return isPause;
    }
    virtual void Main(){}

protected:
    bool isExit = false;
    bool isRunning = false;
    bool isPause = false;
    bool isPausing = false;

private:
    void ThreadMain();
};


#endif //CONNROT_XTHREAD_H
