package com.randroid.btmessenger.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by hui on 3/27/15.
 */
public class BluetoothCommunWrapper extends Thread{

    private final BluetoothSocket mmBluetoothSocket;
    private final ObjectInputStream mmInStream;
    private final ObjectOutputStream mmOutStream;
    private volatile boolean isRunning = true;
    private Handler serviceHandler;
    public BluetoothCommunWrapper(BluetoothSocket socket, Handler handler){
        mmBluetoothSocket = socket;
        ObjectInputStream tmpIn = null;
        ObjectOutputStream tmpOut = null;
        try{
            tmpOut = new ObjectOutputStream(mmBluetoothSocket.getOutputStream());
            tmpIn = new ObjectInputStream(mmBluetoothSocket.getInputStream());
        }catch(Exception ex){
            try{
                socket.close();
            }catch (Exception e){}
            if(serviceHandler != null){
                serviceHandler.obtainMessage(BluetoothUtilities.MSG_CONNECT_ERROR)
                        .sendToTarget();
            }
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        serviceHandler = handler;
    }

    @Override
    public void run() {

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            if(!isRunning){
                break;
            }
            try {
                // Read from the InputStream
                readFully(mmInStream.readObject());
            } catch (Exception e) {
                System.out.println();
            }
        }
        if(mmInStream != null){
            try{
                mmInStream.close();
            }catch (Exception ex){

            }
        }
        if(mmOutStream != null){
            try{
                mmOutStream.close();
            }catch (Exception ex){

            }
        }
        if(mmBluetoothSocket != null){
            try{
                mmBluetoothSocket.close();
            }catch (Exception ex){

            }
        }
    }
    public void write(Object message) {
        try {
            mmOutStream.flush();
            mmOutStream.writeObject(message);
            mmOutStream.flush();
        } catch (IOException e) {
            System.out.println();
        }
        finally{
            message = null;
        }
    }


    private void readFully(Object objBluetoothMessage)
            throws IOException {

        BluetoothMessage bm = (BluetoothMessage)objBluetoothMessage;
        if(bm != null){
            Message msg = serviceHandler.obtainMessage();
            msg.what = BluetoothUtilities.MSG_READ_MESSAGE;
            msg.obj = bm;
            msg.sendToTarget();
        }
    }
    public void sendMessage(BluetoothMessage bluetoothMessage){
        write(bluetoothMessage);
    }
    public void stopCommunSocketThread(){
        isRunning = false;
    }
}
