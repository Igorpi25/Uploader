package com.ivanov.tech.uploader.ui;


import java.io.File;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ivanov.tech.session.Session;
import com.ivanov.tech.uploader.PhotoMultipartRequest;
import com.ivanov.tech.uploader.PhotoMultipartRequest.Params;
import com.ivanov.tech.uploader.Uploader;
import com.ivanov.tech.uploader.R;
import com.ivanov.tech.uploader.Uploader.Status;



/**
 * Created by Igor on 09.05.15.
 */

public class FragmentPreview extends SherlockDialogFragment implements OnClickListener{

    public static final String TAG = FragmentPreview.class
            .getSimpleName();    
    	
    Uploader.Status status;
    PhotoMultipartRequest.Params params;
    
    Button button_done,button_back;    
    ImageView imageview;
    
    View layout_dimming;
        
    
    public static FragmentPreview newInstance(Params params,Uploader.Status status) {
    	FragmentPreview f = new FragmentPreview();
    	f.params=params;
    	f.status=status;
    	
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
        
        view=inflater.inflate(R.layout.fragment_preview, container, false);
                
        button_done = (Button) view.findViewById(R.id.fragment_preview_button_done);
        button_done.setOnClickListener(this);
        
        button_back = (Button) view.findViewById(R.id.fragment_preview_button_back);
        button_back.setOnClickListener(this);
        
        imageview = (ImageView)view.findViewById(R.id.fragment_preview_imageview_photo);
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        
        // down sizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 8;

        final Bitmap bitmap = PhotoMultipartRequest.getBitmap(params);

        imageview.setImageBitmap(bitmap);
        
        layout_dimming=view.findViewById(R.id.fragment_preview_layout_dimming);
        layout_dimming.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                	close();
                }
                return true;
            }
        });
        
        
        
        return view;
    }

    @Override
	public void onClick(View v) {
		if(v.getId()==button_done.getId()){
			final ProgressDialog pDialog = new ProgressDialog(getActivity());
	    	pDialog.setMessage("Uploading image...");
	    	pDialog.show();
	    	pDialog.setCancelable(false);
	    	
			Uploader.doRequest(getActivity(), params,new Status(){

				@Override
				public void onUploaded() {
					pDialog.hide();
					getFragmentManager().popBackStack();
					status.onUploaded();
					
				}

				@Override
				public void onCancelled() {
					pDialog.hide();			
					close();
					status.onCancelled();
					
				}
				
			}, new ErrorListener(){

				@Override
				public void onErrorResponse(VolleyError arg0) {
					pDialog.hide();
					close();
				}
				
			});
		}else if(v.getId()==button_back.getId()){
			close();
		}
	}
    
    
    
    void close(){
    	getFragmentManager().popBackStack();
        if(status!=null)
        	status.onCancelled();
    }
    
}
