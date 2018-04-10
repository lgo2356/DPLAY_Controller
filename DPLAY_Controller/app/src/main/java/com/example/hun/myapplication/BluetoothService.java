package com.example.hun.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Activity mActivity;
    private Handler mHandler;

    private ConnectThread mConnectThread;

    private static final int REQUEST_CONNECT_DEVICE = 101;
    private static final int REQUEST_ENABLE_BT = 102;

    private static final int STATE_NONE = 0;
    private static final int STATE_LISTEN = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;

    private BluetoothAdapter btAdapter;
    private BluetoothDevice btDevice;
    private BluetoothSocket mSocket = null;
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;
    private Activity settingsActivity;

    Set<BluetoothDevice> mDevices;
    private String mConnectedDeviceName = null;
    private int mState;
    private int mPairedDeviceCount = 0;

    /**
     * 블루투스 활성화 및 연결
     *
     * getDeviceFromBondedList(String name): 페어링 된 디바이스 정보를 반환한다.
     * connectToSelectedDevice(String selectedDeviceName): 블루투스 소켓을 생성하고, 두 디바이스를 연결한다.
     * checkBluetooth(): 디바이스가 블루투스를 지원하는지 확인한다.
     * selectDevice(): 디바이스 선택 목록을 띄워주고 아이템 클릭 이벤트를 처리한다.
     */

    public BluetoothService(Activity ac, Handler h) {
        mActivity = ac;
        mHandler = h;

        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // 블루투스 확인 및 활성화

    public boolean getDeviceState() {

        if(btAdapter == null) {
            Log.d(TAG, "No bluetooth");
            return false;
        } else {
            Log.d(TAG, "Yes bluetooth");
            return true;
        }
    }

    public void enableBluetooth() {

        if(btAdapter.isEnabled()) {
            Log.d(TAG, "Enable");
            scanDevice();
        } else {
            Log.d(TAG, "No enable");
            Log.d(TAG, "Request Bluetooth enable");

            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    // 블루투스 장비 검색
    public void scanDevice() {
        Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
        mActivity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    // 블루투스 장비 연결
    public void getDeviceInfo(Intent data) {
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        Log.d(TAG, "Get Device Info \n" + "address: " + address);

        connect(device);
    }

    public void connect(BluetoothDevice device) {
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        public ConnectThread(BluetoothDevice device) {
            mDevice = device;
            BluetoothSocket socket_tmp = null;

            try {
                socket_tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mSocket = socket_tmp;
        }

        public void run() {
            setName("ConnectThread");

            btAdapter.cancelDiscovery();

            // 소켓 연결 시도
            try {
                mSocket.connect();
                Log.d(TAG, "Connect Success");

            } catch (IOException e) {
                try {
                    mSocket.close();
                    run();
                } catch (IOException e2) {
                    Log.e(TAG, "Connect failed", e2);
                }
            }
        }
    }
}
