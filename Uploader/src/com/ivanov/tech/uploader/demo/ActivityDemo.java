package com.ivanov.tech.uploader.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ivanov.tech.connection.Connection.ProtocolListener;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.uploader.R;

/**
 * Created by Igor on 15.01.15.
 */
public class ActivityDemo extends AppCompatActivity {

	//Urls for Session
	static final String url_testapikey="http://igorpi25.ru/v1/testapikey";
	static final String url_login="http://igorpi25.ru/v1/login";
	static final String url_register="http://igorpi25.ru/v1/register";
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init Session
        Session.Initialize(getApplicationContext(),url_testapikey,url_login,url_register);
        
        setContentView(R.layout.activity_main);
        
        Session.checkApiKey(this, getSupportFragmentManager(), R.id.main_container, new ProtocolListener(){
			
			@Override
			public void isCompleted() {
				showFragmentDemo();	
			}

			@Override
			public void onCanceled() {
				finish();
			}
        });
    }

    private void showFragmentDemo() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, new FragmentDemo())
                .commit();
    }

}
