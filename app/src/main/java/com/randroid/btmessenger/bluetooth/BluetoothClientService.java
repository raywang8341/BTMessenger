package com.randroid.btmessenger.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.io.Serializable;

/**
 * Created by hui on 3/30/15.
 */
public class BluetoothClientService extends Service {

    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothCommunWrapper serverCommunThread;

    private Handler clientServiceHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case BluetoothUtilities.MSG_CONNECT_SUCCESS:
                    serverCommunThread = new BluetoothCommunWrapper((BluetoothSocket) msg.obj, clientServiceHandler);
                    serverCommunThread.start();
                    Intent successIntent = new Intent(
                            BluetoothUtilities.ACTION_CONNECT_SUCCESS);
                    sendBroadcast(successIntent);
                    break;

                case BluetoothUtilities.MSG_CONNECT_ERROR:
                    Intent errorIntent = new Intent(
                            BluetoothUtilities.ACTION_CONNECT_ERROR);
                    sendBroadcast(errorIntent);
                    break;

                case BluetoothUtilities.MSG_READ_MESSAGE:
                    Intent msgIntent = new Intent(BluetoothUtilities.ACTION_DATA_AVAILABLE);
                    msgIntent.putExtra(BluetoothUtilities.MSG, (Serializable) msg.obj);
                    sendBroadcast(msgIntent);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private BroadcastReceiver serverServiceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothUtilities.ACTION_STOP_SERVICE.equals(action)) {
                if (serverCommunThread != null) {
                    serverCommunThread.stopCommunSocketThread();
                }
                stopSelf();

            } else if (BluetoothUtilities.ACTION_SEND_MESSAGE.equals(action)) {
                BluetoothMessage data = (BluetoothMessage)intent.getSerializableExtra(BluetoothUtilities.MSG);
                if (serverCommunThread != null) {
                    serverCommunThread.sendMessage(data);
                }
            }else if(BluetoothUtilities.ACTION_CONNECT_DEVICE.equals(action)){
                String macAddress = intent.getStringExtra("MacAddress");
                ClientConnectThread ct = new ClientConnectThread(clientServiceHandler, macAddress);
                ct.start();
            }
        }
    };

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothUtilities.ACTION_START_SERVICE);
        filter.addAction(BluetoothUtilities.ACTION_STOP_SERVICE);
        filter.addAction(BluetoothUtilities.ACTION_SEND_MESSAGE);
        filter.addAction(BluetoothUtilities.ACTION_CONNECT_DEVICE);
        registerReceiver(serverServiceReceiver, filter);
        super.onCreate();
    }
    @Override
    public void onDestroy() {
        if (serverCommunThread != null) {
            serverCommunThread.stopCommunSocketThread();
        }
        unregisterReceiver(serverServiceReceiver);
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
