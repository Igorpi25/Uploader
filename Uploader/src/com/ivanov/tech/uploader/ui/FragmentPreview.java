package com.ivanov.tech.uploader.ui;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.ivanov.tech.uploader.PhotoMultipartRequest;
import com.ivanov.tech.uploader.PhotoMultipartRequest.Params;
import com.ivanov.tech.uploader.Uploader;
import com.ivanov.tech.uploader.R;
import com.ivanov.tech.uploader.Uploader.UploadListener;



/**
 * Created by Igor on 09.05.15.
 */

public class FragmentPreview extends DialogFragment implements OnClickListener{

    public static final String TAG = FragmentPreview.class
            .getSimpleName();    
    	
    Uploader.UploadListener status;
    PhotoMultipartRequest.Params params;
    String filepath;
    
    Button button_done,button_back;    
    ImageView imageview;
    
    View layout_dimming;
        
    
    public static FragmentPreview newInstance(String filePath, Params params,Uploader.UploadListener status) {
    	FragmentPreview f = new FragmentPreview();
    	f.params=params;
    	f.status=status;
    	f.filepath=filePath;
    	
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

        final Bitmap bitmap = PhotoMultipartRequest.getBitmap(filepath,params);

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
        
        imageview.setOnClickListener(this);
        
        return view;
    }

    @Override
	public void onClick(View v) {
		if(v.getId()==button_done.getId()){
			final ProgressDialog pDialog = new ProgressDialog(getActivity());
	    	pDialog.setMessage("Uploading image...");
	    	pDialog.show();
	    	pDialog.setCancelable(false);
	    	
			Uploader.doRequestUpload(getActivity(), filepath,params,new UploadListener(){

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
			
		}else if(v.getId()==imageview.getId()){
			
			Uploader.showFragmentPhoto(getActivity(), getFragmentManager(), R.id.main_container, filepath);
			
		}
	}
    
    void close(){
    	getFragmentManager().popBackStack();
        if(status!=null)
        	status.onCancelled();
    }
    
}
