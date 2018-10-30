package com.exams.anthopoulos.book4thought;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {
    private static final String TAG = "SearchActivityTag";
    private static final int RESULTS_LIMIT = 50;
    private JSONObject searchResults;
    private SearchResultsFragment searchResultsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base);
        super.onCreate(savedInstanceState);
        //Fragment initialization - start with the MainFragment
        //main fragment initialization
        MainFragment mainFragment = new MainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, mainFragment, "searchWaitFragment").commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //get the search query and start the search
        String query = getIntent().getStringExtra("query");
        getSupportActionBar().setTitle(query);
        SearchTask searchTask = new SearchTask(getBaseContext(), new SearchTask.AsyncResponse() {
            @Override
            public void searchFinish(JSONObject output) {
                searchResults = output;
                ArrayList<BookData> booksList = new ArrayList<>();
                try {
                    JSONArray array = searchResults.getJSONArray("items");

                    int resultsLength;
                    if(array.length() > RESULTS_LIMIT) resultsLength = RESULTS_LIMIT;
                    else resultsLength = array.length();

                    for(int index=0; index < resultsLength; index++){//for the items listed in the json
                        JSONObject book = array.getJSONObject(index);
                        JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                        String title, description, thumbnailLink, selfLink, canonicalLink, previewLink;
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

                        BookData bd = new BookData(title, authors, description, selfLink, canonicalLink, thumbnailLink, previewLink);
                        booksList.add(bd);
                    }
                    displayResults(booksList);//when the results are ready, display the search results
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        searchTask.execute(query);

    }

    private void displayResults(List<BookData> booksList){
        searchResultsFragment = new SearchResultsFragment();
        searchResultsFragment.setSearchResults(booksList);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, searchResultsFragment, "searchResultsFragment");
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_search));
        super.onBackPressed();
    }
}
