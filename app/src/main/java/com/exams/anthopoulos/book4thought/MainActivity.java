package com.exams.anthopoulos.book4thought;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.exams.anthopoulos.book4thought.DataBases.RetrieveBooksDBOperation;
import com.exams.anthopoulos.book4thought.Fragments.DBBooks;
import com.exams.anthopoulos.book4thought.Fragments.LoadingFragment;

import java.util.List;


public class MainActivity extends BaseActivity{
    LoadingFragment loadingFragment;
    List<BookData> savedBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base);
        super.onCreate(savedInstanceState);

        try {
            RetrieveBooksDBOperation retrieve = new RetrieveBooksDBOperation(this, new RetrieveBooksDBOperation.AsyncResponse() {
                @Override
                public void booksRetrieved(List<BookData> savedBooks) {
                    booksReady(savedBooks);
                }
            });

            retrieve.execute();
        }catch (Exception e){
            //Probably database not found/created
        }

        //Fragment initialization - start with the MainFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //main fragment initialization
        loadingFragment = new LoadingFragment();
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        loadingFragment.setArguments(getIntent().getExtras());
        loadingFragment.show(transaction, "loadingFragment");
    }

    public void booksReady(List<BookData> savedBooks) {
        loadingFragment.dismiss();
        this.savedBooks = savedBooks;

        DBBooks dbBooks = new DBBooks();
        dbBooks.setBookList(savedBooks);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_container, dbBooks, "dbBooks");
        transaction.commit();
    }
}
