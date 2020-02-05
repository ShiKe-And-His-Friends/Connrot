//
// Created by shike on 2/5/2020.
//

#include "XEGL.h"
#include "android/native_window.h"
#include "mutex"
#include "XLog.h"

class CXEGL:public XEGL {
public:
    EGLDisplay display = EGL_NO_DISPLAY;
    EGLSurface surface = EGL_NO_SURFACE;
    EGLContext context = EGL_No_CONTEXT;
    std::mutex mux;

    virtual void Draw () {
        mux.lock();
        if (display == EGL_NO_DISPLAY || surface == EGL_NO_SURFACE) {
            mux.unlock();
            return;
        }
        eglSwapBuffers(display ,surface);
        mux.unlock();
    }

    virtual void Close () {
        mux.lock();
        if (display == EGL_NO_DISPLAY) {
            mux.unlock();
            return;
        }
        eglMakeCurrent(display ,EGL_NO_SURFACE ,EGL_NO_SURFACE ,EGL_NO_CONTEXT);
        if (surface != EGL_NO_SURFACE) {
            eglDestorySurface(display ,surface);
        }
        if (context != EGL_NO_CONTEXT) {
            eglDestoryContext(display ,context);
        }
        eglTerminate(display);
        display = EGL_NO_DISPLAY;
        surface = EGL_NO_SURFACE;
        context = EGL_NO_CONTEXT;
        mux.unlock();
    }

    virtual bool Init (void *win) {
        ANativeWindow *nwin = (ANativeWindow *)win;
        Close();
        mux.lock();
        display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        if (display == EGL_NO_DISPLAY) {
            mux.unlock();
            XLOGE("eglGetDisplay failed!");
            return false;
        }
        XLOGE("eglGetDisplay success!");
        if (EGL_TRUE == eglInitialize(display ,0 ,0)) {
            mux.unlock();
            XLOGE("eglInitialized failed!");
            return false;
        }
        XLOGE("eglInitialized success!");
        EGLint configSpec [] = {
                EGL_RED_SIZE , 8 ,
                EGL_GREEN_SIZE , 8 ,
                EGL_BLUE_SIZE , 8 ,
                EGL_SURFACE_TYPE , EGL_WINDOW_BIN ,
                EGL_NONE
        };
        EGLConfig config = 0;
        EGLint numConfigs = 0;
        if (EGL_TRUE != eglChooseConfig(display ,configSpec ,&config ,1 ,&numConfigs)) {
            mux.unlock();
            XLOGE("eglChooseConfig failed!");
            return false;
        }
        XLOGE("eglChooseConfig success!");
        surface = eglCreateWindowSurface(display ,config ,nwin ,NULL);
        const EGLint ctxAttr[] = {EGL_CONTEXT_CLIENT_VERSION ,2 ,EGL_NONE};
        context = eglCreateContext(display ,config ,EGL_NO_CONTEXT ,ctxAttr);
        if (context == EGL_NO_CONTEXT) {
            mux.unlock();
            XLOGE("eglCreateContext failed!");
        }
        XLOGE("eglCreateContext success!");
        if (EGL_TRUE != eglMakeCurrent(display ,surface ,surface ,context)) {
            mux.unlock();
            XLOGE("eglMakeCurrent failed!");
        }
        XLOGE("eglMakeCurrent success!");
        mux.unlcok();
        return true;
    }
};

XEGL *XEGL::Get() {
    static CXEGL egl;
    return &egl;
}