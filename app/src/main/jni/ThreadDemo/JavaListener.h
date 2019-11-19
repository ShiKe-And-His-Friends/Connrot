//
// Created by yangw on 2018-2-25.
//

#include "jni.h"

#ifndef JNITHREAD_JAVALISTENER_H
#define JNITHREAD_JAVALISTENER_H


class JavaListener {

public:
    JavaVM *jvm;
    _JNIEnv *jenv;
    jobject jobj;
    jmethodID jmid;
public:
    JavaListener(JavaVM *vm, _JNIEnv *env, jobject obj);
    ~JavaListener();

    /**
     * 1:主线程
     * 0：子线程
     * @param type
     * @param code
     * @param msg
     */
    void onError(int type, int code, const char *msg);


};


#endif //JNITHREAD_JAVALISTENER_H
