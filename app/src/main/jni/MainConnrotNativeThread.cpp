// Created by shike on 10/27/2019.
#include "com_example_ShikeApplication_ndkdemo_ndktool.h"

extern "C" JNIEXPORT jstring JNICALL Java_com_example_ShikeApplication_ndkdemo_ndktool_getSomeDumpTextFromNDK
        (JNIEnv *env, jobject obj){
    return env->NewStringUTF("JNI Native Callback String.");
}

extern "C"  JNIEXPORT jstring JNICALL Java_com_example_ShikeApplication_ndkdemo_ndktool_getNativeCompileVersion
        (JNIEnv *env, jobject obj){
    return env->NewStringUTF("内核版本号:001.19.10.27.001");
}

extern "C"  JNIEXPORT jstring JNICALL Java_com_example_ShikeApplication_ndkdemo_ndktool_getNativeLibraryVersion
        (JNIEnv *env, jobject obj) {
    return env->NewStringUTF("内核版本号:001.19.10.27.1001");
}
