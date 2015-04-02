package com.randroid.btmessenger.bluetooth;

import android.app.Service;
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
public class BluetoothServerService extends Service {

    private BluetoothCommunWrapper serverCommunThread;

    private Handler serverServiceHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case BluetoothUtilities.MSG_CONNECT_SUCCESS:
                    serverCommunThread = new BluetoothCommunWrapper((BluetoothSocket) msg.obj, serverServiceHandler);
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
                case BluetoothUtilities.MSG_LISTENING_SUCCESS:
                    Intent listenIntent = new Intent(BluetoothUtilities.ACTION_LISTENING_SUCCESS);
                    sendBroadcast(listenIntent);
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
            }
        }
    };

    @Override
    public void onCreate() {
        IntentFilter controlFilter = new IntentFilter();
        controlFilter.addAction(BluetoothUtilities.ACTION_START_SERVICE);
        controlFilter.addAction(BluetoothUtilities.ACTION_STOP_SERVICE);
        controlFilter.addAction(BluetoothUtilities.ACTION_SEND_MESSAGE);
        registerReceiver(serverServiceReceiver, controlFilter);
        ServerListeningThread slt = new ServerListeningThread(serverServiceHandler);
        slt.start();
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
