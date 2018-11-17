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

import java.io.ByteArrayOutputStream;

import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_AUTHOR;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_CANONICAL_LINK;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_DESCRIPTION;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_THUMBNAIL;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_TITLE;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.TABLE_NAME;

public class SaveBookDBOperation implements Runnable {
    private static final String TAG = SaveBookDBOperation.class.getCanonicalName();
    private String description;
    private Context activity;
    private String title;
    private String author;
    private String canonicalLink;
    private Bitmap thumbnail;
    private long result;

    public SaveBookDBOperation(Activity activity, String title, String author, String description,String canonicalLink, Bitmap thumbnail) {
        this.activity = activity;
        this.title = title;
        this.author = author;
        this.description = description;
        this.canonicalLink = canonicalLink;
        this.thumbnail = thumbnail;
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

            // Insert the new row, returning the primary key value of the new row
            //will fail if book is already present in data base
            result = db.insertOrThrow(TABLE_NAME, null, values);

            if(result > 0) {
                Toast.makeText(activity, title + " Saved!", Toast.LENGTH_SHORT).show();
            }
            db.close();

        }catch (SQLiteConstraintException SQLiCE){
            //if book is already present in data base, SQLiteConstraintException will be thrown
            Toast.makeText(activity, title +" Already Saved!", Toast.LENGTH_SHORT).show();
        }catch (Throwable e) {
            Log.w(TAG, e.getMessage());
        }
    }
}