package com.randroid.btmessenger.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

/**
 * Created by hui on 3/24/15.
 */
public class ClientConnectThread extends Thread{
    private final BluetoothSocket mmSocket;
    private BluetoothCommunWrapper clientCommunThread;
    private Handler clientServiceHandler;
    public ClientConnectThread(Handler handler, String macAddress)
    {
        this.clientServiceHandler = handler;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocket tmpBlueSocket = null;
        try
        {
            bluetoothAdapter.cancelDiscovery();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
            tmpBlueSocket = device.createRfcommSocketToServiceRecord(BluetoothUtilities.BLUETOOTH_COMMUN_UUID);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        mmSocket = tmpBlueSocket;
    }
    public void run()
    {
        try
        {
            mmSocket.connect();
            Message msg = clientServiceHandler.obtainMessage();
            msg.what = BluetoothUtilities.MSG_CONNECT_SUCCESS;
            msg.obj = mmSocket;
            msg.sendToTarget();

        } catch (Exception connectException){

            try
            {
                clientServiceHandler.obtainMessage(BluetoothUtilities.MSG_CONNECT_ERROR)
                        .sendToTarget();
                mmSocket.close();
            } catch (Exception closeException)
            {

            }
        }
    }

    public void disConnectBluetooth()
    {
        try
        {
            if(clientCommunThread != null){
                clientCommunThread.stopCommunSocketThread();
            }
            mmSocket.close();

        } catch (Exception e){
        }
    }

}
