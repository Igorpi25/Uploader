package com.ivanov.tech.uploader;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.uploader.ui.FragmentChooser;
import com.ivanov.tech.uploader.ui.FragmentPreview;

public class Uploader {
	
	private static final String TAG = "Uploader";
	
	public static void showDialog(final Context context, final FragmentManager fragmentManager, final int container,final PhotoMultipartRequest.Params params, final Status status){
		createPreviewFragment(context,fragmentManager,container,params,status);
	}
	
	public static FragmentPreview createPreviewFragment(final Context context,final FragmentManager fragmentManager, final int container,final PhotoMultipartRequest.Params params, final Status status){

        try{
            if(fragmentManager.findFragmentByTag("FragmentPreview").isVisible()){
                return (FragmentPreview)fragmentManager.findFragmentByTag("FragmentPreview");
            }else{
                throw (new NullPointerException());
            }
            
        }catch(NullPointerException e) {

        	FragmentPreview fragmentchooser = FragmentPreview.newInstance(params,status);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(container, fragmentchooser, "FragmentPreview");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("FragmentPreview");
            fragmentTransaction.commit();
            
            return fragmentchooser;
        }
    }
    
    public static void doRequest(Context context,final PhotoMultipartRequest.Params params, final Status status, final ErrorListener errorlistener) {
    	
    	final String tag=TAG+" doUploadRequest ";
    	
    	
    	PhotoMultipartRequest request=new PhotoMultipartRequest(
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
    
    public interface Status{    	
    	public void onUploaded();
    	public void onCancelled();    	
    }
}
