package com.example.ShikeApplication.bluetooth.interfaces;

/**
 * created by shike , 20200310
 */

public interface IBTStateListener {

    /**
     * 蓝牙开关状态
     * int STATE_OFF = 10; //蓝牙关闭
     * int STATE_ON = 12; //蓝牙打开
     * int STATE_TURNING_OFF = 13; //蓝牙正在关闭
     * int STATE_TURNING_ON = 11; //蓝牙正在打开
     */
    void onStateChange(int state);

}
