package com.ivanov.tech.uploader;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.uploader.Uploader.Status;
import com.ivanov.tech.uploader.ui.FragmentChooser;
import com.ivanov.tech.uploader.ui.FragmentPreview;

public class Chooser {
	
	private static final String TAG = "Chooser";
	
	public static void showDialog(final Context context, final FragmentManager fragmentManager, final int container,final Status statusListener){
		createChooserFragment(context,fragmentManager,container,statusListener);
	}
	
	private static FragmentChooser createChooserFragment(final Context context,final FragmentManager fragmentManager, final int container,final Status statusListener){

        try{
            if(fragmentManager.findFragmentByTag("FragmentChooser").isVisible()){
                return (FragmentChooser)fragmentManager.findFragmentByTag("FragmentChooser");
            }else{
                throw (new NullPointerException());
            }
            
        }catch(NullPointerException e) {

        	FragmentChooser fragmentchooser = FragmentChooser.newInstance(statusListener);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(container, fragmentchooser, "FragmentChooser");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("FragmentChooser");
            fragmentTransaction.commit();
            
            return fragmentchooser;
        }
    }
	    
    public interface Status{    	
    	public void onChoosed(String imagePath);
    	public void onCancelled(); 
    }


}
