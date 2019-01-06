package com.exams.anthopoulos.book4thought;

import android.os.Bundle;
import android.support.v4.app.Fragment;


public class MainActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extendedSavedInstanceState = new Bundle();
        extendedSavedInstanceState.putInt("layout", R.layout.activity_base);
        extendedSavedInstanceState.putBundle("savedInstanceState", savedInstanceState);
        super.onCreate(extendedSavedInstanceState);

        for (Fragment fragment:getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

}
