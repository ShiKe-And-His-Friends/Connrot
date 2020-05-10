# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html
# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.4.1)

set(FF ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI})
add_library(avcodec SHARED IMPORTED)
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION ${FF}/libavcodec.so)

add_library(avformat SHARED IMPORTED)
set_target_properties(avformat PROPERTIES IMPORTED_LOCATION ${FF}/libavformat.so)

add_library(avutil SHARED IMPORTED)
set_target_properties(avutil PROPERTIES IMPORTED_LOCATION ${FF}/libavutil.so)

add_library(swscale SHARED IMPORTED)
set_target_properties(swscale PROPERTIES IMPORTED_LOCATION ${FF}/libswscale.so)

add_library(swresample SHARED IMPORTED)
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION ${FF}/libswresample.so)

add_library(avdevice SHARED IMPORTED)
set_target_properties(avdevice PROPERTIES IMPORTED_LOCATION ${FF}/libavdevice.so)

add_library(postproc SHARED IMPORTED)
set_target_properties(postproc PROPERTIES IMPORTED_LOCATION ${FF}/libpostproc.so)

include_directories(include)
include_directories(src/main/jni/)

aux_source_directory(src/main/jni NATIVE_BASE_SRC )
aux_source_directory(src/main/cpp THREAD_SRC )
#
#aux_source_directory( ./src/main/cpp/ThreadDemo THREAD_Demo_SRC )
#list(APPEND NATIVE_BASE_SRC ${THREAD_Demo_SRC})
#
list(APPEND NATIVE_BASE_SRC ${THREAD_SRC})

# Add Third part header linked file
#
#include_directories(src/main/cpp/include/libavcodec)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
add_library( # Sets the name of the library.
        connrotCmake-lib
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        ${NATIVE_BASE_SRC}
        #${CMAKE_SOURCE_DIR}/src/main/jni/com_example_ShikeApplication_ndkdemo_ndktool.h,
        #${CMAKE_SOURCE_DIR}/src/main/jni/MainConnrotNativeThread.cpp,
        #${CMAKE_SOURCE_DIR}/src/main/cpp/JavaListener.cpp
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
#
#target_include_directories(avcodec
#        PRIVATE
#        ${CMAKE_SOURCE_DIR}/src/main/cpp/include/libavcodec)
find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

#add_library(avdevice SHARED IMPORTED)
#set_target_properties(avdevice PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/src/main/libs/${ANDROID_ABI}/libavdevice.so)
#
#add_library(avfilter SHARED IMPORTED)
#set_target_properties(avfilter PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/src/main/libs/${ANDROID_ABI}/libavfilter.so)
#
#add_library(avformat SHARED IMPORTED)
#set_target_properties(avformat PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/src/main/libs/${ANDROID_ABI}/libavformat.so)
#
#add_library(avutil SHARED IMPORTED)
#set_target_properties(avutil PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/src/main/libs/${ANDROID_ABI}/libavutil.so)
#
#add_library(postproc SHARED IMPORTED)
#set_target_properties(postproc PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/src/main/libs/${ANDROID_ABI}/libpostproc.so)
#
#add_library(swresample SHARED IMPORTED)
#set_target_properties(swresample PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/src/main/libs/${ANDROID_ABI}/libswresample.so)
#
#add_library(swscale SHARED IMPORTED)
#set_target_properties(swscale PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/src/main/libs/${ANDROID_ABI}/libswscale.so)
#
#
#target_link_libraries( # Specifies the target library.
#        connrot-jni
#        avutil
#        avcodec
#        avdevice
#        swresample
#        swscale
#        avfilter
#        avformat
#        postproc
#        # Links the target library to the log library
#        # included in the NDK.
#        ${log-lib} )

target_link_libraries( # Specifies the target library.
        connrotCmake-lib
        GLESv2 EGL
        OpenSLES
        android
        avcodec avformat avutil swscale swresample avdevice postproc
        ${log-lib})