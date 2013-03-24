package com.lauren.wordsprout;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

public class ImageActivity extends Activity {
	ArrayList<ImageView> imageViews;
	static int cId; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_images);
		imageViews = new ArrayList<ImageView>();
		imageViews.add((ImageView) findViewById(R.id.img01));
		imageViews.add((ImageView) findViewById(R.id.img02));
        cId = this.getIntent().getExtras().getInt("cID");
        for(int i = 0; i < 2; i++)
        	setupImages(i);
	}
	
	@SuppressLint("NewApi") private void setupImages(int index) {
		File imageFile = new File(getCurrentImagePath(index));
	   	if (imageFile.exists()){
    		BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
    	    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
    	    //Matrix mat = new Matrix();
    	    
    	    ImageView iv = imageViews.get(index);
    	//    iv.setScaleType(ScaleType.MATRIX);
    	  //  mat.postRotate((float) (index+1)*15, iv.getDrawable().getBounds().width()/2
    	    //		, iv.getDrawable().getBounds().height()/2);
    	    
    	    iv.setImageBitmap(bitmap);
    	   iv.setPivotX(iv.getWidth());
    	   // iv.setPivotX(0);
    	   // iv.setPivotY(0);
    	   // iv.setPivotY(iv.getHeight()/2);
    	  
    	   
    	   
    	   //iv.setRotation(90);
    	   
    	   
    	   
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
