package com.randroid.btmessenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.randroid.btmessenger.bluetooth.BluetoothMessage;
import com.randroid.btmessenger.bluetooth.BluetoothUtilities;

import java.util.Date;

public class ClientActivity extends ActionBarActivity {


    private MyEditText txtMessage;
    private TextView txtMessageHistory;
    private Button btnSend;
    private String mmacAddress;
    @Override
    protected void onStart() {
        registerReceiver();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        txtMessage = (MyEditText)findViewById(R.id.txtMessage);
        txtMessageHistory = (TextView)findViewById(R.id.txtMessageHistory);
        btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String message = txtMessage.getText().toString();
                if(message.length() == 0){
                     Toast toast = Toast.makeText(ClientActivity.this.getApplicationContext(), "Message can not be empty.", Toast.LENGTH_SHORT);
                     toast.show();
                }
                BluetoothMessage bm = new BluetoothMessage(message);
                Intent sendDataIntent = new Intent(BluetoothUtilities.ACTION_SEND_MESSAGE);
                sendDataIntent.putExtra(BluetoothUtilities.MSG, bm);
                sendBroadcast(sendDataIntent);
                String msg =  "↗️ " + new Date().toLocaleString()
                        + " :\r\n" + BluetoothUtilities.space + bm.getMessage() + "\r\n";
                txtMessageHistory.append(msg);
                txtMessage.setText("");
            }
        });
        Bundle bundle = getIntent().getExtras();
        mmacAddress = bundle.getString("MacAddress");
    }
    private void registerReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothUtilities.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothUtilities.ACTION_CONNECT_SUCCESS);
        intentFilter.addAction(BluetoothUtilities.ACTION_CONNECT_ERROR);
        registerReceiver(serverServiceBroadReceiver, intentFilter);
        if(mmacAddress != null){
            Intent connIntent = new Intent(BluetoothUtilities.ACTION_CONNECT_DEVICE);
            connIntent.putExtra("MacAddress", mmacAddress);
            sendBroadcast(connIntent);
        }
    }

    private BroadcastReceiver serverServiceBroadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothUtilities.ACTION_DATA_AVAILABLE.equals(action)) {
                BluetoothMessage bm = (BluetoothMessage) intent.getExtras()
                        .getSerializable(BluetoothUtilities.MSG);

                String msg =  "↙️ " + new Date().toLocaleString()
                        + " :\r\n" + BluetoothUtilities.space + bm.getMessage() + "\r\n";
                txtMessageHistory.append(msg);

            } else if (BluetoothUtilities.ACTION_CONNECT_SUCCESS.equals(action)) {
                String msg = "☑️ " + new Date().toLocaleString()
                        + " :\r\n" + BluetoothUtilities.space + "Connected to server successfully.\r\n";
                txtMessageHistory.append(msg);
                btnSend.setEnabled(true);
            }else if(BluetoothUtilities.ACTION_CONNECT_ERROR.equals(action)){
                String msg = "☑️ " + new Date().toLocaleString()
                        + " :\r\n" + BluetoothUtilities.space + "Connected to server failed.\r\n";
                txtMessageHistory.append(msg);
                btnSend.setEnabled(false);
            }
        }
    };
    @Override
    protected void onStop() {
        unregisterReceiver();
        super.onStop();
    }
    private void unregisterReceiver(){
        Intent startServerService = new Intent(BluetoothUtilities.ACTION_STOP_SERVICE);
        sendBroadcast(startServerService);
        unregisterReceiver(serverServiceBroadReceiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
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
}
