// Created by shike on 2/2/2020.

#ifndef CONNROT_ANDROIDLOGHELP_H
#define CONNROT_ANDROIDLOGHELP_H

extern "C" {
#include <android/log.h>
}
#define LOGD(FORMAT,...) __android_log_print(ANDROID_LOG_DEBUG, "Connrot-DEBUG", FORMAT, ##__VA_ARGS__);

#endif //CONNROT_ANDROIDLOGHELP_H