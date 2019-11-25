// Created by shike on 2019/11/25.
#ifndef CONNROT_JAVALISTENER_H
#define CONNROT_JAVALISTENER_H
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
#endif //CONNROT_JAVALISTENER_H
