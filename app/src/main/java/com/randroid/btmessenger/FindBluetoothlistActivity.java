package com.randroid.btmessenger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.randroid.btmessenger.bluetooth.BluetoothClientService;
import com.randroid.btmessenger.bluetooth.BluetoothUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindBluetoothlistActivity extends ActionBarActivity {

    private Button btnRescan, btnConnect, btnUnPair;
    private ListView lstBluetoothDevices;
    private String mDeviceName = "";
    private String mDeviceMacAddress = "";
    private int number = 0;
    private BluetoothSocket btSocket = null;
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private List<HashMap<String, String>> fillMaps;
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
        mBluetoothAdapter.cancelDiscovery();
    }

    private SimpleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent startServiceService = new Intent(FindBluetoothlistActivity.this, BluetoothClientService.class);
        startService(startServiceService);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serverlists);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mReceiver, filter);
        lstBluetoothDevices = (ListView) findViewById(R.id.lstBluetoothDevies);
        // create the grid item mapping
        String[] from = new String[] {"deviceId", "deviceName", "deviceMacAddress"};
        int[] to = new int[] { R.id.did, R.id.dName, R.id.dMacAddress };
        fillMaps = new ArrayList<HashMap<String, String>>();
        adapter = new SimpleAdapter(this, fillMaps, R.layout.bluetooth_device_row, from, to);
        lstBluetoothDevices.setAdapter(adapter);
        btnConnect = (Button)this.findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(null == mDeviceName || "".equals(mDeviceName)){
                    Toast.makeText(FindBluetoothlistActivity.this, "No Bluetooth device was selected.", Toast.LENGTH_SHORT).show();
                }else if(null == mDeviceMacAddress || "".equals(mDeviceMacAddress)){
                    Toast.makeText(FindBluetoothlistActivity.this, "No Bluetooth device was selected.", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        mBluetoothAdapter.cancelDiscovery();
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mDeviceMacAddress);
                        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                            BluetoothUtilities.createBond(device);
                        }else if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                            showChatUI();
                        }
                    }catch(Exception ex){
                        System.out.println();
                    }
                }
            }
        });
        btnRescan = (Button)this.findViewById(R.id.Rescan);
        btnRescan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mBluetoothAdapter != null){
                    number = 0;
                    mDeviceName = "";
                    mDeviceMacAddress = "";
                    mBluetoothAdapter.cancelDiscovery();
                    fillMaps.clear();
                    adapter.notifyDataSetChanged();
                    mBluetoothAdapter.startDiscovery();
                }
            }
        });
        btnUnPair = (Button)this.findViewById(R.id.btnUnPair);
        btnUnPair.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(null == mDeviceName || "".equals(mDeviceName)){
                    Toast.makeText(FindBluetoothlistActivity.this, "No Bluetooth device was selected.", Toast.LENGTH_SHORT).show();
                }else if(null == mDeviceMacAddress || "".equals(mDeviceMacAddress)){
                    Toast.makeText(FindBluetoothlistActivity.this, "No Bluetooth device was selected.", Toast.LENGTH_SHORT).show();
                }else{
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mDeviceMacAddress);
                    try{
                        if(BluetoothUtilities.unPair(device)){
                            Toast.makeText(FindBluetoothlistActivity.this, "UnPair device successfully.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(FindBluetoothlistActivity.this, "UnPair device failed.", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception ex){}
                }
            }
        });
        lstBluetoothDevices.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                TextView txtDeviceName = (TextView)arg1.findViewById(R.id.dName);
                mDeviceName = txtDeviceName.getText().toString();
                TextView txtDeviceMacAddress = (TextView)arg1.findViewById(R.id.dMacAddress);
                mDeviceMacAddress = txtDeviceMacAddress.getText().toString();
                Toast.makeText(FindBluetoothlistActivity.this, mDeviceName + " | " + mDeviceMacAddress + " | "+position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        discoverAllAvailableBluetoothDevices();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void discoverAllAvailableBluetoothDevices(){
        //tvTitle.setText("Scanning available Bluetooth Devices...");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            //tvTitle.setText("Device does not support Bluetooth.");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        mBluetoothAdapter.startDiscovery();
        /*Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                tvTitle.append("\r\n" + device.getName() + "\n" + device.getAddress());
            }
        }*/
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                String deviceName = device.getName();
                if(null == deviceName || "".equals(deviceName)){
                    deviceName = "Unknown";
                }
                number ++;
                //tvTitle.append("\r\n" + "* " + deviceName + ", " + device.getAddress());
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("deviceId", "" + number);
                map.put("deviceName", deviceName);
                map.put("deviceMacAddress", device.getAddress());
                fillMaps.add(map);
                adapter.notifyDataSetChanged();
            }
            else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.d("BlueToothTestActivity", "Pairing......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d("BlueToothTestActivity", "Pairing finished");
                        showChatUI();
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d("BlueToothTestActivity", "Pairing cancelled");
                    default:
                        break;
                }
            }
        }
    };
    private void showChatUI(){
        Intent clientIntent = new Intent(FindBluetoothlistActivity.this,
                ClientActivity.class);
        clientIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        clientIntent.putExtra("MacAddress", mDeviceMacAddress);
        startActivity(clientIntent);
    }
}
