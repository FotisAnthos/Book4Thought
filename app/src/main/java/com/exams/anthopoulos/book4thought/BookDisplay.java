package com.exams.anthopoulos.book4thought;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class BookDisplay extends BaseActivity implements BookDisplayFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        BookData bookData = intent.getParcelableExtra("bookData");
        getSupportActionBar().setTitle(bookData.getTitle());

        BookDisplayFragment bookDisplayFragment = new BookDisplayFragment();
        bookDisplayFragment.setArguments(bookData);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, bookDisplayFragment, "bookDisplayFragment").commit();

    }

    public void onFragmentInteraction(BookData bookData){
        BookDisplayFragment bookDisplayFragment = (BookDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (bookDisplayFragment != null) {
            //TODO view the book
        }

    }
}
