package com.example.ShikeApplication.bluetooth.interfaces;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.Set;

/**
 * created by shike , 20200310
 */

public interface IBluetoothInterfaceCoustom {

    void init(Context context);

    void destroy();

    void getConnectedDevices();

    boolean isConnected(BluetoothDevice device);

    Set<BluetoothDevice> getBondedDevices();
}
