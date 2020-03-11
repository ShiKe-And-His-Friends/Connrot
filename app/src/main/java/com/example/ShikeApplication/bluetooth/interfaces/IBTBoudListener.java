package com.example.ShikeApplication.bluetooth.interfaces;

import android.bluetooth.BluetoothDevice;

/**
 * created by shike , 20200310
 */

public interface IBTBoudListener {
    /**
     * 设备配对状态改变
     * int BOND_NONE = 10; 配对没有成功
     * int BOND_BONDING = 11; 配对中
     * int BOND_BONDED = 12; 配对成功
     */
    void onBondStateChange(BluetoothDevice dev);
}
