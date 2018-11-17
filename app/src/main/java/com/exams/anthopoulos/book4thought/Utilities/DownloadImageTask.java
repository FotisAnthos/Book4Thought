package com.exams.anthopoulos.book4thought.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

    public interface AsyncResponse {
        void imageDownloadFinish(Bitmap output);
    }

    private static final String TAG = "DownloadImageTaskTag";
    private ImageView bmImage;
    public AsyncResponse response;

    protected DownloadImageTask(ImageView bmImage, AsyncResponse response) {
        //Download task initialized
        this.bmImage = bmImage;
        this.response = response;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
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
        response.imageDownloadFinish(result);
    }
}

