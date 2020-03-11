package com.example.ShikeApplication.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.idcvideo.haokaihui.bluetooth.interfaces.IBTConnectListener;

import java.util.List;

@Deprecated
public class BluetoothConnectListener implements IBTConnectListener{
    private Context mContext;

    public BluetoothConnectListener (Context context) {
        if (context != null) {
            mContext = context;
        }
    }

    @Override
    public void onConnecting(BluetoothDevice bluetoothDevice) {//连接中
    }

    @Override
    public void onConnected(BluetoothDevice bluetoothDevice) {//连接成功
        if (bluetoothDevice.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO) {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.stopBluetoothSco();
                audioManager.startBluetoothSco();
            }
            mContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                    if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                        audioManager.setBluetoothScoOn(true);  //打开SCO
                        audioManager.setMode(0);
                        mContext.unregisterReceiver(this);  //别遗漏
                    }else {//等待一秒后再尝试启动SCO
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            audioManager.startBluetoothSco();
                        }
                }
            }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
        }

    }

    @Override
    public void onDisConnecting(BluetoothDevice bluetoothDevice) {}

    @Override
    public void onDisConnect(BluetoothDevice bluetoothDevice) {//断开
    }

    @Override
    public void onConnectedDevice(List<BluetoothDevice> devices) {//已连接设备
        if(devices==null||devices.size()<1){
            return;
        }
        for(BluetoothDevice dev:devices){
            if (dev.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO) {
                AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null) {
                    audioManager.stopBluetoothSco();
                    audioManager.startBluetoothSco();
                }
                mContext.registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                        if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                            audioManager.setBluetoothScoOn(true);  //打开SCO
                            audioManager.setMode(0);
                            mContext.unregisterReceiver(this);  //别遗漏
                        }else {//等待一秒后再尝试启动SCO
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            audioManager.startBluetoothSco();
                        }
                    }
                }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
            }
        }
    }
};