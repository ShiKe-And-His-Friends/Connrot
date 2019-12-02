// Created by shike on 2019/12/2.
class VideoEncoder {

protected:
    bool transform = false;

public:
    virtual void InitEncoder(const char *mp4Path, int width, int height) = 0;
    virtual void EncodeStart() = 0;
    virtual void EncodeBuffer(unsigned char *nv21Buffer) = 0;
    virtual void EncodeStop() = 0;
    bool isTransform();
};

