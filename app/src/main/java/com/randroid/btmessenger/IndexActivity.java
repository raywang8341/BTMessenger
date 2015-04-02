package com.randroid.btmessenger;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import com.randroid.btmessenger.bluetooth.BluetoothUtilities;
import com.google.android.gms.common.SignInButton;

public class IndexActivity extends ActionBarActivity {

    // UI references.
    private SignInButton mPlusSignInButton;
    private RadioButton rbRunAsServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        // Find the Google+ sign in button.
        rbRunAsServer = (RadioButton) findViewById(R.id.rdRunAsServer);
        mPlusSignInButton = (SignInButton) findViewById(R.id.plus_sign_in_button);
        setGooglePlusButtonText(mPlusSignInButton, "Start Program");
        mPlusSignInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                boolean runAsServer = rbRunAsServer.isChecked();
                BluetoothUtilities.runningMode = runAsServer? BluetoothUtilities.RunningMode.Server: BluetoothUtilities.RunningMode.Client;
                if(runAsServer){
                    Intent serverIntent = new Intent(IndexActivity.this,
                            ServerActivity.class);
                    serverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(serverIntent);
                }else{
                    Intent serverIntent = new Intent(IndexActivity.this,
                            FindBluetoothlistActivity.class);
                    serverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(serverIntent);
                }
            }
        });
    }
    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }
}



