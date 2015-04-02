package com.randroid.btmessenger.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * Created by hui on 3/31/15.
 */
 public class PairingRequestReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BluetoothUtilities.ACTION_PAIRING_REQUEST)) {
            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                try {
                    BluetoothUtilities.setPin(device, BluetoothUtilities.DEVICE_BLUETOOTH_PIN_CLIENT);
                    BluetoothUtilities.setPairingConfirmation(device);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                }
            }
        }
    }
}
