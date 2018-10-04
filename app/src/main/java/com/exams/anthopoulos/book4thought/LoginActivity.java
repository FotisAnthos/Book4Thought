package com.exams.anthopoulos.book4thought;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = "LoginActivity";
    private ProgressDialog dialog;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    private static final int RC_SIGN_IN = 9001;  // The request code


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mStatusTextView = findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope("https://www.googleapis.com/auth/books"))
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END build_client]
    }

    @Override
    public void onStart() {
        super.onStart();
        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    public void signInGoogle(){
        dialog = ProgressDialog.show(this, "","Loading. Please wait...", true);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialog.dismiss();
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else{
            Log.w(TAG, "onActivityResult: requestCode != RC_SIGN_IN");
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.w(TAG, "Sign in - success");
            updateUI(account);
        } catch (ApiException e) {
            switch (e.getStatusCode()) {
                //https://github.com/googlesamples/google-services/issues/374
                case GoogleSignInStatusCodes.CANCELED:
                    Log.w(TAG, "Sign in - CANCELED");
                    break;
                case GoogleSignInStatusCodes.NETWORK_ERROR:
                    Log.w(TAG, "Sign in - NETWORK_ERROR");
                    break;
                case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                    Log.w(TAG, "Sign in - SIGN_IN_CANCELLED");
                    break;
                case GoogleSignInStatusCodes.ERROR:
                    Log.w(TAG, "Sign in - ERROR");
                    break;
            }
            updateUI(null);
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    // [START signOut]
    private void signOutGoogle() {
        dialog = ProgressDialog.show(this, "","Loading. Please wait...", true);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        dialog.dismiss();
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccessGoogle() {
        dialog = ProgressDialog.show(this, "","Loading. Please wait...", true);
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        dialog.dismiss();
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]



    private void updateUI(@Nullable GoogleSignInAccount account) {
        ImageView profilePicture = findViewById(R.id.profilePicture);
        TextView mail = findViewById(R.id.mail);
        if (account != null) {
            mStatusTextView.setText(account.getDisplayName());
            mail.setText(account.getEmail());

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            findViewById(R.id.details).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mail.setText("");
            profilePicture.setImageURI(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.details).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signInGoogle();
                break;
            case R.id.sign_out_button:
                signOutGoogle();
                break;
            case R.id.disconnect_button:
                revokeAccessGoogle();
                break;
        }
    }


}

