//
// Created by 86186 on 2020/6/19.
//

#ifndef CONNROT_VIDEODECODEFFMPEGDEMO_H
    #include "XLog.h"
    extern "C" {
    #include <libavcodec/avcodec.h>
    #include <stdio.h>
    #include <stdlib.h>
    #include <string.h>
    }

#define CONNROT_VIDEODECODEFFMPEGDEMO_H


class VideoDecodeFfmpegDemo {
    public:
    virtual int StartDecode(const char *readPath ,const char *savePath );
    void pgm_save(unsigned char *buf, int wrap, int xsize, int ysize,
                  char *filename);
    void decode(AVCodecContext *dec_ctx, AVFrame *frame, AVPacket *pkt,
                const char *filename);
};


#endif //CONNROT_VIDEODECODEFFMPEGDEMO_H
