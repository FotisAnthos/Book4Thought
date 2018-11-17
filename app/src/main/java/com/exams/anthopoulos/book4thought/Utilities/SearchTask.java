package com.exams.anthopoulos.book4thought.Utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.exams.anthopoulos.book4thought.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

public class SearchTask extends AsyncTask<String, Void, JSONObject> {

    public interface AsyncResponse {
        void searchFinish(JSONObject output);
    }

    private static final String TAG = "SearchTaskTag";
    private final String USER_AGENT = "Mozilla/5.0";
    private Context context;
    private String[] name = {"query", "intitle", "inauthor", "inpublisher", "subject", "isbn", "lccn", "oclc"};
    private String[] value = new String[8];
    private int length;
    public AsyncResponse response;

    public SearchTask(Context context, AsyncResponse response) {
        this.context = context;
        this.response = response;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        this.length = strings.length;
        for (int i=0; i<this.length; i++){
            if(strings[i] != null){
                this.value[i] = strings[i];
            }
        }

        JSONObject response = searchRequest();
        return response;
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

        url.append("&orderBy=relevance");

        //url.append("&key="+ BuildConfig.GOOGLE_BOOKS_API_KEY);
        //Log.i(TAG, url.toString());
        return url.toString();
    }

    public JSONObject searchRequest(){
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(urlBuild());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            String responseCode = Integer.toString(connection.getResponseCode());
            Log.d(TAG, responseCode);

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }
            return new JSONObject(stringBuffer.toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "onQueryTextSubmit-MalformedURL");
            return null;
        } catch (IOException e) {
            Log.e(TAG, "onQueryTextSubmit-IOException");
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "onQueryTextSubmit-JSONException");
            return null;
        }

    }

    protected void onPostExecute(JSONObject result) {
        response.searchFinish(result);
    }


}
