//
// Created by shike on 2/6/2020.
//

#ifndef CONNROT_IDECODE_H
#define CONNROT_IDECODE_H

#include "XParameter.h"
#include "IObserver.h"
#include <list>

class IDecode : public IObserver{
public:
    virtual bool Open() = 0;
};


#endif //CONNROT_IDECODE_H
