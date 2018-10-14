package com.exams.anthopoulos.book4thought;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivityTag";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInAccount account;
    private NavigationView navigationView;
    private ProgressDialog dialog;
    private GoogleSignInClient mGoogleSignInClient;
    private Bitmap profilePicture;
    private boolean doubleBackToExitPressedOnce = false;

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
                .requestEmail()//
                .requestProfile()
                .requestScopes(new Scope("https://www.googleapis.com/auth/books"))
                .build();
        // [END configure_signin]
        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END build_client]

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Have a Snack(bar)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Fragment initialization - start with the MainFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //main fragment initialization
        MainFragment mainFragment = new MainFragment();
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        mainFragment.setArguments(getIntent().getExtras());
        transaction.add(R.id.fragment_container, mainFragment, "MainFragment").commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);
        //update the UI accordingly
        updateUI(account);
    }

    public void updateUI(GoogleSignInAccount account){
        Menu menu = navigationView.getMenu();
        final ImageView profileImage = navigationView.getHeaderView(0).findViewById(R.id.navProfileImage);
        TextView userName = navigationView.getHeaderView(0).findViewById(R.id.navProfileUsername);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.navProfileMail);

        if(account != null){//if signed in
            menu.findItem(R.id.nav_signIn).setVisible(false);
            menu.findItem(R.id.nav_signOut).setVisible(true);
            menu.findItem(R.id.nav_disconnect).setVisible(true);

            userName.setText(account.getDisplayName());
            email.setText(account.getEmail());

            if(profilePicture != null){//if the profilePicture is locally available
                profileImage.setImageBitmap(profilePicture);
                profileImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            else {//else if not locally available start download task
                if(account.getPhotoUrl() != null) {
                    DownloadImageTask DIT = new DownloadImageTask(profileImage, new DownloadImageTask.AsyncResponse(){
                        @Override
                        public void processFinish(Bitmap output) {
                            profilePicture = output;
                        }//when Image is downloaded store it
                    });
                    DIT.execute(account.getPhotoUrl().toString());
                }
            }
        }
        else{//if not signed in
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

        //if the drawer is open, close it
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            //else if the current fragment is the "MainFragment"
            MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("MainFragment");
            if (fragment != null && fragment.isVisible()) {
                if (doubleBackToExitPressedOnce) {
                    //if back button has already been pressed once
                    super.onBackPressed();
                    return;
                } else {
                    //update back button is already pressed once(revert to not pressed after 2secs)
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);//execute (order 65) after 2secs
                }
            } else {
                //if there any other fragment is the current one(and the drawer is closed), go back
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        SearchView searchItem = (SearchView) menu.findItem(R.id.action_search).getActionView();
        ((SearchView) searchItem).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new SearchTask(getBaseContext()).execute(query);
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, searchResultsFragment, "SearchResultsFragment")
                        .addToBackStack(null)
                        .commit();
                //TODO implement the fragment transition differently, maybe after the search results are available
                //The listener can override the standard behavior by returning true
                // to indicate that it has handled the submit request.
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //TODO add suggestions
                return false;
            }
        });


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
            Log.e(TAG, "onActivityResult: requestCode != RC_SIGN_IN");
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

    private void signOutGoogle() {
        dialog = ProgressDialog.show(this, "","Loading. Please wait...", true);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        updateUI(null);
                    }
                });
    }

    private void revokeAccessGoogle() {
        dialog = ProgressDialog.show(this, "","Loading. Please wait...", true);
        profilePicture = null;
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        updateUI(null);
                    }
                });
    }

}
