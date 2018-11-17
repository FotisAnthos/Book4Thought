package com.exams.anthopoulos.book4thought.DataBases;

import android.provider.BaseColumns;

import static android.provider.BaseColumns._ID;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_AUTHOR;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_CANONICAL_LINK;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_DESCRIPTION;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_THUMBNAIL;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_TITLE;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.TABLE_NAME;

public class SavedBooksContract {
    public static final String CREATE_ENTRIES =   "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_TITLE + " TEXT NOT NULL," +
            COLUMN_NAME_AUTHOR + " TEXT NOT NULL," +
            COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL," +
            COLUMN_NAME_CANONICAL_LINK + " TEXT NOT NULL UNIQUE,"+
            COLUMN_NAME_THUMBNAIL + " BLOB);";

    public static final String DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private SavedBooksContract() {
    }

    public static class SavedBookEntry implements BaseColumns {
        public static final String TABLE_NAME = "book";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_CANONICAL_LINK = "canonicalLink";
        public static final String COLUMN_NAME_THUMBNAIL= "thumbnail";
    }


}
