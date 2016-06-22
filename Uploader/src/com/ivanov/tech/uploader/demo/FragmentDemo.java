package com.ivanov.tech.uploader.demo;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.uploader.PhotoMultipartRequest.Params;
import com.ivanov.tech.uploader.Uploader;
import com.ivanov.tech.uploader.R;



/**
 * Created by Igor on 09.05.15.
 */

public class FragmentDemo extends DialogFragment implements OnClickListener{

    public static final String TAG = FragmentDemo.class
            .getSimpleName();    
    	
    public static final int[]  SIZE= {600,600};
    public static final String FILE_PART_NAME = "image";
    public static final String URL_GET_USERS_0="http://igorpi25.ru/v1/users/0";
    public static final String URL_POST_AVATAR_UPLOAD="http://igorpi25.ru/v1/avatars/upload";

    Button button_upload;    
    ImageView imageview_avatar,imageview_icon;    
    TextView textview_name, textview_changed;
    
    String imageFullUrl=null;
    
    public static FragmentDemo newInstance() {
    	FragmentDemo f = new FragmentDemo();
        return f;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        
        view=inflater.inflate(R.layout.fragment_demo, container, false);
                
        button_upload = (Button) view.findViewById(R.id.fragment_demo_button_upload);
        button_upload.setOnClickListener(this);
        
        imageview_avatar = (ImageView)view.findViewById(R.id.fragment_demo_imageview_avatar);
        imageview_avatar.setOnClickListener(this);
        imageview_icon = (ImageView)view.findViewById(R.id.fragment_demo_imageview_icon);
        
        textview_name = (TextView)view.findViewById(R.id.fragment_demo_textview_name);
        textview_changed = (TextView)view.findViewById(R.id.fragment_demo_textview_changed);
       
        getUsersRequest(URL_GET_USERS_0);
        
        return view;
    }

    @Override
	public void onClick(View v) {
		if(v.getId()==button_upload.getId()){
			//Запуск протокола выбора и отправки фотографии на сервер
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
							getUsersRequest(URL_GET_USERS_0);
						}
						
						@Override
						public void onCancelled() {
							
						}
			
		});
		}else if(v.getId()==imageview_avatar.getId()){
			
			showFragmentDemoPhoto(getActivity(), getFragmentManager(), R.id.main_container, imageFullUrl);
			
		}
	}
    
    public void getUsersRequest(String url){
		
    	final String tag = TAG + " getUsersRequest ";    	 
    	    	         
    	Log.e(TAG,tag);
    	
    	JsonObjectRequest request = new JsonObjectRequest(Method.GET,
    			url, null,
    	                new Response.Listener<JSONObject>() {
    	 
    	                    @Override
    	                    public void onResponse(JSONObject response) {
    	                        Log.d(TAG,  tag+" onResponse "+ response.toString());
    	                        
    	                        try{
    	                        
	    	                        if(!response.isNull("users")){	    	                        	
	    	                        	JSONObject json_user=response.getJSONArray("users").getJSONObject(0);
	    	                        	
	    	                			String name="John Doe";
	    	                			long changed_at=0;	    	                        	
	    	                			
	    	                			if(!json_user.isNull("name"))	name=json_user.getString("name");
	    	                			if(!json_user.isNull("changed_at"))	changed_at=json_user.getLong("changed_at");	    	                			
	    	                			setTextViews(name,changed_at);
	    	                			
	    	                			
	    	                			if(!json_user.isNull("avatars")){
	    	                				
	    	                				String avatarUrl=null,iconUrl=null,fullUrl=null;
	    	                			
	    	                				JSONObject json_avatars=json_user.getJSONObject("avatars");
	    	                				
	    	                				if(!json_avatars.isNull("avatar"))avatarUrl=json_avatars.getString("avatar");
	    	                				if(!json_avatars.isNull("icon"))iconUrl=json_avatars.getString("icon");
	    	                				if(!json_avatars.isNull("full"))fullUrl=json_avatars.getString("full");
                        					
                        					setImageViews(avatarUrl,iconUrl,fullUrl);
	    	                			}
	    	                        }
	    	                        
	    	                        
    	                        }catch(Exception e){
    	                        	Log.e(TAG,tag + "onResponse Exception e : "+e.toString());
    	                        	
    	                        }
    	                        
    	                    }
    	                    
    	                }, new Response.ErrorListener() {
    	 
    	                    @Override
    	                    public void onErrorResponse(VolleyError error) {
    	                    	Log.e(TAG,tag + "onErrorResponse VolleyError error : "+error.toString());
    	                    }
    	                }){
    		
    		@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                
                headers.put("Content-Type", "application/json");
                headers.put("Api-Key", Session.getApiKey());
                
                return headers;
            }
    		
    	};
    	 
    	request.setTag(tag);
    	Volley.newRequestQueue(getActivity()).add(request);
    
	}
    
    void setImageViews(String avatarUrl,String iconUrls, String fullUrl){
    	
    	Log.d(TAG,"setImageViews");
    	
    	try{
    	 Glide.with(this).load(avatarUrl).placeholder(R.drawable.image_missing).error(R.drawable.loading_error).into(imageview_avatar);
    	}catch(Exception e){
    		Log.e(TAG,"setImageViews avatar Exception e="+e);
    	}
    	
    	try{
       	 Glide.with(this).load(iconUrls).placeholder(R.drawable.image_missing).error(R.drawable.loading_error).into(imageview_icon);
       	}catch(Exception e){
       		Log.e(TAG,"setImageViews icon Exception e="+e);
       	}
    	
    	imageFullUrl=fullUrl;
    }
    
    void setTextViews(String name,long changed_at){
    	textview_name.setText(name);
    	
    	textview_changed.setText("Changed: "+timestampToString(changed_at));
    }
    
    void showFragmentDemoPhoto(final Context context,final FragmentManager fragmentManager, final int container,final String url){

        try{
            if(fragmentManager.findFragmentByTag("FragmentDemoPhoto").isVisible()){
                return;
            }else{
                throw (new NullPointerException());
            }
            
        }catch(NullPointerException e) {

        	FragmentDemoPhoto fragmentdemophoto = FragmentDemoPhoto.newInstance(url);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(container, fragmentdemophoto, "FragmentDemoPhoto");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack("FragmentDemoPhoto");
            fragmentTransaction.commit();
        }
    }
  //---------------Timestamp Utilities----------------------------
 	
   	private String timestampToString(long timestamp_unix){
  		
   		long timestamp=timestamp_unix*1000;
   		
  	 	Date date=new Date(timestamp);
  	 	
  	 	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
  	 	String asd=format.format(date);
  	 	
  	 	Log.d(TAG, "timestampToString timestamp="+timestamp+" asd="+asd);
  	 	
  	 	return asd;
  	}
   	

}
