package com.randroid.btmessenger.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by hui on 3/27/15.
 */
public class BluetoothUtilities {
    public static final UUID BLUETOOTH_COMMUN_UUID = UUID
            .fromString("0f3561b9-bda5-4672-84ff-ab1f98e349b6");
    public static final int MSG_CONNECT_SUCCESS = 0x00000002;
    public static final int MSG_CONNECT_ERROR = 0x00000003;
    public static final int MSG_READ_MESSAGE = 0x00000004;
    public static final int MSG_LISTENING_SUCCESS = 0x00000005;
    public static final int MSG_ESTABLISH_CONNECTION_SUCCESS = 0x00000006;

    public static final String ACTION_CONNECT_SUCCESS = "ACTION_CONNECT_SUCCESS";
    public static final String ACTION_CONNECT_ERROR = "ACTION_CONNECT_ERROR";
    public static final String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_SEND_MESSAGE = "ACTION_SEND_MESSAGE";
    public static final String ACTION_CONNECT_DEVICE = "ACTION_CONNECT_DEVICE";
    public static final String ACTION_LISTENING_SUCCESS = "ACTION_LISTENING_SUCCESS";
    public static final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";


    public static final String MSG = "MSG";
    public static final String space = "      ";
    public static final String DEVICE_BLUETOOTH_PIN_CLIENT = "0000";
    public static volatile RunningMode runningMode = RunningMode.Client;

    public enum RunningMode{
        Server,
        Client
    }
    public static boolean createBond(BluetoothDevice device) throws Exception {
        Method createBondMethod = device.getClass().getMethod("createBond");
        return (Boolean)createBondMethod.invoke(device);
    }

    public static boolean unPair(BluetoothDevice device) throws Exception {
        Method removeBondMethod = device.getClass().getMethod("removeBond");
        return (Boolean)removeBondMethod.invoke(device);
    }

    public static boolean setPin(BluetoothDevice device,String str) throws Exception {
        Method setPinMethod = device.getClass().getMethod("setPin", new Class[]{byte[].class});
        return (Boolean)setPinMethod.invoke(device,new Object[]{str.getBytes()});
    }

    public static void setPairingConfirmation(BluetoothDevice device)throws Exception{
        device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
    }

    public static boolean cancelPairingUserInput(BluetoothDevice device) throws Exception{
        Method cancelPairingUserInputMethod = device.getClass().getMethod("cancelPairingUserInput");
        return (Boolean)cancelPairingUserInputMethod.invoke(device, true);
    }
}
