package com.randroid.btmessenger.bluetooth;

import java.io.Serializable;

/**
 * Created by hui on 3/30/15.
 */
public class BluetoothMessage implements Serializable {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;
    public BluetoothMessage(String msg){
        this.message = msg;
    }
    public BluetoothMessage(){

    }
}
