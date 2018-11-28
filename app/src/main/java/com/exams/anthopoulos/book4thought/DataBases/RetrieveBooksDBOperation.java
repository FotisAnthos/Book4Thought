package com.exams.anthopoulos.book4thought.DataBases;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;

import com.exams.anthopoulos.book4thought.BookData;

import java.util.ArrayList;
import java.util.List;

import static com.exams.anthopoulos.book4thought.DataBases.DatabaseHelper.DATABASE_NAME;
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

public class RetrieveBooksDBOperation extends AsyncTask<String, Void, List<BookData>> {
    private static final String TAG = RetrieveBooksDBOperation.class.getCanonicalName();
    private final AsyncResponse response;
    private final String dbPath;

    public interface AsyncResponse {
        void booksRetrieved(List<BookData> savedBooks);
    }

    public RetrieveBooksDBOperation(Activity activity, AsyncResponse response) {
        this.response = response;
        dbPath = activity.getApplicationContext().getDatabasePath(DATABASE_NAME).getPath();
    }


    @Override
    protected List<BookData> doInBackground(String... strings) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        List<BookData> savedBooks = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        try {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TITLE));
                    String author = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AUTHOR));
                    String description = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DESCRIPTION));
                    String canonicalLink = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CANONICAL_LINK));
                    String category = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CATEGORY));
                    String webReaderLink = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_WEB_READER_LINK));
                    int rating =cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_RATINGS));
                    int ratingsCount =cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_RATINGS_COUNT));

                    //get the blob and convert to bitmap
                    byte[] rawThumbnail = cursor.getBlob(cursor.getColumnIndex(COLUMN_NAME_THUMBNAIL));
                    Bitmap thumbnail = BitmapFactory.decodeByteArray(rawThumbnail, 0, rawThumbnail.length);

                    BookData book = new BookData(title, author, description, canonicalLink, thumbnail, category, rating, ratingsCount, webReaderLink);
                    savedBooks.add(book);
                } while (cursor.moveToNext());
            }
            // close db connection
            cursor.close();
            db.close();
        }catch (Exception sqLiteCantOpenDatabaseException){
            Log.i(TAG, "Database could not be opened");
        }
        return savedBooks;
    }

    @Override
    protected void onPostExecute(List<BookData> retrievedBooks) {
        super.onPostExecute(retrievedBooks);
        response.booksRetrieved(retrievedBooks);
    }
}
