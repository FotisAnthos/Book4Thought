package com.exams.anthopoulos.book4thought;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.exams.anthopoulos.book4thought.Fragments.BookDisplayFragment;


public class BookDisplay extends BaseActivity implements BookDisplayFragment.OnFragmentInteractionListener {

    private static final String TAG = "BookDisplayTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        BookData bookData = intent.getParcelableExtra("bookData");
        try{
            getSupportActionBar().setTitle(bookData.getTitle());
        }catch (NullPointerException npe){
            Log.w(TAG, npe.getMessage());
        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
        BookDisplayFragment bookDisplayFragment = new BookDisplayFragment();
        bookDisplayFragment.setArguments(bookData);
        fragmentTransaction.add(R.id.fragment_container, bookDisplayFragment, "bookDisplayFragment");
        fragmentTransaction.commit();


    }

    public void onFragmentInteraction(String bookTitle, String destination){
        BookDisplayFragment bookDisplayFragment = (BookDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (bookDisplayFragment != null) {
            try {//try opening in Google Play
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(destination));
                intent.setPackage("com.android.vending");
                startActivity(intent);
            }catch (Exception e){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(destination));
                Intent chooseIntent = Intent.createChooser(browserIntent , "Choose browser ");
                // Verify that the intent will resolve to an activity
                if (chooseIntent.resolveActivity(getPackageManager()) != null) {
                    // Here we use an intent without a Chooser unlike the next example
                    startActivity(chooseIntent);
                }
                Toast.makeText(this, bookTitle + " not found on Play Store", Toast.LENGTH_SHORT).show();
            }
        }

    }



}
