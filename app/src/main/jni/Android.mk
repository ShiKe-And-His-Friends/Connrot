# Return *.mk dir by build system
LOCAL_PATH := $(call my-dir)
NDK_PROJECT_PATH := $(call my-dir)

# MUST Involve a system global GNU Makefile script to clear LOCAL_MODEL,
# LOCAL_SRC_FILE,LOCAL_STATIC_LIBRARIES ... Not include LOCAL_PATH
include $(CLEAR_VARS)

# Different *.so file
LOCAL_MODULE := connrot-jni
APP_PLATFORM := android-28
app_abi := all

# List the set of C/C++ files that will compile.Not include *.H file
rwildcard = $(wildcard $1$2) $(foreach d,$(wildcard $1*),$(call rwildcard,$d/,$2))
MY_ALL_FILES := $(foreach src_path,$(MY_FILES_PATH), $(call rwildcard,$(src_path),*.*) )
MY_ALL_FILES := $(MY_ALL_FILES:$(MY_CPP_PATH)/./%=$(MY_CPP_PATH)%)
MY_SRC_LIST  := $(filter $(MY_FILES_SUFFIX),$(MY_ALL_FILES))
LOCAL_SRC_FILES  := $(MY_SRC_LIST)
LOCAL_SRC_FILES  := $(MY_SRC_LIST)
LOCAL_SRC_FILES += MainConnrotNativeThread.cpp
# Collect  all type files After CLEAR_VARS script run ,like LOCAL_xxxinx
# BUILD_STATIC_LIBRARY  a Type Static Library
# BUILD_SHARED_LIBRARY  a Type Dynamic Library
# BUILD_SHARED_LIBRARY  a Type Executable Native programe
# BUILD_PREBUILT  means this module has precompile
include $(BUILD_SHARED_LIBRARY)