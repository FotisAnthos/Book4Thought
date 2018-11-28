package com.exams.anthopoulos.book4thought.DataBases;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.exams.anthopoulos.book4thought.BookData;

import java.io.ByteArrayOutputStream;

import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_AUTHOR;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_CANONICAL_LINK;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_CATEGORY;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_DESCRIPTION;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_RATINGS;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_RATINGS_COUNT;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_THUMBNAIL;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_TITLE;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_WEB_READER_LINK;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.TABLE_NAME;

public class SaveBookDBOperation implements Runnable {
    private static final String TAG = SaveBookDBOperation.class.getCanonicalName();
    private final String description;
    private final Context activity;
    private final String title;
    private String author;
    private final String canonicalLink;
    private final Bitmap thumbnail;
    private String category;
    private final String webReaderLink;
    private final int rating;
    private final int ratingsCount;

    public SaveBookDBOperation(Activity activity, BookData bookData, Bitmap thumbnail) {
        this.activity = activity;
        this.title = bookData.getTitle();
        this.description = bookData.getDescription();
        this.canonicalLink = bookData.getCanonicalLink();
        this.thumbnail = thumbnail;
        this.rating = bookData.getRating();
        this.ratingsCount = bookData.getRatingsCount();
        this.webReaderLink = bookData.getWebReaderLink();
        try{
            this.category = bookData.getCategories().get(0);
        }catch (NullPointerException npe){this.category="";}
        try{
            this.author = bookData.getAuthors().get(0);
        }catch (NullPointerException npe){this.author = "";}

    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] thumbnailByte = bos.toByteArray();

        try {
            DatabaseHelper dbHelp = new DatabaseHelper(activity.getApplicationContext());
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelp.getReadableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_TITLE, title);
            values.put(COLUMN_NAME_AUTHOR, author);
            values.put(COLUMN_NAME_DESCRIPTION, description);
            values.put(COLUMN_NAME_CANONICAL_LINK, canonicalLink);
            values.put(COLUMN_NAME_THUMBNAIL, thumbnailByte);
            values.put(COLUMN_NAME_CATEGORY, category);
            values.put(COLUMN_NAME_RATINGS, rating);
            values.put(COLUMN_NAME_RATINGS_COUNT, ratingsCount);
            values.put(COLUMN_NAME_WEB_READER_LINK, webReaderLink);

            // Insert the new row, returning the primary key value of the new row
            //will fail if book is already present in data base
            long result = db.insertOrThrow(TABLE_NAME, null, values);

            if(result > 0) {
                Toast.makeText(activity, title + " Saved!", Toast.LENGTH_SHORT).show();
            }
            db.close();

        }catch (SQLiteConstraintException SQLiCE){
            //if book is already present in data base, SQLiteConstraintException will be thrown
            Toast.makeText(activity, title +" is already Saved!", Toast.LENGTH_SHORT).show();
        }catch (Throwable e) {
            Log.w(TAG, e.getMessage());
        }
    }
}