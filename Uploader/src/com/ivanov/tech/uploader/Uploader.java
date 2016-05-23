package com.ivanov.tech.uploader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.uploader.ui.FragmentChooser;
import com.ivanov.tech.uploader.ui.FragmentPhoto;
import com.ivanov.tech.uploader.ui.FragmentPreview;

public class Uploader {
	
	private static final String TAG = "Uploader";
	
	public static FragmentChooser showFragmentChooser(final Context context,final FragmentManager fragmentManager, final int container,final ChooseListener statusListener){

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
	
	public static FragmentPreview showFragmentPreview(final Context context,final FragmentManager fragmentManager, final int container,final String filepath,final PhotoMultipartRequest.Params params, final UploadListener status){

        try{
            if(fragmentManager.findFragmentByTag("FragmentPreview").isVisible()){
                return (FragmentPreview)fragmentManager.findFragmentByTag("FragmentPreview");
            }else{
                throw (new NullPointerException());
            }
            
        }catch(NullPointerException e) {

        	FragmentPreview fragmentchooser = FragmentPreview.newInstance(filepath,params,status);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(container, fragmentchooser, "FragmentPreview");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("FragmentPreview");
            fragmentTransaction.commit();
            
            return fragmentchooser;
        }
    }
    
	public static FragmentPhoto showFragmentPhoto(final Context context,final FragmentManager fragmentManager, final int container,final String filepath){

        try{
            if(fragmentManager.findFragmentByTag("FragmentPhoto").isVisible()){
                return (FragmentPhoto)fragmentManager.findFragmentByTag("FragmentPhoto");
            }else{
                throw (new NullPointerException());
            }
            
        }catch(NullPointerException e) {

        	FragmentPhoto fragmentphoto = FragmentPhoto.newInstance(filepath);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(container, fragmentphoto, "FragmentPhoto");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("FragmentPhoto");
            fragmentTransaction.commit();
            
            return fragmentphoto;
        }
    }
	
	public static void protocolChooseAndUpload(final Context context, final FragmentManager fragmentmanager, final PhotoMultipartRequest.Params params, final UploadListener status){
    	
    	Uploader.showFragmentChooser(context, fragmentmanager, R.id.main_container, new Uploader.ChooseListener(){

			@Override
			public void onChoosed(final String imagePath) {
				Log.e(TAG, "Choose onChoosed imagePath = "+imagePath);
							
				Uploader.showFragmentPreview(context, fragmentmanager, R.id.main_container,imagePath,params, new UploadListener(){
			    		
							@Override
							public void onUploaded() {
								Log.e(TAG, "Upload onUploaded");
								Toast.makeText(context,"Upload onUploaded", Toast.LENGTH_SHORT);
								status.onUploaded();
							}
				
							@Override
							public void onCancelled() {
								Log.e(TAG, "Upload onCancelled");
								Toast.makeText(context,"Upload onCancelled", Toast.LENGTH_SHORT);
								status.onCancelled();
							}
			    		
			    		});
			
			}

			@Override
			public void onCancelled() {
				Log.e(TAG, "Choose onCancelled");
			}
    		
    	});
    }
	
    public static void doRequestUpload(Context context, final String filePath, final PhotoMultipartRequest.Params params, final UploadListener status, final ErrorListener errorlistener) {
    	
    	final String tag=TAG+" doUploadRequest ";
    	
    	
    	PhotoMultipartRequest request=new PhotoMultipartRequest(filePath,
    			params,
    			new Listener<String>(){

					@Override
					public void onResponse(String response) {
						Log.e(TAG, tag +"onResponse "+response);
						status.onUploaded();
					}
    				
    			},errorlistener){
    		@Override
    	    public Map<String, String> getHeaders() throws AuthFailureError {
    	        Map<String, String> headers = super.getHeaders();

    	        if (headers == null
    	                || headers.equals(Collections.emptyMap())) {
    	            headers = new HashMap<String, String>();
    	        }
    	        headers.put("Api-Key", Session.getApiKey());
    	        

    	        Log.e(TAG, "getHeaders: " + headers.toString());
    	        
    	        
    	        return headers;
    	    }
    	};
    	
    	request.setTag(tag);
    	request.setRetryPolicy(new DefaultRetryPolicy(1000*60,1,0));
    	
    	Volley.newRequestQueue(context.getApplicationContext()).add(request);

    }
    
    public interface UploadListener{
    	public void onUploaded();
    	public void onCancelled();    	
    }
    
    public interface ChooseListener{    	
    	public void onChoosed(String imagePath);
    	public void onCancelled(); 
    }

}
