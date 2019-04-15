package br.com.chrezende.messengersocketclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import br.com.chrezende.messengersocketclient.ClientSocketASyncTask;
import br.com.chrezende.messengersocketclient.R;
import br.com.chrezende.messengersocketclient.alert.Alert;
import br.com.chrezende.messengersocketclient.socket.ClientSocketService;
import br.com.chrezende.messengersocketclient.utils.SharedPreferencesHelper;

public class MessengerHomeActivity extends AppCompatActivity {

    //Class refs
    private static Boolean isConnected = false;
    private static ProgressBar mLoadingProgressBar;

    //UI Refs
    private EditText mAddressEditText;
    private EditText mPortNumberEditText;
    private static Button mConnectButton;
    private static Button mDisconnectButton;

    private EditText mMessageEditText;
    private static Button mSendMessageButton;

    private static TextView mAddressTextview;
    private static TextView mPortTextview;
    private static TextView mStatusTextview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //GEt refs

        mAddressEditText = findViewById(R.id.address_input_edittext);
        mPortNumberEditText = findViewById(R.id.port_number_editText);
        mConnectButton = findViewById(R.id.address_port_button);
        mDisconnectButton = findViewById(R.id.disconnect_button);

        mAddressTextview = findViewById(R.id.address_textview);
        mPortTextview = findViewById(R.id.port_textview);
        mStatusTextview = findViewById(R.id.connection_status_textview);

        mLoadingProgressBar = findViewById(R.id.connect_server_progressBar);
        /*
         * Check old uses addresses
         */
        final SharedPreferencesHelper helper = new SharedPreferencesHelper(MessengerHomeActivity.this, getString(R.string.pref_key));

        String oldAddress = helper.getStringParam(getString(R.string.pref_address));
        String oldPort = helper.getStringParam(getString(R.string.pref_port));
        if (oldAddress != null || oldPort != null) {
            //If address saves so put on inputs
            mAddressEditText.setText(oldAddress);
            mPortNumberEditText.setText(oldPort);
        }

        //listener buttn connect
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate
                if (!mAddressEditText.getText().toString().isEmpty() &&
                        !mPortNumberEditText.getText().toString().isEmpty()) {

                    //Start service socket task
                    new ClientSocketASyncTask(MessengerHomeActivity.this).execute(
                            mPortNumberEditText.getText().toString(),
                            mAddressEditText.getText().toString()
                    );

                    //Save address on shared preferences
                    helper.setParam(getString(R.string.pref_address), mAddressEditText.getText().toString());
                    helper.setParam(getString(R.string.pref_port), mPortNumberEditText.getText().toString());

                } else {
                    //Show alert
                    Alert.showAlert(MessengerHomeActivity.this, "Atenção", "Verifique o formulário");
                }
            }
        });

        //Button disconnect listener
        mDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Call disconnect service
                    ClientSocketService.disconnectToServer();
                } catch (IOException e) {
                    Alert.showAlert(MessengerHomeActivity.this, "Warning", "Not possible disconnect to server");
                }
            }
        });

        //Mesage layout
        mMessageEditText = findViewById(R.id.message_edittext);
        mSendMessageButton = findViewById(R.id.send_message_button);

        //Message send listener button
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check is empty
                if (!mMessageEditText.getText().toString().isEmpty()) {
                    //send to server
                    ClientSocketService.sendMessageToServer(mMessageEditText.getText().toString(), MessengerHomeActivity.this);
                    mMessageEditText.setText("");
                } else {
                    Alert.showAlert(MessengerHomeActivity.this, "Warning", "Message input could not be empty");
                }
            }

        });


    }

    /**
     * Change info connection status
     *
     * @param state
     * @param address
     * @param port
     */
    public static void changeConnectedState(Boolean state, String address, int port) {
        isConnected = state;
        mAddressTextview.setText("IP: " + address);
        mPortTextview.setText("Port: " + port);
        if (isConnected) {
            mConnectButton.setVisibility(View.GONE);
            mDisconnectButton.setVisibility(View.VISIBLE);
            mStatusTextview.setText("Connected");
        } else {
            mConnectButton.setVisibility(View.VISIBLE);
            mDisconnectButton.setVisibility(View.GONE);
            mStatusTextview.setText("Disconnect");
        }
    }

    /**
     * Show proggress bar when is loading
     *
     * @param isLoading
     */
    public static void showLoading(Boolean isLoading) {
        if (isLoading) {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            mConnectButton.setVisibility(View.GONE);
            mDisconnectButton.setVisibility(View.GONE);
        } else {
            if (isConnected) {
                mConnectButton.setVisibility(View.GONE);
                mDisconnectButton.setVisibility(View.VISIBLE);
            } else {
                mConnectButton.setVisibility(View.VISIBLE);
                mDisconnectButton.setVisibility(View.GONE);
            }
            mLoadingProgressBar.setVisibility(View.GONE);

        }


    }

    /**
     * Logout user shared pref and go to login activoty
     */
    private void logout() {
        SharedPreferencesHelper helper = new SharedPreferencesHelper(this, getString(R.string.pref_key));
        helper.remove(getString(R.string.pref_text));
        helper.remove(getString(R.string.pref_port));
        helper.remove(getString(R.string.pref_address));

        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
