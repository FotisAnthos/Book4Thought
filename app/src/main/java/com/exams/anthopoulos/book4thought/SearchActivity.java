package com.exams.anthopoulos.book4thought;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import org.json.JSONObject;

public class SearchActivity extends BaseActivity {
    private static final String TAG = "SearchActivityTag";
    private JSONObject searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base);
        super.onCreate(savedInstanceState);

        //Fragment initialization - start with the MainFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //main fragment initialization
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
        searchResultsFragment.setSearchResults(searchResults);
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        searchResultsFragment.setArguments(getIntent().getExtras());
        transaction.add(R.id.fragment_container, searchResultsFragment, "searchResultsFragment").commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //get the search query and start the search
        String query = getIntent().getStringExtra("query");
        SearchTask searchTask = new SearchTask(getBaseContext(), new SearchTask.AsyncResponse() {
            @Override
            public void searchFinish(JSONObject output) {
                searchResults = output;
            }
        });
        searchTask.execute(query);

    }
}
