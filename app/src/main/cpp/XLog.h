//
// Created by shike on 2/4/2020.
//

#ifndef CONNROT_XLOG_H
#define CONNROT_XLOG_H


class XLog {

};

#ifdef ANDROID
#include <android/log.h>
#define XLOGD(...) __android_log_print(ANDROID_LOG_DEBUG,"connort-jni",__VA_ARGS__)
#define XLOGI(...) __android_log_print(ANDROID_LOG_INFO,"connort-jni",__VA_ARGS__)
#define XLOGE(...) __android_log_print(ANDROID_LOG_ERROR,"connort-jni",__VA_ARGS__)
#else
#define XLOGD(...) printf("connort-jni",__VA_ARGS__)
#define XLOGI(...) printf("connort-jni",__VA_ARGS__)
#define XLOGE(...) printf("connort-jni",__VA_ARGS__)

#endif

#endif //CONNROT_XLOG_H
