package com.example.hun.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SettingsActivity extends Activity {
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice mRemoteDevice = null;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;

    String mStrDelimiter = "\n";
    char mCharDelimiter = '\n';

    Thread mWorkerThread = null;

    byte[] readBuffer = null;
    int readBufferPosition = 0;

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_ENABLE_BT = 101;
    private int mPairedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;

    TextView mEditReceive, mEditSend;
    Button mButtonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mEditSend = (EditText) findViewById(R.id.edit_text_send);
        mEditReceive = (TextView) findViewById(R.id.edit_text_receive);
        mButtonSend = (Button) findViewById(R.id.button_send);

        checkBluetooth();
    }

    /**
     * 블루투스 활성화 및 연결
     *
     * getDeviceFromBondedList(String name): 페어링 된 디바이스 정보를 반환한다.
     * connectToSelectedDevice(String selectedDeviceName): 블루투스 소켓을 생성하고, 두 디바이스를 연결한다.
     * checkBluetooth(): 디바이스가 블루투스를 지원하는지 확인한다.
     * selectDevice(): 디바이스 선택 목록을 띄워주고 아이템 클릭 이벤트를 처리한다.
     */

    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;

        for(BluetoothDevice device : mDevices) {
            if(name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }

    void connectToSelectedDevice(String selectedDeviceName) {
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);

        try {
            // 소켓 생성, RFCOMM 채널을 통한 연결.
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            // 두 디바이스 연결
            mSocket.connect();

            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

            beginListenForData();

            mWorkerThread.start();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null) {
            // 디바이스가 블루투스를 지원하지 않을 경우
            finish();
        } else {
            // 디바이스가 블루투스를 지원하는 경우
            if(!mBluetoothAdapter.isEnabled()) {
                Intent enalbeBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enalbeBTIntent, REQUEST_ENABLE_BT);
            } else {
                // 페어링 된 디바이스 목록을 보여주고, 연결할 장치를 선택할 수 있다.
                selectDevice();
            }
        }
    }

    void selectDevice() {
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDeviceCount = mDevices.size();

        if(mPairedDeviceCount == 0) {
            finish();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        List<String> listItems = new ArrayList<String>();

        for(BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }
        listItems.add("취소");

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(item == mPairedDeviceCount) {
                    finish();
                } else {
                    connectToSelectedDevice(items[item].toString());
                }
            }
        });

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     *
     */

    void beginListenForData() {
        final Handler handler = new Handler();

        readBuffer = new byte[1024];
        readBufferPosition = 0;

        mWorkerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        int byteAvailable = mInputStream.available();

                        if(byteAvailable > 0) {
                            byte[] packetBytes = new byte[byteAvailable];

                            mInputStream.read(packetBytes);

                            for (int i = 0; i < byteAvailable; i++) {
                                byte b = packetBytes[i];

                                if (b == mCharDelimiter) {
                                    byte[] encodeedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodeedBytes, 0, encodeedBytes.length);

                                    final String data = new String(encodeedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mEditReceive.setText(mEditReceive.getText().toString() + data + mStrDelimiter);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "데이터 수신 중 오류가 발생 했습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            mWorkerThread.interrupt();
            mInputStream.close();
            mSocket.close();
        } catch (Exception e) {
            super.onDestroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) {
                    // 블루투스를 활성화 상태일 때
                    selectDevice();
                } else {
                    if(resultCode == RESULT_CANCELED) {
                        // 블루투스가 비활성화 상태일 때
                        Toast.makeText(getApplicationContext(), "블루투스를 사용할 수 없어 프로그램을 종료합니다.",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                }
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
