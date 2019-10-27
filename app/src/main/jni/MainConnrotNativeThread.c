// Created by shike on 10/27/2019.
#include "com_example_ShikeApplication_ndkdemo_ndktool.h"

JNIEXPORT jstring JNICALL Java_com_example_ShikeApplication_ndkdemo_ndktool_getSomeDumpTextFromNDK
        (JNIEnv *env, jobject obj){
    return (*env)->NewStringUTF(env,"JNI Native Callback String.");
}

JNIEXPORT jstring JNICALL Java_com_example_ShikeApplication_ndkdemo_ndktool_getNativeCompileVersion
(JNIEnv *env, jobject obj){
    return (*env)->NewStringUTF(env,"内核版本号:001.19.10.27.001");
}
