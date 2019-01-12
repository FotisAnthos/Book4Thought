package com.exams.anthopoulos.book4thought;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.exams.anthopoulos.book4thought.Fragments.MainFragment;
import com.exams.anthopoulos.book4thought.Fragments.SearchResultsFragment;
import com.exams.anthopoulos.book4thought.Utilities.BookDataGather;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends BaseActivity implements SearchResultsFragment.OnFragmentInteractionListener{
    private static final String TAG = "MainActivityTag";
    private JSONObject suggestionsResults;
    private static final int RESULTS_LIMIT = 50;
    private ArrayList<BookData> suggestedBooks;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extendedSavedInstanceState = new Bundle();
        extendedSavedInstanceState.putInt("layout", R.layout.activity_base);
        extendedSavedInstanceState.putBundle("savedInstanceState", savedInstanceState);
        super.onCreate(extendedSavedInstanceState);
        this.context = this;

        //View initialization, show loading Fragment until results are ready to display
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MainFragment mainFragment = new MainFragment();
        transaction.add(R.id.fragment_container, mainFragment, "mainFragment");
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        boolean isSignedIn = GoogleSignIn.hasPermissions(account, new Scope("https://www.googleapis.com/auth/books"));
        updateUI(account, isSignedIn);
    }

    private void updateUI(GoogleSignInAccount account, boolean signedIn){
        if (signedIn) {
            getSuggestions(account);
        } else {
            Toast.makeText(context, "Sign in to view personalized suggestions", Toast.LENGTH_LONG).show();
        }

    }

    private void showSuggestions(){
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
        searchResultsFragment.setSearchResults(suggestedBooks);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, searchResultsFragment, "suggestionsFragment");
        transaction.commit();
    }

    private void getSuggestions(GoogleSignInAccount account){
        SuggestionsTask sk = new SuggestionsTask(this, account, new SuggestionsTask.AsyncResponse() {
            @Override
            public void suggestionsFinish(JSONObject output) {
                suggestionsResults = output;
                if(suggestionsResults != null){
                    BookDataGather gather = new BookDataGather(suggestionsResults, RESULTS_LIMIT);
                    suggestedBooks = gather.dataGather();
                    showSuggestions();
                } else{
                    Toast.makeText(context, "No Suggestions recovered :( ", Toast.LENGTH_LONG).show();
                }
            }
        });
        sk.execute();
    }

    @Override
    public void onRefreshRequest() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        boolean isSignedIn = GoogleSignIn.hasPermissions(account, new Scope("https://www.googleapis.com/auth/books"));
        updateUI(account, isSignedIn);
    }

    private static class SuggestionsTask extends AsyncTask<String, Void, JSONObject>  {
        private final GoogleSignInAccount account;
        private AsyncResponse response;
        private WeakReference<MainActivity> mActivityRef;

        public interface AsyncResponse {
            void suggestionsFinish(JSONObject output);
        }

        public SuggestionsTask(MainActivity activity, GoogleSignInAccount account,  AsyncResponse response) {
            this.response = response;
            this.mActivityRef = new WeakReference<>(activity);
            this.account = account;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            BufferedReader bufferedReader;

            try {
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                        mActivityRef.get().getApplicationContext(),
                        Collections.singleton("https://www.googleapis.com/auth/books"));

                credential.setSelectedAccount(account.getAccount());

                URL url = new URL("https://www.googleapis.com/books/v1/volumes/recommended");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                String USER_AGENT = "Mozilla/5.0";
                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setRequestProperty("authorization", "Bearer " + credential.getToken());

                if(connection.getResponseCode() != 200){
                    String responseCode = Integer.toString(connection.getResponseCode());

                    if(connection.getResponseCode() != 200){
                        Log.i(TAG, "Connection failed: code: "+ responseCode);
                    }
                }else{
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuilder stringBuffer = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        stringBuffer.append(line);
                    }
                    connection.disconnect();
                    return new JSONObject(stringBuffer.toString());
                }
                return null;

            }catch (Exception e ) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            response.suggestionsFinish(result);
        }
    }
    }
