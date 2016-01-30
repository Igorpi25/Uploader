package com.ivanov.tech.uploader.tester;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.uploader.Chooser;
import com.ivanov.tech.uploader.PhotoMultipartRequest.Params;
import com.ivanov.tech.uploader.Uploader;
import com.ivanov.tech.uploader.R;
import com.ivanov.tech.uploader.Uploader.Status;



/**
 * Created by Igor on 09.05.15.
 */

public class FragmentTester extends SherlockDialogFragment implements OnClickListener{

    public static final String TAG = FragmentTester.class
            .getSimpleName();    
    	
    public static final int[]  SIZE= {600,600};
    public static final String FILE_PART_NAME = "image";
    public static final String URL_GET_AVATAR_0="http://121.127.248.83/v1/avatars/0";
    public static final String URL_POST_AVATAR_UPLOAD="http://121.127.248.83/v1/avatars/upload";

    Button button_upload;
    
    ImageView imageview;    
    
    public static FragmentTester newInstance() {
    	FragmentTester f = new FragmentTester();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
    }

    @Override
    public void onStart() {
        super.onStart();
        
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        
        view=inflater.inflate(R.layout.fragment_tester, container, false);
                
        button_upload = (Button) view.findViewById(R.id.fragment_tester_button_upload);
        button_upload.setOnClickListener(this);
        
        imageview = (ImageView)view.findViewById(R.id.fragment_tester_imageview_avatar);
       
        getImageUrl();
        
        return view;
    }

    @Override
	public void onClick(View v) {
		if(v.getId()==button_upload.getId()){
			Choose(getActivity(),getFragmentManager(),new Status(){

				@Override
				public void onUploaded() {
					loadImage(URL_GET_AVATAR_0);
				}

				@Override
				public void onCancelled() {}
				
			});
		}
	}
    
    public static void Choose(final Context context,final FragmentManager fragmentmanager,final Status status){
    	Chooser.showDialog(context, fragmentmanager, R.id.main_container, new Chooser.Status(){

			@Override
			public void onChoosed(String imagePath) {
				Log.e(TAG, "Choose onChoosed imagePath = "+imagePath);
				Toast.makeText(context,"Choose onChoosed", Toast.LENGTH_SHORT);				
				Upload(context,fragmentmanager,imagePath,status);
			}

			@Override
			public void onCancelled() {
				Log.e(TAG, "Choose onCancelled");
				Toast.makeText(context,"Choose onCancelled", Toast.LENGTH_SHORT);
								
			}
    		
    	});
    }
    

    public static void Upload(final Context context,final FragmentManager fragmentmanager,final String imagePath,final Status status){
    	
    	Uploader.showDialog(context, fragmentmanager, R.id.main_container,     			
    		new Params(){
    		
				@Override
				public String getPartName() {return FILE_PART_NAME;}
				
				@Override
				public int[] getSize() {return SIZE;}
				
				@Override
				public String getFilePath() {return imagePath;}
				
				@Override
				public String getUrl() {return URL_POST_AVATAR_UPLOAD;}
    		
    		},
    		new Status(){
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
    		
    		}
    	
    	);
    }
    
    void getImageUrl(){
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
    	                        				loadImage(avatarUrl);
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
    
    void loadImage(String url){
    	Log.d(TAG,"loadImage url="+url);
    	
    	 Glide.with(this).load(url).placeholder(R.drawable.image_missing).error(R.drawable.loading_error).into(imageview);         
    }
    
}
