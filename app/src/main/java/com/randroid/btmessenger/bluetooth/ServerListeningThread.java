package com.randroid.btmessenger.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

/**
 * Created by hui on 3/27/15.
 */
public class ServerListeningThread extends Thread {
    private BluetoothServerSocket serverSocket;
    private volatile boolean isRunning = true;
    private Handler serviceHandler;
    public ServerListeningThread(Handler handler){
        this.serviceHandler = handler;
    }
    public void stopServerListener(){
        isRunning = false;
    }
    private boolean succeededListening = false;
    @Override
    public void run() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        //We might use secure method later. listenUsingInsecureRfcommWithServiceRecord
        while(true){
            if(!isRunning){
                break;
            }
            try{
                serverSocket = adapter.listenUsingRfcommWithServiceRecord("Server",
                        BluetoothUtilities.BLUETOOTH_COMMUN_UUID);
                if(!succeededListening){
                    serviceHandler.obtainMessage(BluetoothUtilities.MSG_LISTENING_SUCCESS)
                            .sendToTarget();
                }
                succeededListening = true;
                BluetoothSocket bluetoothSocket = serverSocket.accept();
                Message msg = serviceHandler.obtainMessage();
                msg.what = BluetoothUtilities.MSG_CONNECT_SUCCESS;
                msg.obj = bluetoothSocket;
                msg.sendToTarget();
            }catch(Exception ex){
                serviceHandler.obtainMessage(BluetoothUtilities.MSG_CONNECT_ERROR)
                        .sendToTarget();
            }finally {
                try{
                    if(serverSocket != null){
                        serverSocket.close();
                    }
                }catch(Exception ex){

                }
            }
        }
    }
}

