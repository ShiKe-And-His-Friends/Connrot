package com.example.ShikeApplication.audioProcess;

enum ThreadEnum {

    THTREAD_PERPARE(-1),

    THTREAD_START(0),

    THREAD_STOP(1),

    THREAD_FAIL(1);

    private int value = 0;

    ThreadEnum(int val){this.value = val;}

    public int Value(){return this.value;}

}
