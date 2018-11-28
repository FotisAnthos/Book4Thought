package com.exams.anthopoulos.book4thought;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

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

        try {
            getSupportActionBar().setTitle(query);
        } catch (NullPointerException npe) {
            Log.w(TAG, npe.getMessage());
        }

        SearchTask searchTask = new SearchTask(getString(R.string.google_Books_API), new SearchTask.AsyncResponse() {
            @Override
            public void searchFinish(JSONObject output) {
                searchResults = output;
                dataGather();
            }
        });
        searchTask.execute(query);
    }



    private void dataGather(){
        booksList = new ArrayList<>();
        try {
            JSONArray array = searchResults.getJSONArray("items");

            int resultsLength;
            if(array.length() > RESULTS_LIMIT) resultsLength = RESULTS_LIMIT;
            else resultsLength = array.length();

            for(int index=0; index < resultsLength; index++){//for the items listed in the json
                String title, description, thumbnailLink, selfLink, canonicalLink, previewLink, webReaderLink;
                int rating, ratingsCount;

                JSONObject book = array.getJSONObject(index);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                JSONObject accessInfo = book.getJSONObject("accessInfo");
                //Book Title
                try{
                    title = volumeInfo.getString("title");
                }catch (JSONException e){
                    title = "";
                    Log.i(TAG, e.getMessage());
                }
                //Book Authors
                ArrayList<String> authors = new ArrayList<>();
                try{
                    JSONArray jsAuthors = volumeInfo.getJSONArray("authors");
                    for(int authorIndex=0; authorIndex < jsAuthors.length(); authorIndex++){
                        authors.add(jsAuthors.getString(authorIndex));
                    }
                }catch (JSONException e){
                    Log.i(TAG, e.getMessage());
                }
                //Book categories
                ArrayList<String> bookCategories = new ArrayList<>();
                try{
                    JSONArray jBookCategories = volumeInfo.getJSONArray("categories");
                    for(int catIndex=0; catIndex < jBookCategories.length(); catIndex++){
                        bookCategories.add(jBookCategories.getString(catIndex));
                    }
                }catch (JSONException e){
                    Log.i(TAG, e.getMessage());
                }
                //Book rating
                try{
                    rating = volumeInfo.getInt("averageRating");
                }catch (JSONException e){
                    rating = 0;
                    Log.i(TAG, e.getMessage());
                }
                //Book ratings count
                try{
                    ratingsCount = volumeInfo.getInt("ratingsCount");
                }catch (JSONException e){
                    ratingsCount = 0;
                    Log.i(TAG, e.getMessage());
                }
                //Book description
                try{
                    description = volumeInfo.getString("description");
                }catch (JSONException e){
                    description = "";
                    Log.i(TAG, e.getMessage());
                }
                //Book Thumbnail
                try{
                    thumbnailLink = volumeInfo.getJSONObject("imageLinks").getString("smallThumbnail");
                }catch (JSONException e){
                    thumbnailLink = null;
                }

                try{
                    selfLink = book.getString("selfLink");
                }catch (JSONException e){
                    selfLink = "";
                }
                try{
                    previewLink = volumeInfo.getString("previewLink");
                }catch (JSONException e){
                    previewLink = "";
                }
                //Book Canonical Link
                try{
                    canonicalLink = volumeInfo.getString("canonicalVolumeLink");
                }catch (JSONException e){
                    canonicalLink = "";
                }
                //Book web reader link
                try{
                    webReaderLink = accessInfo.getString("webReaderLink");
                }catch (JSONException e){
                    webReaderLink = "";
                }

                BookData bd = new BookData(title, authors, description, selfLink, previewLink, canonicalLink, thumbnailLink, bookCategories, rating, ratingsCount, webReaderLink);
                booksList.add(bd);
            }
            //when the results are ready, display the search results
            displayResults(booksList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
