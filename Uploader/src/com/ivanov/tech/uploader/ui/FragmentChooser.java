package com.ivanov.tech.uploader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ivanov.tech.uploader.R;
import com.ivanov.tech.uploader.Uploader;

public class FragmentChooser extends DialogFragment implements OnClickListener{

    public static final String TAG = FragmentChooser.class
            .getSimpleName();    
    	
    //Constants of onResultActivity
    private static final int SELECT_FILE = 1;
    
    Uploader.ChooseListener status;
    
    Button button_gallery,button_camera;
    View layout_dimming;
    
    public static FragmentChooser newInstance(Uploader.ChooseListener status) {
    	FragmentChooser f = new FragmentChooser();
    	f.status=status;
    	
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        
        view=inflater.inflate(R.layout.fragment_chooser, container, false);
                
        button_gallery = (Button) view.findViewById(R.id.fragment_chooser_button_gallery);
        button_gallery.setOnClickListener(this);
        
        button_camera = (Button) view.findViewById(R.id.fragment_chooser_button_camera);
        button_camera.setOnClickListener(this);
        
        layout_dimming=view.findViewById(R.id.fragment_chooser_layout_dimming);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();

                String selectedImagePath = getPath(selectedImageUri);
                
                close();
                status.onChoosed(selectedImagePath);                
                
            }
        }
    }
    
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getActivity()
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
	public void onClick(View v) {
		if(v.getId()==button_gallery.getId()){
			goToGallery();
		}
	}
    
    void goToGallery(){
    	Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                SELECT_FILE);   
    }
    
    
    void close(){
    	getFragmentManager().popBackStack();
        if(status!=null)
        	status.onCancelled();
    }
    
}

