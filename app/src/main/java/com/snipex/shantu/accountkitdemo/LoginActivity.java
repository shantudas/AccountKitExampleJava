package com.snipex.shantu.accountkitdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private static final Integer APP_REQUEST_CODE = 1001;
    private int SKIN_BACKGROUND_IMAGE = R.drawable.gradient_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        printKeyHash(); // print key hash

        setBackGroundAnimation();


        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        Log.d(TAG, "onCreate: accessToken" + accessToken);

        if (accessToken != null) {
            goToMyLoggedInActivity();
        }


        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePhoneLogIn();

            }
        });

    }


    /**
     * set background animation
     *
     * @param @null
     */
    private void setBackGroundAnimation() {
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.root_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
    }

    /**
     * Handle phone login with account kit
     * Setting up a configuration for account kit.
     * Bangladesh(BD) set as default country
     * and finally call activity result
     *
     * @param @null
     */
    private void handlePhoneLogIn() {
        final Intent intent = new Intent(LoginActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN and .ResponseType.CODE
        // ... perform additional configuration ...

        // to change the color of account kit skin
        UIManager uiManager = new SkinManager(
                SkinManager.Skin.CLASSIC,
                ContextCompat.getColor(LoginActivity.this, android.R.color.holo_blue_dark),
                SKIN_BACKGROUND_IMAGE,
                SkinManager.Tint.BLACK,
                0.50D

        );
        configurationBuilder.setUIManager(uiManager);
        configurationBuilder.setDefaultCountryCode("BD");
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    /**
     * calling onActivityResult from handlePhoneLogin
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;

            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                showErrorActivity(loginResult.getError());
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format("Success:%s...", loginResult.getAuthorizationCode().substring(0, 10));
                }
                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                goToMyLoggedInActivity();
            }

            // Surface the result to your user in an appropriate way.
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method is called when account kit gets any error
     *
     * @param error
     */
    private void showErrorActivity(AccountKitError error) {
        Log.d(TAG, "showErrorActivity: " + error);
    }

    /**
     * This method is called when user verify his/her phone number
     * sends user's to logged in activity
     *
     * @param @null
     */
    private void goToMyLoggedInActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }


    /**
     * get key hash for facebook account kit settings
     * This key hash will be saved in account kit's android platform's debug key hash
     *
     * @param @null
     */
    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.snipex.shantu.accountkitdemo", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
