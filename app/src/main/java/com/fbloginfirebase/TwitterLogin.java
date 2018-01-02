package com.fbloginfirebase;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by SIS310 on 01-Jan-18.
 */

public class TwitterLogin {

    private Activity mActivity;
    private FirebaseAuth mAuth;
    public TwitterAuthClient mTwitterAuthClient;

    public TwitterLogin(Activity activity) {
        this.mActivity = activity;
        mAuth = FirebaseAuth.getInstance();
        mTwitterAuthClient = new TwitterAuthClient();
    }



    public static final String TAG = TwitterLogin.class.getSimpleName();

    public void initializeTwitterAuthClient() {

        mTwitterAuthClient.authorize(mActivity, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

            @Override
            public void success(final Result<TwitterSession> twitterSessionResult) {
                Log.e(TAG, "success: " + "authentication..");
                handleTwitterSession(twitterSessionResult.data);

            }

            @Override
            public void failure(TwitterException e) {
                Log.e(TAG, "failure: " + "authorize.." + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void handleTwitterSession(TwitterSession session) {
        Log.e(TAG, "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.e(TAG, "onComplete: name="+user.getDisplayName()+" email="+user.getEmail()+" image="+user.getPhotoUrl() );

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(mActivity, "Authentication failed.", Toast.LENGTH_SHORT).show();

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
                    Log.e(TAG, "Twitter deleted!");
                    logoutTwitter();
                    FirebaseAuth.getInstance().signOut();
                } else {
                    Log.e(TAG, "Something is wrong!");
                }
            }
        });
    }


    public static void logoutTwitter() {
        try {
            TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
            if (twitterSession != null) {
                ClearCookies(getApplicationContext());
                // Twitter.getSessionManager().clearActiveSession();
                //Twitter.logOut();
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ClearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}
