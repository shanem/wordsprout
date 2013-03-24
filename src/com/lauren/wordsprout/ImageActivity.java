package com.lauren.wordsprout;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class ImageActivity extends Activity {
	ArrayList<ImageView> imageViews;
	static int cId; 
	Button sharedBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_images);
		imageViews = new ArrayList<ImageView>();
		imageViews.add((ImageView) findViewById(R.id.img01));
		imageViews.add((ImageView) findViewById(R.id.img02));
		
		
		sharedBtn = (Button) findViewById(R.id.sharedBtn);
		sharedBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				View view =  findViewById(android.R.id.content).getRootView();
				view.setDrawingCacheEnabled(true);
				BitmapFactory.Options options = new BitmapFactory.Options();
	            options.inSampleSize = 4;
				Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
				String path = Images.Media.insertImage(getContentResolver(), bitmap, "Image to Share", null);
				Uri sharedUri = Uri.parse(path);
				
				Intent sharedIntent = new Intent(Intent.ACTION_SEND);
				//sharedIntent.setType("text/html");
				//sharedIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>This is the text that will be shared.</p>"));
				
				sharedIntent.setType("image/png");
				sharedIntent.putExtra(Intent.EXTRA_STREAM, sharedUri);
				startActivity(Intent.createChooser(sharedIntent, "Share the pictures using..."));
			}
		});
        cId = this.getIntent().getExtras().getInt("cID");
        for(int i = 0; i < 2; i++)
        	setupImages(i);
	}
	
	@SuppressLint("NewApi") private void setupImages(int index) {
		File imageFile = new File(getCurrentImagePath(index));
	   	if (imageFile.exists()){
    		BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
    	    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
    	    //Matrix mat = new Matrix();
    	    
    	    ImageView iv = imageViews.get(index);
    	//    iv.setScaleType(ScaleType.MATRIX);
    	  //  mat.postRotate((float) (index+1)*15, iv.getDrawable().getBounds().width()/2
    	    //		, iv.getDrawable().getBounds().height()/2);
    	    
    	    iv.setImageBitmap(bitmap);
    	    iv.setPivotX(iv.getWidth()/2);
    	   // iv.setPivotX(0);
    	   // iv.setPivotY(0);
    	    iv.setPivotY(iv.getHeight()/2);
    	  
    	   iv.setRotation(20+index*10);
    	   
    	   
    	   
    	   // iv.setImageMatrix(mat);
    	    iv.setVisibility(View.VISIBLE);
    
    	}
    	else {
    		imageViews.get(index).setVisibility(View.INVISIBLE);
    	}
	}
	
	   public static String getCurrentImagePath(int index) {
	    	String path = Environment.getExternalStorageDirectory().toString();
	        path += "/WordSprout-image-" + cId + "_" + index + ".jpg";
	        return path;
	    }
}
