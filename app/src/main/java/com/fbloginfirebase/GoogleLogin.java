package com.fbloginfirebase;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by SIS310 on 01-Jan-18.
 */

public class GoogleLogin {

    private Activity mActivity;
    GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 540;
    public static final String TAG = GoogleLogin.class.getSimpleName();
    private FirebaseAuth mAuth;

    public GoogleLogin(Activity activity) {
        this.mActivity = activity;
        mAuth = FirebaseAuth.getInstance();
    }

    private GoogleSignInClient getGoogleSignInClientObj() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("211223628822-osf81csmdej225vj5bvqd2qn0ntj1h14.apps.googleusercontent.com")/*get from google api devepoer console*/
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso);
        return mGoogleSignInClient;
    }

    public void signIn() {
        Intent signInIntent = getGoogleSignInClientObj().getSignInIntent();
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Log.e(TAG, "handleSignInResult: name=" + account.getDisplayName() + " email=" + account.getEmail() + " image=" + account.getPhotoUrl());
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    private void signOut() {
        // Firebase sign out
        if(mGoogleSignInClient==null)
            return;
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e(TAG, "firebaseAuthWithGoogle:" + acct.getIdToken());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.e(TAG, "onComplete: name=" + user.getDisplayName() + " email=" + user.getEmail() + " image=" + user.getPhotoUrl());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                        }

                    }
                });
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
                    Log.e(TAG, "Google deleted!");
                    signOut();
                    FirebaseAuth.getInstance().signOut();
                } else {
                    Log.e(TAG, "Something is wrong!");
                }
            }
        });
    }

}
