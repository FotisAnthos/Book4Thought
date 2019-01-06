package com.exams.anthopoulos.book4thought;

import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.exams.anthopoulos.book4thought.DataBases.RetrieveBooksDBOperation;
import com.exams.anthopoulos.book4thought.Fragments.DBBooks;
import com.exams.anthopoulos.book4thought.Fragments.LoadingFragment;
import com.exams.anthopoulos.book4thought.Fragments.MainFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BaseActivityTag";
    private static final int RC_SIGN_IN = 9001;
    private NavigationView navigationView;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean doubleBackToExitPressedOnce = false;
    private Menu menu;
    private LoadingFragment loadingFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int layoutCode = savedInstanceState.getInt("layout");
        Bundle savedInstanceStateDefault = savedInstanceState.getBundle("savedInstanceState");
        setContentView(layoutCode);
        super.onCreate(savedInstanceStateDefault);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //update the UI accordingly
        updateUI(account);
    }


    private void signIn() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        loadingFragment = new LoadingFragment();
        loadingFragment.show(transaction, "loadingFragment");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadingFragment.dismiss();
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        loadingFragment = new LoadingFragment();
        loadingFragment.show(transaction, "loadingFragment");
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingFragment.dismiss();
                        updateUI(null);
                    }
                });
    }

    private void revokeAccessGoogle() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        loadingFragment = new LoadingFragment();
        loadingFragment.show(transaction, "loadingFragment");
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingFragment.dismiss();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(GoogleSignInAccount account) {
        Menu menu = navigationView.getMenu();
        final ImageView profileImage = navigationView.getHeaderView(0).findViewById(R.id.navProfileImage);
        TextView userName = navigationView.getHeaderView(0).findViewById(R.id.navProfileUsername);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.navProfileMail);

        if (account != null) {//if signed in
            menu.findItem(R.id.nav_signIn).setVisible(false);
            menu.findItem(R.id.nav_signOut).setVisible(true);
            menu.findItem(R.id.nav_disconnect).setVisible(true);

            userName.setText(account.getDisplayName());
            email.setText(account.getEmail());

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String url = Objects.requireNonNull(account.getPhotoUrl()).toString();
                    Drawable placeholder = getDrawable(R.drawable.ic_launcher_background);
                    Picasso.get()
                            .load(url)
                            .placeholder(placeholder)
                            .into(profileImage);
                }
            } catch (Exception e) {
                Log.w(TAG, "Could not load profile image");
            }

            /*
            if(profilePicture != null){//if the profilePicture is readily available
                profileImage.setImageBitmap(profilePicture);
                profileImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            else {//else if not locally available start download task
                if(account.getPhotoUrl() != null) {
                    DownloadImageTask DIT = new DownloadImageTask(profileImage, new DownloadImageTask.AsyncResponse(){
                        @Override
                        public void imageDownloadFinish(Bitmap output) {
                            profilePicture = output;
                        }//when Image is downloaded store it
                    });
                    DIT.execute(account.getPhotoUrl().toString());
                }
            }
            */
        } else {//if not signed in
            menu.findItem(R.id.nav_signIn).setVisible(true);
            menu.findItem(R.id.nav_signOut).setVisible(false);
            menu.findItem(R.id.nav_disconnect).setVisible(false);

            userName.setText(R.string.app_name);
            email.setText(R.string.noString);
            profileImage.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
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
        } else if (id == R.id.nav_saved_books) {
            showSavedBooks();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSavedBooks(){
        try {
            RetrieveBooksDBOperation retrieve = new RetrieveBooksDBOperation(this, new RetrieveBooksDBOperation.AsyncResponse() {
                @Override
                public void booksRetrieved(List<BookData> savedBooks) {
                    DBBooks dbBooks = new DBBooks();
                    dbBooks.setBookList(savedBooks);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    for (Fragment fragment:getSupportFragmentManager().getFragments()) {
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                    transaction.add(R.id.fragment_container, dbBooks, "dbBooks");
                    transaction.commit();
                }
            });

            retrieve.execute();
        }catch (Exception e) {
            //Probably database not found/created TODO
            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu //this is the search
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(isConnected()){
                    //prepare the new intent
                    Intent search = new Intent(getBaseContext(), SearchActivity.class);
                    search.putExtra("query", query);
                    //reset the searchView
                    searchView.setQuery("", false);
                    searchView.setIconified(true);
                    searchView.clearFocus();
                    //start the new (search) activity
                    startActivity(search);
                }
                else{
                    String warning = getResources().getString(R.string.not_connected);
                    Toast.makeText(getBaseContext(), warning, Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        SearchView searchItem = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("MainFragment");

        //if the drawer is open, close it
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!searchItem.isIconified()) {
            searchItem.onActionViewCollapsed();
        }
        //else if the current fragment is the "MainFragment"
        else if (mainFragment != null && mainFragment.isVisible()) {
            if (doubleBackToExitPressedOnce) {
                //if back button has already been pressed once
                super.onBackPressed();
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
            //if any other fragments are the current ones, go back
            super.onBackPressed();
        }
    }


    public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
        // AUTHORITY is a unique name, but it is recommended to use the name of the
        // package followed by the name of the class.
        final static String AUTHORITY = "com.exams.anthopoulos.book4thought.SearchSuggestionProvider";

        // Uncomment line below, if you want to provide two lines in each suggestion:
        // public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;
        final static int MODE = DATABASE_MODE_QUERIES;

        public SearchSuggestionProvider() {
            setupSuggestions(AUTHORITY, MODE);
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

}

