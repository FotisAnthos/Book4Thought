package com.exams.anthopoulos.book4thought;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.exams.anthopoulos.book4thought.Fragments.LoadingFragment;
import com.exams.anthopoulos.book4thought.Fragments.SearchResultsFragment;
import com.exams.anthopoulos.book4thought.Utilities.SearchTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements SearchResultsFragment.OnFragmentInteractionListener {
    private static final String TAG = "SearchActivityTag";
    private static final int RESULTS_LIMIT = 50;
    private JSONObject searchResults;
    private SearchResultsFragment searchResultsFragment;
    private ArrayList<BookData> booksList;
    private LoadingFragment loadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base);
        super.onCreate(savedInstanceState);
        booksList = new ArrayList<>();

        //View initialization, show loading Fragment until results are ready to display
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        loadingFragment = new LoadingFragment();
        loadingFragment.show(transaction, "loadingFragment");
    }


    @Override
    protected void onStart() {
        super.onStart();
        //get the search query and start the search

        String query = getIntent().getStringExtra("query");
        getSupportActionBar().setTitle(query);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            SearchTask searchTask = new SearchTask(getBaseContext(), new SearchTask.AsyncResponse() {
                @Override
                public void searchFinish(JSONObject output) {
                    searchResults = output;
                    dataGather();
                }
            });
            searchTask.execute(query);

        }   else {
            Toast.makeText(this, "Not connected to the Internet!", Toast.LENGTH_LONG).show();
        }


    }

    private void dataGather(){
        booksList = new ArrayList<>();
        try {
            JSONArray array = searchResults.getJSONArray("items");

            int resultsLength;
            if(array.length() > RESULTS_LIMIT) resultsLength = RESULTS_LIMIT;
            else resultsLength = array.length();

            for(int index=0; index < resultsLength; index++){//for the items listed in the json
                JSONObject book = array.getJSONObject(index);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                String title, description, thumbnailLink, selfLink, canonicalLink, previewLink, id;

                id = book.getString("id");
                title = volumeInfo.getString("title");
                JSONArray jsAuthors = volumeInfo.getJSONArray("authors");
                ArrayList<String> authors = new ArrayList<>();
                for(int authorIndex=0; authorIndex < jsAuthors.length(); authorIndex++){
                    authors.add(jsAuthors.getString(authorIndex));
                }

                description = volumeInfo.getString("description");
                try{
                    thumbnailLink = volumeInfo.getJSONObject("imageLinks").getString("smallThumbnail");
                }catch (JSONException e){
                    thumbnailLink = null;
                }

                selfLink = book.getString("selfLink");
                canonicalLink = volumeInfo.getString("canonicalVolumeLink");
                previewLink = volumeInfo.getString("previewLink");

                BookData bd = new BookData(title, authors, description, selfLink, canonicalLink, thumbnailLink, previewLink, id);
                booksList.add(bd);
            }
            //when the results are ready, display the search results
            displayResults(booksList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayResults(List<BookData> booksList){
        searchResultsFragment = new SearchResultsFragment();
        searchResultsFragment.setSearchResults(booksList);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        loadingFragment.dismiss();//search results are ready to be displayed, dismiss the loading fragment
        transaction.replace(R.id.fragment_container, searchResultsFragment, "searchResultsFragment");
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_search));
        super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction() {
        //TODO for fragment interaction
    }
}
