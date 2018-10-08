package com.exams.anthopoulos.book4thought;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.io.InputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInAccount account;
    private NavigationView navigationView;
    private ProgressDialog dialog;
    private GoogleSignInClient mGoogleSignInClient;
    private Bitmap profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    public void updateUI(GoogleSignInAccount account){
        Menu menu = navigationView.getMenu();
        ImageView profileImage = navigationView.getHeaderView(0).findViewById(R.id.navProfileImage);
        TextView userName = navigationView.getHeaderView(0).findViewById(R.id.navProfileUsername);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.navProfileMail);

        if(account != null){
            menu.findItem(R.id.nav_signIn).setVisible(false);
            menu.findItem(R.id.nav_signOut).setVisible(true);
            menu.findItem(R.id.nav_disconnect).setVisible(true);

            userName.setText(account.getDisplayName());
            email.setText(account.getEmail());
            if(profilePicture != null){
                profileImage.setImageBitmap(profilePicture);
                profileImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            else {
                if(account.getPhotoUrl() != null) {
                    new DownloadImageTask(profileImage).execute(account.getPhotoUrl().toString());
                }
            }
        }
        else{
            menu.findItem(R.id.nav_signIn).setVisible(true);
            menu.findItem(R.id.nav_signOut).setVisible(false);
            menu.findItem(R.id.nav_disconnect).setVisible(false);

            userName.setText(R.string.app_name);
            email.setText(R.string.noString);
            profileImage.setImageResource(R.mipmap.ic_launcher_round);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_search) {
            MenuItem search = findViewById(R.id.action_search);
            search.isChecked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id;
        id = item.getItemId();

        if (id == R.id.nav_signIn) {
            signIn();
        } else if (id == R.id.nav_signOut) {
            signOutGoogle();
        } else if (id == R.id.nav_disconnect) {
            revokeAccessGoogle();
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signIn() {
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
        profilePicture = null;
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

    

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        protected DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            profilePicture = result;
            bmImage.setImageBitmap(result);
            bmImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
    }


}
