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

    public SearchTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String query = strings[0];
        String response = searchRequest(query);
        return null;
    }


    public String urlBuild(String searchString){
        StringBuilder url = new StringBuilder();
        url.append(context.getString(R.string.google_Books_API));
        url.append("volumes?q=");

        StringTokenizer st = new StringTokenizer(searchString);
        if(st.hasMoreTokens()){
            url.append(st.nextToken());
        }
        while(st.hasMoreTokens()){
            url.append("+" + st.nextToken());
        }
        return url.toString();
    }

    public String searchRequest(String searchText){
        try {
            URL url = new URL(urlBuild(searchText));
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
