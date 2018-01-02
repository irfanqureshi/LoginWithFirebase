package com.fbloginfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private FacebookLogin mFacebookLogin;
    private Button fbLogin, fbDelete, googleLogin, googleDelete, twitterLogin, twitterDelete;
    private GoogleLogin mGoogleLogin;
    private TwitterLogin mTwitterLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fbkeyhash.getKeyHash(this, "com.fbloginfirebase");
        FacebookSdk.sdkInitialize(getApplicationContext());
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("nszIDyhFtcGl6vUEwM3g1S5Od", "DX2WVIMXgARuwm9jSCnQ34cU2cQu4oYzJK5Du9lOh0anWquvc4"))
                .debug(true)
                .build();
        Twitter.initialize(config);

        mGoogleLogin = new GoogleLogin(this);
        mFacebookLogin = new FacebookLogin(this);
        mTwitterLogin = new TwitterLogin(this);

        mFacebookLogin.setupCallback();
        fbLogin = findViewById(R.id.fbLoginId);
        fbLogin.setOnClickListener(this);
        fbDelete = findViewById(R.id.fbDeleteId);
        fbDelete.setOnClickListener(this);
        googleLogin = findViewById(R.id.googleLoginId);
        googleLogin.setOnClickListener(this);
        googleDelete = findViewById(R.id.googleDeleteId);
        googleDelete.setOnClickListener(this);
        twitterDelete = findViewById(R.id.twitterDeleteId);
        twitterDelete.setOnClickListener(this);
        twitterLogin = findViewById(R.id.twiiterLoginId);
        twitterLogin.setOnClickListener(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mFacebookLogin.callbackManager.onActivityResult(requestCode, resultCode, data);
        mTwitterLogin.mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GoogleLogin.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            mGoogleLogin.handleSignInResult(task);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fbLoginId:
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile"));
                break;

            case R.id.fbDeleteId:
                if (mFacebookLogin != null)
                    mFacebookLogin.deleteAccount();
                break;

            case R.id.googleLoginId:
                mGoogleLogin.signIn();
                break;

            case R.id.googleDeleteId:
                mGoogleLogin.deleteAccount();
                break;

            case R.id.twiiterLoginId:
                mTwitterLogin.initializeTwitterAuthClient();
                break;

            case R.id.twitterDeleteId:
                mTwitterLogin.deleteAccount();
                break;
        }
    }
}
