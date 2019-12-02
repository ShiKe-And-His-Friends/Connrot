// Created by yangw on 2018-2-24.
#include "../../jni/com_example_ShikeApplication_ndkdemo_ndktool.h"
#ifndef JNITHREAD_ANDROIDLOG_H_H
#define JNITHREAD_ANDROIDLOG_H_H

#endif //JNITHREAD_ANDROIDLOG_H_H
#include <android/log.h>
#define LOGD(FORMAT,...) __android_log_print(ANDROID_LOG_DEBUG, "Connrot-DEBUG", FORMAT, ##__VA_ARGS__);

