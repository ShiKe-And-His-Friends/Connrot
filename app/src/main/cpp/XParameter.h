//
// Created by shike on 2/5/2020.
//

#ifndef CONNROT_XPARAMETER_H
#define CONNROT_XPARAMETER_H

struct AVCodecParameters;
class XParameter {
public:
    AVCodecParameters *para = 0;
    int channels = 2;
    int sample_rate = 44100;
};

#endif //CONNROT_XPARAMETER_H
