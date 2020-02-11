#!/bin/bash
set -x

#NDK 20b
#FFmpeg4.2.2
export NDK=/home/sk95120/Downloads/android-ndk-r20b
export API=26
export TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/linux-x86_64/bin

export ARCH=arm
export PLATFORM=armv7a
export TARGET=$PLATFORM-linux-androideabi
#正确的sysroot
export SYSROOT=$NDK/toolchains/llvm/prebuilt/linux-x86_64/sysroot
export CPU=aarch64
export PREFIX=/home/sk95120/anroid-ffmpeg-so/$CPU
#export CFLAG="-D__ANDROID_API__=$API -Os -fPIC -DANDROID "
export CFLAG="-Os -mfpu=vfp -marm -march=armv7-a "

function function_one
{
./configure \
--prefix=$PREFIX \
--disable-neon \
--disable-hwaccels \
--disable-gpl \
--disable-asm \
--enable-shared \
--disable-static \
--enable-jni \
--enable-mediacodec \
--enable-decoder=h264_mediacodec \
--disable-doc \
--enable-ffmpeg \
--disable-ffplay \
--disable-ffprobe \
--disable-avdevice \
--disable-decoders \
--disable-encoders \
--disable-devices \
--disable-symver \
--cross-prefix=$TOOLCHAIN/$ARCH-linux-androideabi- \
--target-os=android  \
--arch=$ARCH \
--cpu=$CPU \
--cc=$TOOLCHAIN/$TARGET$API-clang \
--cxx=$TOOLCHAIN/$TARGET$API-clang++ \
--enable-cross-compile \
--sysroot=$SYSROOT \
--extra-cflags="$CFLAG" \
--extra-ldflags="-fPIE -pie"

make clean all
make 
make install
}
CPU=armv7-a
PREFIX=$(pwd)/android/$CPU
function_one

#armv8-a

#ARCH=arm64
#CPU=armv8-a
#CC=$TOOLCHAIN/bin/aarch64-linux-android$API-clang
#CXX=$TOOLCHAIN/bin/aarch64-linux-android$API-clang++
#SYSROOT=$NDK/toolchains/llvm/prebuilt/linux-x86_64/sysroot
#CROSS_PREFIX=$TOOLCHAIN/bin/aarch64-linux-android-
#PREFIX=$(pwd)/android/$CPU
#OPTIMIZE_CFLAGS="-march=$CPU"


#armv7-a
#OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=vfp -marm -march=$CPU"


#x86

#ARCH=x86
#CPU=x86
#CC=$TOOLCHAIN/bin/i686-linux-android$API-clang
#CXX=$TOOLCHAIN/bin/i686-linux-android$API-clang++
#SYSROOT=$NDK/toolchains/llvm/prebuilt/linux-x86_64/sysroot
#CROSS_PREFIX=$TOOLCHAIN/bin/i686-linux-android-
#PREFIX=$(pwd)/android/$CPU
#OPTIMIZE_CFLAGS="-march=i686 -mtune=intel -mssse3 -mfpmath=sse -m32"


#x86_64

#ARCH=x86_64
#CPU=x86-64
#CC=$TOOLCHAIN/bin/x86_64-linux-android$API-clang
#CXX=$TOOLCHAIN/bin/x86_64-linux-android$API-clang++
#SYSROOT=$NDK/toolchains/llvm/prebuilt/linux-x86_64/sysroot
#CROSS_PREFIX=$TOOLCHAIN/bin/x86_64-linux-android-
#PREFIX=$(pwd)/android/$CPU
#OPTIMIZE_CFLAGS="-march=$CPU-msse4.2 -mpopcnt -m64 -mtune=intel"

#链接：https://www.jianshu.com/p/6bbc392cc2bc
