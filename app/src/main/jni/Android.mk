# Return *.mk dir by build system
LOCAL_PATH := $(call my-dir)
NDK_PROJECT_PATH := $(call my-dir)

# MUST Involve a system global GNU Makefile script to clear LOCAL_MODEL,
# LOCAL_SRC_FILE,LOCAL_STATIC_LIBRARIES ... Not include LOCAL_PATH
include $(CLEAR_VARS)

# Different *.so file
LOCAL_MODULE := connrot-jni
APP_PLATFORM := android-28
app_abi := arm64-v8a armeabi-v7a x86

# List the set of C/C++ files that will compile.Not include *.H file
LOCAL_SRC_FILES := MainConnrotNativeThread.c

# Collect  all type files After CLEAR_VARS script run ,like LOCAL_xxxinx
# BUILD_STATIC_LIBRARY  a Type Static Library
# BUILD_SHARED_LIBRARY  a Type Dynamic Library
# BUILD_SHARED_LIBRARY  a Type Executable Native programe
# BUILD_PREBUILT  means this module has precompile
include $(BUILD_SHARED_LIBRARY)