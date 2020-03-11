package com.example.ShikeApplication.bluetooth.interfaces;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.Set;

/**
 * created by shike , 20200310
 */

public interface IBluetoothInterface {

    void init(Context context);
    boolean open();  // 打开蓝牙
    boolean close();  // 关闭蓝牙
    boolean startDiscovery();  // 搜索蓝牙
    boolean stopDiscovery();  // 停止搜索蓝牙
    String getName();  // 获取本地蓝牙名称
    boolean setName(String name);  // 设置蓝牙的名称
    String getAddress();  // 获取本地蓝牙地址
    boolean isEnable();  // 蓝牙是否可用，即是否打开
    boolean isSupport();  // 是否支持蓝牙
    Set<BluetoothDevice> getBondedDevices();  // 获取以配对设备
    boolean createBond(BluetoothDevice device);  // 配对
    boolean removeBond(BluetoothDevice device);  // 取消配对

    boolean connect(BluetoothDevice device);  // 连接设备
    boolean disconnect(BluetoothDevice device);  // 断开设备
    void destroy();
    void getConnectedDevices();  // 获取已连接的设备
    boolean isConnected(BluetoothDevice device);  // 是否连接
    boolean setDiscoverableTimeout(int timeout);  // 设备可见时间

    void setBTStateListener(IBTStateListener btStateListener);

    void setBTScanListener(IBTScanListener btScanListener);

    void setBTBoudListener(IBTBoudListener btBoudListener);

    void setBTConnectListener(IBTConnectListener btConnectListener);

    /** copy from
     * https:  // download.csdn.net/download/qq_29924041/9733014
     * */
}
