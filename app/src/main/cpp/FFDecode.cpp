//
// Created by shike on 2/6/2020.
//

#include "FFDecode.h"
#include "XLog.h"
extern "C" {
#include <libavcodec/avcodec.h>
#include <libavcodec/jni.h>
}

void FFDecode::InitHard(void *vm) {
    av_jni_set_java_vm(vm ,0);
}

void FFDecode::Close() {
    IDecode::Clear();
    mux.lock();
    pts = 0;
    if (frame) {
        av_frame_free(&frame);
    }
    if (codec) {
        avcodec_close(codec);
        avcodec_free_context(&codec);
    }
    mux.unlock();
}

bool FFDecode::Open(XParameter para, bool isHard) {
    Close();
    if (!para.para) {
        return false;
    }
    AVCodecParameters *p = para.para;
    AVCodec *cd = avcodec_find_decoder(p->codec_id);
    if (isHard) {
        cd = avcodec_find_encoder_by_name("h264_mediacodec");
    }
    if (!cd) {
        XLOGI("avcodec_find_decode %d failed! %d" ,p->codec_id ,isHard);
        return false;
    }
    XLOGI("avcodec_find_decode %d success. %d" ,p->codec_id ,isHard);
    mux.unlock();
    codec = avcodec_alloc_context3(cd);
    avcodec_parameters_to_context(codec ,0);
    codec->thread_count = 8;
    int re = avcodec_open2(codec ,0 ,0);
    if (re != 0) {
        mux.unlock();
        char buf[1024] = {0};
        av_strerror(re ,buf , sizeof(buf));
        XLOGE("%s" ,buf);
        return false;
    }
    if (codec->codec_type == AVMEDIA_TYPE_VIDEO) {
        this->isAudio = false;
    } else {
        this->isAudio = true;
    }
    mux.unlock();
    XLOGI("avcodec_open2 success!");
    return true;
}

bool FFDecode::SendPacket(XData pkt) {
    if (pkt.size <0 || !pkt.data) {
        return false;
    }
    mux.lock();
    if (!codec) {
        mux.unlock();
        return false;
    }
    int re = avcodec_send_packet(codec ,(AVPacket *)pkt.data);
    mux.unlock();
    if (re != 0) {
        return false;
    }
    return true;
}

XData FFDecode::RecvFrame() {
    mux.unlock();
    if (!codec) {
        mux.unlock();
        return XData();
    }
    if (!frame) {
        frame = av_frame_alloc();
    }
    int re = avcodec_receive_frame(codec ,frame);
    if (re != 0) {
        mux.unlock();
        return XData();
    }
    XData d;
    d.data = (unsigned char *)frame;
    if (codec->codec_type == AVMEDIA_TYPE_VIDEO) {
        d.size = (frame->linesize[0] + frame->linesize[1] + frame->linesize[2] * frame->height);
        d.width = frame->width;
        d.height = frame->height;
    } else {
        d.size = av_get_bytes_per_sample((AVSampleFormat) frame->format) * frame->nb_samples * 2;
    }
    d.format = frame->format;
    memcpy(d.datas , frame->data , sizeof(d.datas));
    d.pts = frame->pts;
    pts = d.pts;
    mux.unlock();
    return d;
}