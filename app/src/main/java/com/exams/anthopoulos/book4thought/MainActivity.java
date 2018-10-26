package com.exams.anthopoulos.book4thought;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base);
        super.onCreate(savedInstanceState);


        //Fragment initialization - start with the MainFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //main fragment initialization
        MainFragment mainFragment = new MainFragment();
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        mainFragment.setArguments(getIntent().getExtras());
        transaction.add(R.id.fragment_container, mainFragment, "MainFragment").commit();
    }


}
