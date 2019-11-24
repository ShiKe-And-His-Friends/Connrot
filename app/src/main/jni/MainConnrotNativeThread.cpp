// Created by shike on 10/27/2019.
#include "com_example_ShikeApplication_ndkdemo_ndktool.h"
#include <jni.h>
#include <string>
#include "pthread.h"
#include "../cpp/ThreadDemo/AndroidLog.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_ShikeApplication_ndkdemo_ndktool_getSomeDumpTextFromNDK
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


pthread_t thread;

void *normalCallBack(void * data)
{
    LOGD("create normal thread from C++!");
    pthread_exit(&thread);
}

extern "C"  JNIEXPORT void JNICALL Java_com_example_ShikeApplication_ndkdemo_ndktool_setNormalThread(JNIEnv *env, jobject instance) {
    // TODO
    pthread_create(&thread, NULL, normalCallBack, NULL);
}

#include "queue"
#include "unistd.h"

pthread_t produc;
pthread_t custom;
pthread_mutex_t mutex;
pthread_cond_t cond;

std::queue<int> queue;


void *producCallback(void *data)
{

    while (1)
    {
        pthread_mutex_lock(&mutex);

        queue.push(1);
        LOGD("生产者生产一个产品，通知消费者消费， 产品数量为 %d", queue.size());
        pthread_cond_signal(&cond);
        pthread_mutex_unlock(&mutex);
        sleep(5);
    }


    pthread_exit(&produc);
}

void *customCallback(void *data)
{
    while (1)
    {
        pthread_mutex_lock(&mutex);

        if(queue.size() > 0)
        {
            queue.pop();
            LOGD("消费者消费产品，产品数量还剩余 %d ", queue.size());
        } else{
            LOGD("没有产品可以消费， 等待中...");
            pthread_cond_wait(&cond, &mutex);
        }
        pthread_mutex_unlock(&mutex);
        usleep(500 * 1000);
    }
    pthread_exit(&custom);
}


extern "C"  JNIEXPORT void JNICALL Java_com_example_ShikeApplication_ndkdemo_ndktool_setMutexThread(JNIEnv *env, jobject instance) {
    for(int i = 0; i < 10; i++)
    {
        queue.push(1);
    }
    // TODO
    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init(&cond, NULL);

    pthread_create(&produc, NULL, producCallback, NULL);
    pthread_create(&custom, NULL, customCallback, NULL);
}

#include "../cpp/JavaListener.cpp"

JavaVM *jvm;

JavaListener *javaListener;

pthread_t chidlThread;


void *childCallback(void *data)
{
    JavaListener *javaListener1 = (JavaListener *) data;

    javaListener1->onError(0, 101, "c++ call java meid from child thread!");
    pthread_exit(&chidlThread);
}

extern "C"  JNIEXPORT void JNICALL Java_com_example_ShikeApplication_ndkdemo_ndktool_setCallbackFromC(JNIEnv *env, jobject instance) {
    // TODO
    javaListener = new JavaListener(jvm, env, env->NewGlobalRef(instance));
    //javaListener->onError(1, 100, "c++ call java meid from main thread!");
    pthread_create(&chidlThread, NULL, childCallback, javaListener);
}



JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void* reserved)
{
    JNIEnv *env;
    jvm = vm;
    if(vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK)
    {
        return -1;
    }
    return JNI_VERSION_1_6;
}