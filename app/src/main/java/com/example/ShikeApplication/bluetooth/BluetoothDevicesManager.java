package com.example.ShikeApplication.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.idcvideo.haokaihui.bluetooth.interfaces.IBluetoothInterfaceCoustom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * created by shike , 20200310
 */

public class BluetoothDevicesManager implements IBluetoothInterfaceCoustom {
    private final String TAG="BluetoothDevicesManager";
    private Context mContext;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothadapter;
    private BluetoothA2dp mBluetoothA2dp;
    private BluetoothHeadset mBluetoothHeadset;
    private IntentFilter mFilter;
    private boolean isBackConDev;
    private boolean isA2dpComplete,isHeadsetComplete;

    @Override
    public void init(Context context) {
        if (context == null) {
            return;
        }
        mContext=context.getApplicationContext();
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) {
            return;
        }
        mBluetoothadapter = mBluetoothManager.getAdapter();
        isA2dpComplete=false;
        isHeadsetComplete=false;
        mBluetoothadapter.getProfileProxy(mContext,mProfileServiceListener, BluetoothProfile.A2DP);
        mBluetoothadapter.getProfileProxy(mContext,mProfileServiceListener, BluetoothProfile.HEADSET);
        if(mFilter==null){
            mContext.registerReceiver(mBluetoothReceiver,makeFilter());
        }
    }


    private IntentFilter makeFilter() {
        if(mFilter==null){
            mFilter = new IntentFilter();
            mFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            mFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            mFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            mFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
            mFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED);
        }
        return mFilter;
    }

    @Override
    public Set<BluetoothDevice> getBondedDevices() {  // 获取以配对设备
        if(mBluetoothadapter==null){
            return null;
        }
        return mBluetoothadapter.getBondedDevices();
    }

    @Override
    public void destroy() {
        if(mFilter!=null && mContext!= null){
            mFilter=null;
            mContext.unregisterReceiver(mBluetoothReceiver);
        }
        isA2dpComplete=false;
        isHeadsetComplete=false;
        mBluetoothadapter.closeProfileProxy(BluetoothProfile.A2DP,mBluetoothA2dp);
        mBluetoothadapter.closeProfileProxy(BluetoothProfile.HEADSET,mBluetoothHeadset);
        mContext = null;
    }

    @Override
    public void getConnectedDevices() {
        if(isBackConDev){
            return;
        }
        isBackConDev=true;
        if(isA2dpComplete&&isHeadsetComplete){
            List<BluetoothDevice> devices=new ArrayList<>();
            if(mBluetoothA2dp!=null){
                List<BluetoothDevice> deviceList=mBluetoothA2dp.getConnectedDevices();
                if(deviceList!=null&&deviceList.size()>0){
                    devices.addAll(deviceList);
                }
            }
            if(mBluetoothHeadset!=null){
                List<BluetoothDevice> deviceList=mBluetoothHeadset.getConnectedDevices();
                if(deviceList!=null&&deviceList.size()>0){
                    devices.addAll(deviceList);
                }
            }
            checkConnectedAudiodevices(devices);
            isBackConDev=false;
        }

    }

    @Override
    public boolean isConnected(BluetoothDevice device) {  // 是否连接
        if(mBluetoothA2dp!=null&&mBluetoothA2dp.getConnectionState(device) == BluetoothA2dp.STATE_CONNECTED){
            Log.d(TAG,"isConnected name="+device.getName());
            List<BluetoothDevice> bluetoothDeviceList=mBluetoothA2dp.getConnectedDevices();
            if(bluetoothDeviceList!=null&&bluetoothDeviceList.size()>0){
                for(BluetoothDevice bluetoothDevice:bluetoothDeviceList){
                    if(!TextUtils.isEmpty(device.getAddress())&&device.getAddress().equals(bluetoothDevice.getAddress())){
                        return true;
                    }
                }
            }

        }
        if(mBluetoothHeadset!=null&&mBluetoothHeadset.getConnectionState(device) == BluetoothHeadset.STATE_CONNECTED){
            Log.d(TAG,"isConnected name="+device.getName());
            List<BluetoothDevice> bluetoothDeviceList=mBluetoothHeadset.getConnectedDevices();
            if(bluetoothDeviceList!=null&&bluetoothDeviceList.size()>0){
                for(BluetoothDevice bluetoothDevice:bluetoothDeviceList){
                    if(!TextUtils.isEmpty(device.getAddress())&&device.getAddress().equals(bluetoothDevice.getAddress())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

      // A2dp
     private BluetoothProfile.ServiceListener mProfileServiceListener=new BluetoothProfile.ServiceListener() {
         @Override
         public void onServiceConnected(int profile, BluetoothProfile proxy) {
             Log.i(TAG, "onServiceConnected profile="+profile);
             if(profile == BluetoothProfile.A2DP){  // 播放音乐
                 mBluetoothA2dp = (BluetoothA2dp) proxy;   // 转换
                 isA2dpComplete=true;
             }else if(profile == BluetoothProfile.HEADSET){  // 打电话
                 mBluetoothHeadset = (BluetoothHeadset) proxy;
                 isHeadsetComplete=true;
             }
             if(isA2dpComplete){
                 List<BluetoothDevice> devices=new ArrayList<>();
                 if(mBluetoothA2dp!=null){
                     List<BluetoothDevice> deviceList=mBluetoothA2dp.getConnectedDevices();
                     if(deviceList!=null&&deviceList.size()>0){
                         devices.addAll(deviceList);
                     }
                 }
                 if(mBluetoothHeadset!=null){
                     List<BluetoothDevice> deviceList=mBluetoothHeadset.getConnectedDevices();
                     if(deviceList!=null&&deviceList.size()>0){
                         devices.addAll(deviceList);
                     }
                 }
                 checkConnectedAudiodevices(devices);
             }
         }

         @Override
         public void onServiceDisconnected(int profile) {
             Log.i(TAG, "onServiceDisconnected profile="+profile);
             if(profile == BluetoothProfile.A2DP){
                 mBluetoothA2dp = null;
             }else if(profile == BluetoothProfile.HEADSET){
                 mBluetoothHeadset = null;
             }
         }
     };

    private BroadcastReceiver mBluetoothReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice dev;
            int state;
            if (action == null) {
                return;
            }
            switch (action) {
                /**
                 * 蓝牙开关状态
                 * int STATE_OFF = 10;   // 蓝牙关闭
                 * int STATE_ON = 12;   // 蓝牙打开
                 * int STATE_TURNING_OFF = 13;   // 蓝牙正在关闭
                 * int STATE_TURNING_ON = 11;   // 蓝牙正在打开
                 */
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    if(state == BluetoothAdapter.STATE_OFF){
                        closeBluetoothMusic();
                    }
                    break;
                /**
                 * 设备建立连接
                 * int STATE_DISCONNECTED = 0;   // 未连接
                 * int STATE_CONNECTING = 1;   // 连接中
                 * int STATE_CONNECTED = 2;   // 连接成功
                 */
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG, "设备建立连接：" + dev.getBondState());
                    break;
                /**
                 * 设备断开连接
                 */
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    break;
                /**
                 * 本地蓝牙适配器
                 * BluetoothAdapter连接状态
                 */
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG, "Adapter STATE: " + intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0));
                    Log.i(TAG, "BluetoothDevice: " + dev.getName() + ", " + dev.getAddress());
                    break;
                /**
                 * 提供用于手机的蓝牙耳机支持
                 * BluetoothHeadset连接状态
                 */
                case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
                    dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG, "Headset STATE: " + intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, 0));
                    Log.i(TAG, "BluetoothDevice: " + dev.getName() + ", " + dev.getAddress());
                    switch (intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1)) {
                        case BluetoothHeadset.STATE_CONNECTED:  // 已连接
                            checkConnectedAudiodevices(dev);
                            break;
                        case BluetoothHeadset.STATE_DISCONNECTED:  // 断开
                            closeBluetoothMusic();
                            break;
                        case BluetoothHeadset.STATE_DISCONNECTING:  // 断开中
                        case BluetoothHeadset.STATE_CONNECTING:  // 连接中
                            break;
                    }
                    break;
                /**
                 * 定义高质量音频可以从一个设备通过蓝牙连接传输到另一个设备
                 * BluetoothA2dp连接状态
                 */
                case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                    dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1)) {
                        case BluetoothA2dp.STATE_CONNECTED:
                            Log.i(TAG,"A2dp device: " + dev.getName() + " connected");
                            checkConnectedAudiodevices(dev);
                            break;

                        case BluetoothA2dp.STATE_DISCONNECTED:
                            Log.i(TAG,"A2dp device: " + dev.getName() + " disconnected");
                            closeBluetoothMusic();
                            break;

                        case BluetoothA2dp.STATE_DISCONNECTING:
                        case BluetoothA2dp.STATE_CONNECTING:
                        default:
                            break;
                    }

                case AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED: {
                    int deviceState = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                    switch (deviceState) {
                        case AudioManager.SCO_AUDIO_STATE_CONNECTED:
                            openBluetoothMusic();
                            break;

                        case AudioManager.SCO_AUDIO_STATE_DISCONNECTED:
                            closeBluetoothMusic();
                            break;
                    }
                }
                    break;
                default:
                    break;
            }
        }
    };

    private void checkConnectedAudiodevices (BluetoothDevice device) {
        Log.d(TAG , "check Connected Audio device.");
        if(device==null){
            return;
        }
        if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO) {
            prepareOpenBluetoothMusic();
        }
    }

    private void checkConnectedAudiodevices (List<BluetoothDevice> devices) {
        Log.d(TAG , "check Connected Audio devices.");
        if(devices==null||devices.size()<1){
            return;
        }
        boolean findBluetoothMusicDevices = false;
        for(BluetoothDevice dev:devices){
            if (dev.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO) {
                findBluetoothMusicDevices = true;
            }
        }
        if (findBluetoothMusicDevices) {
            prepareOpenBluetoothMusic();
        }
    }

    private void prepareOpenBluetoothMusic () {
        Log.d(TAG , "Prepare Open Bluetooth Music.");
        if (mContext == null) {
            Log.d(TAG , "mContext is null.");
            return;
        }
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.stopBluetoothSco();
            audioManager.startBluetoothSco();
        }
    }

    private void openBluetoothMusic () {
        Log.d(TAG , "open Bluetooth Music.");
        if (mContext == null) {
            Log.d(TAG , "mContext is null.");
            return;
        }
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setBluetoothScoOn(true);  //打开SCO
            audioManager.setMode(0);
        }
    }

    private void closeBluetoothMusic () {
        Log.d(TAG , "close Bluetooth Music.");
        if (mContext == null) {
            Log.d(TAG , "mContext is null.");
            return;
        }
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.stopBluetoothSco();
            audioManager.setSpeakerphoneOn(true);
        }
    }
}
