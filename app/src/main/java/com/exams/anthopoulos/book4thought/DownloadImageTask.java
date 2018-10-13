package com.exams.anthopoulos.book4thought;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

    public interface AsyncResponse {
        void processFinish(Bitmap output);
    }
    private static final String TAG = "DownloadImageTaskTag";
    private ImageView bmImage;
    public AsyncResponse response = null;

    protected DownloadImageTask(ImageView bmImage, AsyncResponse response) {
        this.bmImage = bmImage;
        this.response = response;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if(result != null){
            bmImage.setImageBitmap(result);
            bmImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
        response.processFinish(result);
    }
}

