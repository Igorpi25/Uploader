package com.ivanov.tech.uploader.demo;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.uploader.PhotoMultipartRequest.Params;
import com.ivanov.tech.uploader.Uploader;
import com.ivanov.tech.uploader.R;



/**
 * Created by Igor on 09.05.15.
 */

public class FragmentDemo extends SherlockDialogFragment implements OnClickListener{

    public static final String TAG = FragmentDemo.class
            .getSimpleName();    
    	
    public static final int[]  SIZE= {600,600};
    public static final String FILE_PART_NAME = "image";
    public static final String URL_GET_AVATAR_0="http://94.245.159.1/igorserver/v1/avatars/0";
    public static final String URL_POST_AVATAR_UPLOAD="http://94.245.159.1/igorserver/v1/avatars/upload";

    Button button_upload;
    
    ImageView imageview;    
    
    public static FragmentDemo newInstance() {
    	FragmentDemo f = new FragmentDemo();
        return f;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        
        view=inflater.inflate(R.layout.fragment_tester, container, false);
                
        button_upload = (Button) view.findViewById(R.id.fragment_tester_button_upload);
        button_upload.setOnClickListener(this);
        
        imageview = (ImageView)view.findViewById(R.id.fragment_tester_imageview_avatar);
       
        getAvatarUrlFromServer_And_setAvatarImage();
        
        return view;
    }

    @Override
	public void onClick(View v) {
		if(v.getId()==button_upload.getId())	{
			Uploader.protocolChooseAndUpload( getActivity(), getFragmentManager(),
					new Params()	{
	    				
						@Override
						public String getPartName() {
							return FILE_PART_NAME;					
						}
						
						@Override
						public int[] getSize() {
							return SIZE;
						}
								
						@Override
						public String getUrl() {
							return URL_POST_AVATAR_UPLOAD;
						}	    		
						
					},	
					new Uploader.UploadListener(){

						@Override
						public void onUploaded() {
							getAvatarUrlFromServer_And_setAvatarImage();
						}
						
						@Override
						public void onCancelled() {
							
						}
			
		});
		}
	}
    
    void getAvatarUrlFromServer_And_setAvatarImage(){
    	final String tag = TAG+" getImageUrl ";  
        
    	Log.e(TAG,tag);
    	
    	final ProgressDialog pDialog = new ProgressDialog(getActivity());
    	pDialog.setMessage("Getting image URL...");
    	pDialog.show();     
    	
    	StringRequest request = new StringRequest(Method.GET,
    			URL_GET_AVATAR_0,
    	                new Response.Listener<String>() {
    	 
    	                    @Override
    	                    public void onResponse(String response) {
    	                        Log.d(TAG, tag+"onResponse" + response);
    	                        
    	                        JSONObject json;
    	                        
    	                        try{
    	                        	json=new JSONObject(response);
    	                        
    	                        	if(!json.isNull("success")){	    	                        	
    	                        		if(json.getInt("success")==1){
    	                        				
    	                        				String avatarUrl=json.getJSONObject("avatars").getString("avatar");
    	                        				setAvatarImage(avatarUrl);
    	                        		}
    	                        		else throw (new JSONException(json.getString("message")));
	    	                        }else throw (new JSONException("success=null"));
	    	                        
    	                        }catch(JSONException e){
    	                        	Log.e(TAG,tag+"JSONException "+e.toString());
    	                        	Toast.makeText(getActivity(), "Error: "+e.getMessage(), Toast.LENGTH_LONG);
    	                        }finally{
    	                        	pDialog.hide();    	                        	
    	                        }
    	                        
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                        Log.e(TAG, tag+"Volley.onErrorResponser:" + error.getMessage());
    	                        
    	                        Toast.makeText(getActivity(), "Error: "+error.getMessage(), Toast.LENGTH_LONG);
    	                        pDialog.hide(); 
    	                    }
    	                }){
    		
    		
    		@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Api-Key", Session.getApiKey());
                
                return headers;
            }
    		
    		
    	};
    	 
    	request.setTag(tag);
    	Volley.newRequestQueue(getActivity().getApplicationContext()).add(request);
    }
    
    void setAvatarImage(String url){
    	
    	Log.d(TAG,"setAvatarImage url="+url);
    	try{
    	 Glide.with(this).load(url).placeholder(R.drawable.image_missing).error(R.drawable.loading_error).into(imageview);
    	}catch(Exception e){
    		Log.e(TAG,"setAvatarImage Exception e="+e);
    	}
    }
    
}
