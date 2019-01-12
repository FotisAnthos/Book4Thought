package com.exams.anthopoulos.book4thought;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.exams.anthopoulos.book4thought.Fragments.LoadingFragment;
import com.exams.anthopoulos.book4thought.Fragments.SearchResultsFragment;
import com.exams.anthopoulos.book4thought.Utilities.BookDataGather;
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

    private LoadingFragment loadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extendedSavedInstanceState = new Bundle();
        extendedSavedInstanceState.putInt("layout", R.layout.activity_base);
        extendedSavedInstanceState.putBundle("savedInstanceState", savedInstanceState);
        super.onCreate(extendedSavedInstanceState);



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

        try {
            getSupportActionBar().setTitle(query);
        } catch (NullPointerException npe) {
            Log.w(TAG, npe.getMessage());
        }

        SearchTask searchTask = new SearchTask(getString(R.string.google_Books_API), new SearchTask.AsyncResponse() {
            @Override
            public void searchFinish(JSONObject output) {
                searchResults = output;
                BookDataGather gather = new BookDataGather(searchResults, RESULTS_LIMIT);
                displayResults(gather.dataGather());
            }
        });
        searchTask.execute(query);
    }


    private void displayResults(List<BookData> booksList){
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
        searchResultsFragment.setSearchResults(booksList);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        loadingFragment.dismiss();//search results are ready to be displayed, dismiss the loading fragment
        transaction.replace(R.id.fragment_container, searchResultsFragment, "searchResultsFragment");
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        try{
            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_search));
        }catch (NullPointerException npe){
            Log.w(TAG, npe.getMessage());
        }
        super.onBackPressed();
    }

}
