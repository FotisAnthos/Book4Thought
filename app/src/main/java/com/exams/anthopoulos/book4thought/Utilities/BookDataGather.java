package com.exams.anthopoulos.book4thought.Utilities;

import com.exams.anthopoulos.book4thought.BookData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookDataGather {
    private static final String TAG = "BookDataGather";

    private final JSONObject searchResults;
    private final int resultsLimit;
    private ArrayList<BookData> booksList;

    public BookDataGather(JSONObject searchResults, int resultsLimit){
        this.searchResults = searchResults;
        this.resultsLimit = resultsLimit;
    }

    public ArrayList<BookData> getBooksList() {
        return booksList;
    }

    public ArrayList<BookData> dataGather(){
        booksList = new ArrayList<>();
        try {
            JSONArray array = searchResults.getJSONArray("items");

            int resultsLength;
            if(array.length() > resultsLimit) resultsLength = resultsLimit;
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
                }
                //Book Authors
                ArrayList<String> authors = new ArrayList<>();
                try{
                    JSONArray jsAuthors = volumeInfo.getJSONArray("authors");
                    for(int authorIndex=0; authorIndex < jsAuthors.length(); authorIndex++){
                        authors.add(jsAuthors.getString(authorIndex));
                    }
                }catch (JSONException e){
                    //do nothing
                }
                //Book categories
                ArrayList<String> bookCategories = new ArrayList<>();
                try{
                    JSONArray jBookCategories = volumeInfo.getJSONArray("categories");
                    for(int catIndex=0; catIndex < jBookCategories.length(); catIndex++){
                        bookCategories.add(jBookCategories.getString(catIndex));
                    }
                }catch (JSONException e){
                    //do nothing
                }
                //Book rating
                try{
                    rating = volumeInfo.getInt("averageRating");
                }catch (JSONException e){
                    rating = 0;
                }
                //Book ratings count
                try{
                    ratingsCount = volumeInfo.getInt("ratingsCount");
                }catch (JSONException e){
                    ratingsCount = 0;
                }
                //Book description
                try{
                    description = volumeInfo.getString("description");
                }catch (JSONException e){
                    description = "";
                }
                //Book Thumbnail
                try{
                    thumbnailLink = volumeInfo.getJSONObject("imageLinks").getString("smallThumbnail");
                }catch (JSONException e){
                    try{
                        thumbnailLink = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
                    }catch (JSONException e1){
                        thumbnailLink = null;
                    }
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return booksList;
    }
}
