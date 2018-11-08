package com.exams.anthopoulos.book4thought;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;


public class BookDisplay extends BaseActivity implements BookDisplayFragment.OnFragmentInteractionListener{

    private static final String TAG = "BookDisplayTag";
    private BookDisplayFragment bookDisplayFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        BookData bookData = intent.getParcelableExtra("bookData");
        getSupportActionBar().setTitle(bookData.getTitle());

        BookDisplayFragment bookDisplayFragment = new BookDisplayFragment();
        if (savedInstanceState == null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            bookDisplayFragment = new BookDisplayFragment();
            bookDisplayFragment.setArguments(bookData);
            fragmentTransaction.add(R.id.fragment_container, bookDisplayFragment, "bookDisplayFragment");
            fragmentTransaction.commit();
        } else {
            this.bookDisplayFragment = (BookDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onFragmentInteraction(BookData bookData){
        BookDisplayFragment bookDisplayFragment = (BookDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (bookDisplayFragment != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(bookData.getCanonicalLink()));
                intent.setPackage("com.android.vending");
                startActivity(intent);
            }catch (Exception e){
                Toast toast = Toast.makeText(this, bookData.getTitle() + " not found on Play Store", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }


}
