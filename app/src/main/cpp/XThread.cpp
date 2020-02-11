//
// Created by shike on 2/4/2020.
//

#include "XThread.h"
#include "XLog.h"
#include "thread"

using namespace std;

void XSleep (int mis) {
    chrono::milliseconds du(mis);
    this_thread::sleep_for(du);
}

void XThread::SetPause(bool isP)
{
    isPause = isP;
    //等待100毫秒
    for(int i = 0; i < 10; i++)
    {
        if(isPausing == isP)
        {
            break;
        }
        XSleep(10);
    }

}

bool XThread::Start() {
    isExit = false;
    thread th(&XThread::ThreadMain,this);
    th.detach();
    return true;
}

void XThread::ThreadMain() {
    isRunning = true;
    XLOGI("Main Thread in.");
    Main();
    XLOGI("Main Thread out.");
    isRunning = false;
}

void XThread::Stop() {
    XLOGI("Main Thread Stop Start.");
    isExit = true;
    for (int i = 0 ; i < 200 ; i++) {
        if (!isRunning)
        {
            XLOGE("Main Thread Stop Sucess.");
            return;
        }
        XSleep(1);
    }
    XLOGI("Main Thread Stop Time out.");
}