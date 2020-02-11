//
// Created by shike on 2/5/2020.
//

#ifndef CONNROT_XEGL_H
#define CONNROT_XEGL_H


class XEGL {
public:
    virtual bool Init(void *win) = 0;
    virtual void Close() = 0;
    virtual void Draw() = 0;
    static XEGL * Get();

protected:
    XEGL(){}
};


#endif //CONNROT_XEGL_H
