// Created by shike on 10/27/2019.
#include "com_example_ShikeApplication_ndkdemo_ndktool.h"
#include <string>
#include "pthread.h"
#include "../cpp/ThreadDemo/AndroidLogHelp.h"
#include "../cpp/IPlayerPorxy.h"
#include <android/native_window_jni.h>

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

extern "C" JNIEXPORT jint JNI_OnLoad (JavaVM * vm ,void *res){
    IPlayerPorxy::Get()->Init(vm);
    //IPlayerPorxy::Get()->Open("/sdcard/v1080.mp4");
    //IPlayerPorxy::Get()->Start();
    return JNI_VERSION_1_4;
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
#include "../cpp/VideoDecodeFfmpegDemo.h"

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


extern "C"  JNIEXPORT void JNICALL Java_com_example_ShikeApplication_ndkdemo_ndktool_setCallbackFromC(JNIEnv *env, jobject instance) {
    // TODO
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ShikeApplication_ndkdemo_ndktool_encoderMP4VideoStart
(JNIEnv *env, jclass clazz,jstring video_loacl_path,jint video_in_stream_width,jint video_in_stream_height) {
    // TODO: implement encoderMP4VideoStart()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ShikeApplication_ndkdemo_ndktool_encoderMP4VideoEnd
(JNIEnv *env, jclass clazz) {
    // TODO: implement encoderMP4VideoEnd()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ShikeApplication_ndkdemo_ndktool_encoderMP4VideoOnPrevireFrame
(JNIEnv *env,jclass clazz,jbyteArray raw_yuv_date,jint video_in_stream_width,jint video_in_stream_height) {
    // TODO: implement encoderMP4VideoOnPrevireFrame()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ShikeApplication_ndkdemo_ndktool_NPlayerInitView(JNIEnv *env, jclass clazz,
                                                           jobject surface) {
    ANativeWindow *win = ANativeWindow_fromSurface(env ,surface);
    IPlayerPorxy::Get()->Init();
    IPlayerPorxy::Get()->InitView(win);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ShikeApplication_ndkdemo_ndktool_NPlayerOpenUrl(JNIEnv *env, jclass clazz, jstring SourceUrl) {
    const char *url = env->GetStringUTFChars(SourceUrl ,0);
    IPlayerPorxy::Get()->Open(url);
    IPlayerPorxy::Get()->Start();
    //IPlayerPorxy::Get()->Seek(0.5);
    //env->ReleaseStringUTFChars(SourceUrl ,url);
}

extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_ShikeApplication_ndkdemo_ndktool_NPlayerGetPos(JNIEnv *env, jclass clazz) {
    return IPlayerPorxy::Get()->PlayPos();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ShikeApplication_ndkdemo_ndktool_NPlayerSeek(JNIEnv *env, jclass clazz ,jdouble pos) {
    IPlayerPorxy::Get()->Seek(pos);
}extern "C"
JNIEXPORT void JNICALL
Java_com_example_ShikeApplication_ndkdemo_ndktool_NPlayerPauseOrPlay(JNIEnv *env, jclass clazz) {
    IPlayerPorxy::Get()->SetPause(!IPlayerPorxy::Get()->IsPause());
}extern "C"
JNIEXPORT void JNICALL
Java_com_example_ShikeApplication_ndkdemo_ndktool_deocdeVideoMethod(JNIEnv *env, jclass clazz
        , jstring readPathStr, jstring savePathStr) {
    const char *readPath = env->GetStringUTFChars(readPathStr ,0);
    const char *savePath = env->GetStringUTFChars(savePathStr ,0);
    VideoDecodeFfmpegDemo *decodeFfmpegDemo = new VideoDecodeFfmpegDemo();
    decodeFfmpegDemo->StartDecode(readPath ,savePath);
}