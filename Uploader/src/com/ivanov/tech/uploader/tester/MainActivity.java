package com.ivanov.tech.uploader.tester;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.session.Session.Status;
import com.ivanov.tech.uploader.R;

/**
 * Created by Igor on 15.01.15.
 */
public class MainActivity extends SherlockFragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Session.Initialize(getApplicationContext());
        
        setContentView(R.layout.activity_main);
        
        showTester();
        
    }


    private void changeFragment(int position) {
        switch (position) {
            case 0:
                showFragment(new FragmentTester());
                break;
        }
    }

    private void showFragment(Fragment currentFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, currentFragment)
                .commit();
    }

    public void showTester(){
    	Session.checkApiKey(getApplicationContext(), getSupportFragmentManager(), R.id.main_container, new Status(){

			@Override
			public void isSuccess() {
				changeFragment(0);				
			}
        });
    }
}
