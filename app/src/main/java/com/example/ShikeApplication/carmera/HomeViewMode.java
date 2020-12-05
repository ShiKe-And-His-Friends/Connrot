package com.example.ShikeApplication.carmera;

public enum HomeViewMode {

    CLOSE(0),

    CAMERA_BACK_OPEN(1),

    CAMERA_FRONT_OPEN(2);

    private int value = 0;

    HomeViewMode(int val){this.value = val;}

    public int Value(){return this.value;}

}
