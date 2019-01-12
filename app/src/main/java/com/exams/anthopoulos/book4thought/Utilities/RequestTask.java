package com.exams.anthopoulos.book4thought.Utilities;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class RequestTask extends AsyncTask<String, Void, JSONObject> {
    private static final String TAG = "RequestTaskTag";
    private final String requestUrl;

    public interface AsyncResponse {
        void searchFinish(JSONObject output);
    }

    private final AsyncResponse response;

    public RequestTask(String requestUrl, AsyncResponse response) {
        this.requestUrl = requestUrl;
        this.response = response;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        return Request();
    }

    private JSONObject Request(){
        BufferedReader bufferedReader;
        StringBuilder stringBuffer = new StringBuilder();
        try {
            URL url = new URL(requestUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            String USER_AGENT = "Mozilla/5.0";
            connection.setRequestProperty("User-Agent", USER_AGENT);

            String responseCode = Integer.toString(connection.getResponseCode());
            Log.w(TAG, responseCode);

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }
            return new JSONObject(stringBuffer.toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "onQueryTextSubmit-MalformedURL");
            Log.e(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, "onQueryTextSubmit-IOException");
            Log.e(TAG, e.getMessage());
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "onQueryTextSubmit-JSONException");
            Log.e(TAG, e.getMessage());
            return null;
        }

    }

    protected void onPostExecute(JSONObject result) {
        response.searchFinish(result);
    }
}
