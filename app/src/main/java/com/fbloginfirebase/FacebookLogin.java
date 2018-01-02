package com.fbloginfirebase;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by SIS310 on 06-Jul-17.
 */

public class FacebookLogin implements FacebookCallback<LoginResult> {

    public CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private Activity myContext;
    private static final String TAG = FacebookLogin.class.getSimpleName();


    public FacebookLogin(Activity context) {
        this.myContext = context;
        mAuth = FirebaseAuth.getInstance();

    }

    public void setupCallback() {
        if (myContext == null)
            return;
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, FacebookLogin.this);
    }


    public void logoutFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e(TAG, "handleFacebookAccessToken:" + token.getToken());
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(myContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userName = user.getDisplayName();
                            String userEmail = user.getEmail();
                            String userImage = user.getPhotoUrl().toString();
                            String userPhone = user.getPhoneNumber();
                            Log.e(TAG, "onComplete: name=" + userName + " email=" + userEmail + " image=" + userImage + " phone=" + userPhone);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(myContext, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.e(TAG, "onSuccess: ");
        handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
        Log.e(TAG, "onCancel: ");
    }

    @Override
    public void onError(FacebookException error) {
        Log.e(TAG, "onError: " + error.getMessage());
    }

    public void deleteAccount() {
        Log.e(TAG, " deleteAccount");
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
            return;
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "facebook deleted!");
                    logoutFromFacebook();
                    FirebaseAuth.getInstance().signOut();
                } else {
                    Log.e(TAG, "Something is wrong!");
                }
            }
        });
    }
}


