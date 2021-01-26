//
// Created by shike on 2/5/2020.
//

#include <EGL/egl.h>
#include <android/native_window.h>
#include <mutex>
#include "XLog.h"
#include "XEGL.h"

class CXEGL :public XEGL {
public:
    EGLDisplay display = EGL_NO_DISPLAY;
    EGLSurface surface = EGL_NO_SURFACE;
    EGLContext context = EGL_NO_CONTEXT;
    std::mutex mux;

    virtual void Draw () {
        mux.lock();
        if (display == EGL_NO_DISPLAY) {
            mux.unlock();
            XLOGE("CXEGL Draw failure, display is EGL_NO_DISPLAY.");
            return;
        }
        if (surface == EGL_NO_SURFACE) {
            mux.unlock();
            XLOGE("CXEGL Draw failure, surface is NO_SURFACE.");
            return;
        }
        eglSwapBuffers(display ,surface);
        XLOGI("CXEGL Draw success.");
        mux.unlock();
    }

    virtual void Close () {
        mux.lock();
        if (display == EGL_NO_DISPLAY) {
            mux.unlock();
            XLOGI("CXEGL Close failure,display NO_SURFACE.");
            return;
        }
        eglMakeCurrent(display ,EGL_NO_SURFACE ,EGL_NO_SURFACE ,EGL_NO_CONTEXT);
        if (surface != EGL_NO_SURFACE) {
            XLOGE("CXEGL Close ,surface destory.");
            eglDestroySurface(display ,surface);
        }
        if (context != EGL_NO_CONTEXT) {
            XLOGE("CXEGL Close ,context destory.");
            eglDestroyContext(display ,context);
        }
        eglTerminate(display);
        display = EGL_NO_DISPLAY;
        surface = EGL_NO_SURFACE;
        context = EGL_NO_CONTEXT;
        mux.unlock();
        XLOGI("CXEGL Close success.");
    }

    virtual bool Init (void *win) {
        ANativeWindow *nwin = (ANativeWindow *)win;
        XLOGE("CXEGL init windows");
        // Close();
        mux.lock();
        display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        if (display == EGL_NO_DISPLAY) {
            mux.unlock();
            XLOGE("CXEGL eglGetDisplay failed!");
            return false;
        }
        XLOGI("CXEGL eglGetDisplay success!");
        if (EGL_TRUE != eglInitialize(display ,0 ,0)) {
            mux.unlock();
            XLOGE("CXEGL eglInitialized failed!");
            return false;
        }
        XLOGI("CXEGL eglInitialized success!");
        EGLint configSpec [] = {
                EGL_RED_SIZE,8,
                EGL_GREEN_SIZE,8,
                EGL_BLUE_SIZE,8,
                EGL_SURFACE_TYPE,EGL_WINDOW_BIT,
                EGL_NONE
        };
        EGLConfig config = 0;
        EGLint numConfigs = 0;
        if (EGL_TRUE != eglChooseConfig(display ,configSpec ,&config ,1 ,&numConfigs)) {
            mux.unlock();
            XLOGE("CXEGL eglChooseConfig failed!");
            return false;
        }
        XLOGI("CXEGL eglChooseConfig success!");
        surface = eglCreateWindowSurface(display ,config ,nwin ,NULL);
        if (surface == EGL_NO_SURFACE) {
            mux.unlock();
            XLOGE("CXEGL eglCreateWindowSurface surface failed!");
            return false;
        }
        const EGLint ctxAttr[] = {EGL_CONTEXT_CLIENT_VERSION ,2 ,EGL_NONE};
        context = eglCreateContext(display ,config ,EGL_NO_CONTEXT ,ctxAttr);
        if (context == EGL_NO_CONTEXT) {
            mux.unlock();
            XLOGE("CXEGL eglCreateContext failed!");
            return false;
        }
        XLOGI("CXEGL eglCreateContext success!");
        if (EGL_TRUE != eglMakeCurrent(display ,surface ,surface ,context)) {
            mux.unlock();
            XLOGE("CXEGL eglMakeCurrent failed!");
            return false;
        }
        XLOGI("CXEGL eglMakeCurrent success!");
        mux.unlock();
        return true;
    }
};

XEGL *XEGL::Get() {
    static CXEGL egl;
    return &egl;
}