package br.com.chrezende.messengersocketclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import br.com.chrezende.messengersocketclient.R;
import br.com.chrezende.messengersocketclient.utils.SharedPreferencesHelper;


public class LoginActivity extends AppCompatActivity {


    // UI references.
    private AutoCompleteTextView mUsernameView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Check if exists a user saved
        final SharedPreferencesHelper helper = new SharedPreferencesHelper(this, getString(R.string.pref_key));

        String username = helper.getStringParam(getString(R.string.pref_text));
        if (!username.isEmpty()) {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MessengerHomeActivity.class);
            startActivity(intent);
            finish();
        }

        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        Button mUsernameSignInButton = (Button) findViewById(R.id.username_sign_in_button);
        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save the username on shared preferences

                helper.setParam(getString(R.string.pref_text), mUsernameView.getText().toString());

                //Start new activity
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MessengerHomeActivity.class);
                startActivity(intent);
                finish();

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
    }

}

