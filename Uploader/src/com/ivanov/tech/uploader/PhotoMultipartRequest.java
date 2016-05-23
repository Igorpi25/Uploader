package com.ivanov.tech.uploader;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import ch.boye.httpclientandroidlib.entity.ContentType;
import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class PhotoMultipartRequest extends StringRequest {

    
    private static final String TAG="PhotoMultipartRequest";

    private MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private final Response.Listener<String> mListener;
    protected Map<String, String> headers;
    protected Params mParams;
    protected String mFilePath;
    
    public PhotoMultipartRequest(String filePath, Params params,                                 
                                 Listener<String> listener,
                                 ErrorListener errorListener
                                 ) 
    {
        super(Request.Method.POST,params.getUrl(),listener,errorListener);

        mListener = listener;
        mFilePath=filePath;
        mParams=params;
        
        buildMultipartEntity();
        
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        //headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Accept", "application/json");
        
        return headers;
    }

    @Override
    public String getBodyContentType()
    {
        String contentTypeHeader = mBuilder.build().getContentType().getValue();
        return contentTypeHeader;
    }

    @Override
    public byte[] getBody() throws AuthFailureError
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            mBuilder.build().writeTo(bos);
        }
        catch (IOException e)
        {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }
        return bos.toByteArray();
    }
    
    protected void buildMultipartEntity()
    {
    	Log.e("PhotoMultipartRequest", "buildMultipartEntity mImageFile.getName()="+mFilePath);
        
    	mBuilder.addBinaryBody(mParams.getPartName(), getInputStream(mFilePath, mParams), ContentType.create("image"), mFilePath);
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
        
    }
    
    public static final Bitmap getBitmap(String filePath, Params params){
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        
        BitmapFactory.decodeFile(filePath, options);
        float srcMinSize=options.outWidth<options.outHeight?options.outWidth:options.outHeight;
        float dstMaxSize=params.getSize()[0]>params.getSize()[1]?params.getSize()[0]:params.getSize()[1];
        
        float scale=((float)srcMinSize)/dstMaxSize;
        
        if(scale>1.0f){
        	options.inSampleSize=(int)scale;
        }
        options.inJustDecodeBounds=false;
        
        Bitmap srcBmp = BitmapFactory.decodeFile(filePath, options);
        
        Bitmap dstBmp=null;
        float ratio;
        int x,y,width,height;
        
        
        if (srcBmp.getWidth() >= srcBmp.getHeight()){
        	
        	ratio=((float)srcBmp.getHeight())/srcBmp.getWidth();
        	
        	x = Math.round( 0.5f*(1-ratio)*srcBmp.getWidth() );
        	y = 0;		
        	width = Math.round( srcBmp.getWidth()*ratio );
        	height = srcBmp.getHeight();
        	
        }else{

        	ratio=((float)srcBmp.getWidth())/srcBmp.getHeight();
        	
        	x = 0;
        	y = Math.round( 0.5f*(1-ratio)*srcBmp.getHeight() );		
        	width = srcBmp.getWidth();
        	height = Math.round(srcBmp.getHeight()*ratio );
        	
        }
        
        Matrix matrix=new Matrix();
        matrix.postScale(((float)params.getSize()[0])/width, ((float)params.getSize()[1])/height);
        
        dstBmp = Bitmap.createBitmap(
       	     srcBmp, 
       	     x,
       	     y,
       	     width, 
       	     height,
       	     matrix,
       	     false
       	);
        
        return dstBmp;
    }
    
    public static final InputStream getInputStream(String filePath, Params params){
    	
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        getBitmap(filePath, params).compress(CompressFormat.PNG, 0, bos); 
        byte[] data = bos.toByteArray();
        
        final ByteArrayInputStream bs = new ByteArrayInputStream(data);
        
        return bs;
    }
    
    public interface Params{
    	public String getPartName();
    	public int[] getSize();
    	public String getUrl();    	
    }
    
}
