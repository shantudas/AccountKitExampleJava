package com.snipex.shantu.accountkitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private TextView tvUserPhone;
    private Button btnLogOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        initialization();

        getLoggedPhoneNumber();


    }

    /**
     * get current logged in phone number
     *
     * Note:: make sure in your facebook account dashboard settings are like
     *
     * Turn off the option "App Require Secret"
     * Make sure the option "Enable Client Access Token Flow " has turned on, set to "YES"
     *
     * And, In your AccountKitConfiguration.AccountKitConfigurationBuilder
     * Use AccountKitActivity.ResponseType.TOKEN instead of AccountKitActivity.ResponseType.CODE
     */
    private void getLoggedPhoneNumber() {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                if (phoneNumber != null) {
                    String phoneNumberString = phoneNumber.toString();
                    Log.d(TAG, "onSuccess: phoneNumberString" + phoneNumberString);
                    tvUserPhone.setText("Logged in with " + phoneNumberString);
                }
            }

            @Override
            public void onError(AccountKitError accountKitError) {
                Log.d(TAG, "onError: accountKitError" + accountKitError);
            }
        });
    }

    private void initialization() {
        tvUserPhone = findViewById(R.id.tvUserPhone);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogOut:
                goToLogInActivity();
                break;
        }
    }

    private void goToLogInActivity() {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
