package com.exams.anthopoulos.book4thought;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

class SearchTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "SearchTaskTag";
    private final String USER_AGENT = "Mozilla/5.0";
    private Context context;
    private String[] name = {"query", "intitle", "inauthor", "inpublisher", "subject", "isbn", "lccn", "oclc"};
    private String[] value = new String[8];
    private int length;

    public SearchTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        this.length = strings.length;
        for (int i=0; i<this.length; i++){
            if(strings[i] != null){
                this.value[i] = strings[i];
            }
        }

        String response = searchRequest();
        return null;
    }


    public String urlBuild(){
        //example url: {https://www.googleapis.com/books/v1/volumes?}{q=}flowers{+inauthor:}keyes{&key=}yourAPIKey
        StringBuilder url = new StringBuilder();
        //Base url
        url.append(context.getString(R.string.google_Books_API));
        url.append("volumes?");
        //when a general query is requested
        if(this.value[0] != null){
            url.append("q=");
            StringTokenizer st = new StringTokenizer(this.value[0]);
            if(st.hasMoreTokens()){
                url.append(st.nextToken());
            }
            while(st.hasMoreTokens()){
                url.append("+" + st.nextToken());
            }
        }
        //specific search fields
        for(int i=1; i<this.length; i++){
            if(this.value[i] != null){
                url.append("+"+this.name[i]+":");

                StringTokenizer st = new StringTokenizer(this.value[i]);
                if(st.hasMoreTokens()){
                    url.append(st.nextToken());
                }
                while(st.hasMoreTokens()){
                    url.append("+" + st.nextToken());
                }
            }
        }
        //TODO add authentication for user-specific info
        return url.toString();
    }

    public String searchRequest(){
        try {
            URL url = new URL(urlBuild());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            String responseCode = Integer.toString(connection.getResponseCode());
            Log.e(TAG, responseCode);
            return responseCode;

        } catch (MalformedURLException e) {
            Log.e(TAG, "onQueryTextSubmit-MalformedURL");
            return "Exception";
        } catch (IOException e) {
            Log.e(TAG, "onQueryTextSubmit-IOException");
            return "Exception";
        }

    }


}
