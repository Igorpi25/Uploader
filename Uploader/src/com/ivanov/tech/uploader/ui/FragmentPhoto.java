package com.ivanov.tech.uploader.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ivanov.tech.uploader.R;

public class FragmentPhoto extends DialogFragment {
	
    private static final String TAG="FragmentDemoPhoto";
     
    String filepath;
    
    ImageView imageview;
    View layout_dimming;
    

	public static FragmentPhoto newInstance(String filepath) {
		
		FragmentPhoto f = new FragmentPhoto();  
        f.filepath=filepath;
        
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_photo, container, false);
                
        Log.d(TAG,"onCreateView url="+filepath);
          
        layout_dimming=view.findViewById(R.id.fragment_photo_layout_dimming);        
        
        imageview=(ImageView)view.findViewById(R.id.fragment_photo_imageview);
        
        imageview.setImageURI(Uri.parse(filepath));
                       
        return view;
    }
    
}
