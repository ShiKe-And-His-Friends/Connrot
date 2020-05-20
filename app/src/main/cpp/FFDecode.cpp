//
// Created by shike on 2/6/2020.
//

extern "C" {
#include <libavcodec/jni.h>
#include <libavutil/frame.h>
#include <libavcodec/avcodec.h>
}

#include "FFDecode.h"
#include "XLog.h"
void FFDecode::InitHard(void *vm) {
    av_jni_set_java_vm(vm ,0);
}

void FFDecode::Close() {
    if (FFDecode_DEBUG_LOG) {
        XLOGD("FFDecode Close methods.");
    }
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
    if (FFDecode_DEBUG_LOG) {
        XLOGD("FFDecode Open methods.");
    }
    Close();
    if (!para.para) {
        return false;
    }
    AVCodecParameters *p = para.para;
    XLOGI("FFDecode codec id is %d, codec type is %d." ,p->codec_id ,p->codec_type);
    AVCodec *cd = avcodec_find_decoder(p->codec_id);
    if (isHard) {
        cd = avcodec_find_encoder_by_name("h264_mediacodec");
        XLOGI("FFDecode use h264 mediacodec");
    }
    if (!cd) {
        XLOGI("FFDecode avcodec_find_decode %d failed! %d" ,p->codec_id ,isHard);
        return false;
    }
    XLOGI("FFDecode avcodec_find_decode %d success. %d" ,p->codec_id ,isHard);
    mux.lock();
    codec = avcodec_alloc_context3(cd);
    avcodec_parameters_to_context(codec ,p);
    codec->thread_count = 8;
    int re = avcodec_open2(codec ,cd ,0);
    if (re != 0) {
        mux.unlock();
        char buf[1024] = {0};
        av_strerror(re ,buf , sizeof(buf));
        XLOGE("%s" ,buf);
        return false;
    }
    if (codec->codec_type == AVMEDIA_TYPE_VIDEO) {
        this->isAudio = false;
        XLOGE("shikeDebug is Audio");
    } else {
        this->isAudio = true;
        XLOGE("shikeDebug is Video");
    }
    mux.unlock();
    XLOGI("FFDecode avcodec_open2 success! Is audio type is %d" ,this->isAudio);
    return true;
}

bool FFDecode::SendPacket(XData pkt) {
    if (FFDecode_DEBUG_LOG) {
        XLOGD("FFDecode SendPacket methods.");
    }
    if (pkt.size <0 || !pkt.data) {
        return false;
    }
    mux.lock();
    if (!codec) {
        mux.unlock();
        return false;
    }
    if (IDecode_DEBUG_LOG) {
        XLOGI("FFDecode stack send size is %d" ,pkt.size);
    }
    int re = avcodec_send_packet(codec ,(AVPacket *)pkt.data);
    mux.unlock();
    if (re != 0) {
        XLOGE("FFDecode stack send package ,return false.");
        return false;
    }
    return true;
}

XData FFDecode::RecvFrame() {
    if (FFDecode_DEBUG_LOG) {
        XLOGD("FFDecode RecvFrame methods.");
    }
    mux.lock();
    if (!codec) {
        mux.unlock();
        XLOGE("FFDecode RecvFrame failure ,cedec is null.");
        return XData();
    }
    if (!frame) {
        frame = av_frame_alloc();
    }
    int re = avcodec_receive_frame(codec ,frame);
    if (re != 0) {
        mux.unlock();
        XLOGE("FFDecode RecvFrame failure ,frame is null.");
        return XData();
    }
    if (FFDecode_DEBUG_LOG) {
        XLOGD("FFDecode RecvFrame.Decoder is Audio type %d .Frame format if %d ,memo size is %d" ,this->isAudio ,frame->format ,
              sizeof(frame->data));
        /*for (int i = 0 ; i < 8 ; i++) {
            XLOGD("FFDecode RecvFrame.Decoder is Audio type %d .Index Frame %d format if %d ,memo size is %d" ,this->isAudio ,i ,frame->format ,
                  sizeof(*frame->data[i]));
        }*/
    }
    XData d;
    d.data = (unsigned char *)(frame);
    if (codec->codec_type == AVMEDIA_TYPE_VIDEO) {
        /*d.size = (frame->linesize[0] + frame->linesize[1] + frame->linesize[2])* frame->height;*/
        d.size = (int)(frame->width * frame->height * 1.5);
        d.width = frame->width;
        d.height = frame->height;
        if (FFDecode_DEBUG_LOG) {
            XLOGI("FFDecode VIDEO DECODE.Decoder is Audio type %d.XData width is %d ,height is %d.Frame linesize is %d %d %d."
                    , this->isAudio ,d.width ,d.height ,frame->linesize[0] ,frame->linesize[1] ,frame->linesize[2] );
        }
    } else {
        d.size = av_get_bytes_per_sample((AVSampleFormat) frame->format) * frame->nb_samples * 2;
    }
    if (FFDecode_DEBUG_LOG) {
        XLOGD("FFDecode RecvFrame and processing date size if %d ,format is %d ,PTS is %d"
                ,sizeof(d.datas) ,frame->format ,(int)frame->pts);
    }
    d.format = frame->format;
    d.pts = (int)frame->pts;
    XLOGD("FFDecode RecvFrame and process success.Decoder is Video type %d .XData size if %d ,memo size is %d" ,this->isAudio ,d.size ,
          sizeof(d.datas));
    /*memcpy(d.datas[0] , frame->data[0] ,sizeof(frame->width*frame->height));
    memcpy(d.datas[1] , frame->data[1] ,sizeof(frame->width*frame->height/2));
    memcpy(d.datas[2] , frame->data[2] ,sizeof(frame->width*frame->height/2));*/
    memcpy(d.datas , frame->data ,sizeof(d.datas));
    pts = d.pts;
    mux.unlock();
    if (FFDecode_DEBUG_LOG) {
        XLOGD("FFDecode RecvFrame and process success.Decoder is Audio type %d .XData size if %d ,memo size is %d" ,this->isAudio ,d.size ,
              sizeof(d.datas));
    }
    return d;
}