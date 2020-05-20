//
// Created by shike on 2/6/2020.
//

#include "FFDemux.h"
#include "XLog.h"
extern "C" {
#include <libavformat/avformat.h>
}

static double r2d (AVRational r) {
    return r.num == 0 || r.den == 0 ? 0. :(double) r.num/(double)r.den;
}

void FFDemux::Close () {
    mux.lock();
    if (ic) {
        avformat_close_input(&ic);
    }
    mux.unlock();
}

bool FFDemux::Seek (double pos) {
    if (pos < 0 || pos > 1) {
        XLOGE("FFDemux seek value must 0.0 ~ 1.0");
        return false;
    }
    bool re = false;
    mux.lock();
    if (!ic) {
        mux.unlock();
        return false;
    }
    avformat_flush(ic);
    long long seekPt = 0;
    seekPt = ic->streams[videoStream]->duration * pos;
    re = av_seek_frame(ic ,videoStream ,seekPt ,AVSEEK_FLAG_FRAME | AVSEEK_FLAG_BACKWARD);
    mux.unlock();
    return re;
}

bool FFDemux::Open (const char *url) {
    XLOGI("FFDemux Open file %s begin." ,url);
    Close();
    mux.lock();
    int re = avformat_open_input(&ic ,url ,0 ,0);
    if (re != 0) {
        mux.unlock();
        char buf[1024] = {0};
        av_strerror(re ,buf , sizeof(buf));
        XLOGE("FFDemux open %s failed! %s" ,url,buf);
        return false;
    }
    XLOGI("FFDemux open %s success!" ,url);
    re = avformat_find_stream_info(ic ,0);
    if (re != 0) {
        mux.unlock();
        char buf[1024] = {0};
        av_strerror(re ,buf , sizeof(buf));
        XLOGI("FFDemux avformat_find_stream_info %s fail! %s" ,url ,buf);
        return  false;
    }
    this->totalMs = ic->duration / (AV_TIME_BASE /1000);
    mux.unlock();
    XLOGI("total ms = %d ." ,totalMs);
    GetVPara();
    GetAPara();
    return true;
}

XParameter FFDemux::GetVPara () {
    XLOGI("FFDemux GetVPara start!");
    mux.lock();
    if (!ic) {
        mux.unlock();
        XLOGE("FFDemux GetVPara failed! ic is NULL!");
        return XParameter();
    }
    /*int re = 0;

    for (int i = 0 ; i < ic->nb_streams ; i++) {
        if (DEBUG)
            XLOGI("GetVPara stream channel is %d", i);
        AVStream *as = ic->streams[i];
        if (as->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            if (DEBUG)
                XLOGI("GetVPara stream channel find Video is %d", i);
            int fps = (int)r2d(as->avg_frame_rate);
            if (DEBUG)
                XLOGI("GetVPara stream fps = %d ,width = %d ,height = %d ,codec is = %d ,avformat = %d"
                    ,fps ,as->codecpar->width ,as->codecpar->height ,as->codecpar->codec_id ,as->codecpar->format);
        }
    }*/
    int re = av_find_best_stream(ic ,AVMEDIA_TYPE_VIDEO ,-1 ,-1 ,0 ,0);
    AVStream *as = ic->streams[re];
    XLOGI("FFDemux GetVPara stream re is %d ,width = %d ,height = %d ,codec is = %d ,avformat = %d",re ,as->codecpar->width ,as->codecpar->height ,as->codecpar->codec_id ,as->codecpar->format);
    if (re < 0) {
        mux.unlock();
        char errorBuf[1024] = {0};
        av_strerror(re ,errorBuf , sizeof(errorBuf));
        XLOGI("FFDemux GetVParav av_find_best_stream is %d failed!,%s" ,re ,errorBuf);
        return XParameter();
    }
    videoStream = re;
    XLOGD("FFDemux GetVPara getVideoPara video stream index is %d" ,videoStream);
    XParameter para;
    para.para = ic->streams[re]->codecpar;
    mux.unlock();
    XLOGI("FFDemux GetVPara start success!");
    return  para;
}

XParameter FFDemux::GetAPara () {
    XLOGI("FFDemux GetAPara staret!");
    mux.lock();
    if (!ic) {
        mux.unlock();
        XLOGI("FFDemux GetAPara failed! ic is NULL !");
        return XParameter();
    }
    int re = 0;
    for (int i = 0 ; i < ic->nb_streams ; i++) {
        if (DEBUG)
            XLOGI("FFDemux GetAPara stream channel is %d", i);
        AVStream *as = ic->streams[i];
        if (as->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            if (DEBUG)
                XLOGI("FFDemux GetAPara stream channel find Video is %d", i);
            int fps = (int)r2d(as->avg_frame_rate);
            if (DEBUG)
                XLOGI("FFDemux GetAPara stream smaple_rate = %d ,channels is = %d ,format = %d  ,codecid = %d"
            ,as->codecpar->sample_rate ,as->codecpar->channels ,as->codecpar->format ,as->codecpar->codec_id);
            re = i;
        }
    }
    //int re = av_find_best_stream(ic ,AVMEDIA_TYPE_AUDIO ,-1 ,-1 ,0 ,0);
    if (re < 0) {
        mux.unlock();
        XLOGE("FFDemux avfind_best_stream audio failed!");
        return XParameter();
    }
    audioStream = re;
    XLOGD("FFDemux GetAPara getVideoPara video stream index is %d" ,audioStream);
    XParameter para;
    para.para = ic->streams[re]->codecpar;
    para.channels = ic->streams[re]->codecpar->channels;
    para.sample_rate = ic->streams[re]->codecpar->sample_rate;
    mux.unlock();
    XLOGI("FFDemux GetAPara success!");
    return para;
}

XData FFDemux::Read () {
    if (DEBUG)
        XLOGI("FFDemux Read stream.");
    mux.lock();
    if (!ic) {
        mux.unlock();
        XLOGE("FFDemux Read failure, AVFormatContext ic is null.");
        return XData();
    }
    XData d;
    AVPacket *pkt = av_packet_alloc();
    int re = av_read_frame(ic ,pkt);
    if (re != 0) {
        av_packet_free(&pkt);
        mux.unlock();
        XLOGE("FFDemux Read failure,AVPacket re is null.");
        return XData();
    }
    d.data = (unsigned char*)pkt;
    d.size = pkt->size;
    if (pkt->stream_index == audioStream) {
        d.isAudio = true;
    } else if (pkt->stream_index == videoStream) {
        d.isAudio = false;
    } else {
        av_packet_free(&pkt);
        mux.unlock();
        XLOGE("FFDemux Read failure,AVPacket type re is failure.");
        return XData();
    }
    pkt->pts = pkt->pts * (1000 * r2d(ic->streams[pkt->stream_index]->time_base));
    pkt->dts = pkt->dts * (1000 * r2d(ic->streams[pkt->stream_index]->time_base));
    d.pts = (int) pkt->pts;
    mux.unlock();
    XLOGE("FFDemux Read success. XData type is %d, size is %d" ,d.isAudio ,d.size);
    return  d;
}

FFDemux::FFDemux() {
    static bool isFirst = true;
    if (isFirst) {
        isFirst = false;
        av_register_all();
        avcodec_register_all();
        avformat_network_init();
        XLOGI("regist ffmpeg!");
    }
}