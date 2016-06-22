package com.ivanov.tech.uploader.demo;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ivanov.tech.uploader.R;

public class FragmentDemoPhoto extends DialogFragment {
	
    private static final String TAG="FragmentDemoPhoto";
     
    String url;
    
    ImageView imageview;
    View layout_dimming;

	public static FragmentDemoPhoto newInstance(String url) {
		
		FragmentDemoPhoto f = new FragmentDemoPhoto();  
        f.url=url;
        
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_photo, container, false);
                
        Log.d(TAG,"onCreateView url="+url);
          
        layout_dimming=view.findViewById(R.id.fragment_photo_layout_dimming);        
        
        imageview=(ImageView)view.findViewById(R.id.fragment_photo_imageview);
        Glide.with(getActivity()).load(url).error(R.drawable.image_missing).placeholder(R.drawable.image_missing).into(imageview);
                       
        return view;
    }
    
}
