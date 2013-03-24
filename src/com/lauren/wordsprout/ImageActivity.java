package com.lauren.wordsprout;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class ImageActivity extends Activity {
	ArrayList<ImageView> imageViews;
	private int cId; 
	ImageView sharedBtn;
	ImageView restartBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_images);
		imageViews = new ArrayList<ImageView>();
		imageViews.add((ImageView) findViewById(R.id.img01));
		imageViews.add((ImageView) findViewById(R.id.img02));
	
		sharedBtn = (ImageView) findViewById(R.id.sharedBtn);
		restartBtn = (ImageView) findViewById(R.id.restartBtn);
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
		restartBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_OK);
				finish();
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
    	    bitmap = rotateImage(bitmap, 80 + index * 20);
    	    
    	    BlurMaskFilter blurFilter = new BlurMaskFilter(5, BlurMaskFilter.Blur.OUTER);
    	    Paint shadowPaint = new Paint();
    	    shadowPaint.setMaskFilter(blurFilter);
    	    shadowPaint.setShadowLayer(2f, 1f, 1f, Color.BLACK);

    	    int[] offsetXY = new int[2];
    	    Bitmap shadowImage = bitmap.extractAlpha(shadowPaint, offsetXY);
    	    Bitmap shadowImage32 = shadowImage.copy(Bitmap.Config.ARGB_8888, true);


    	    Canvas c = new Canvas(shadowImage32);
    	    c.drawBitmap(bitmap, offsetXY[0], offsetXY[1], null);
    	    
    	    ImageView iv = imageViews.get(index);
    	    
    	    iv.setImageBitmap(shadowImage32);

    	   
    	    iv.setVisibility(View.VISIBLE);
    	}
    	else {
    		imageViews.get(index).setVisibility(View.INVISIBLE);
    	}
	}
	
	   private String getCurrentImagePath(int index) {
	    	String path = Environment.getExternalStorageDirectory().toString();
	        path += "/WordSprout-image-" + cId + "_" + index + ".jpg";
	        return path;
	    }
	   
	    private Bitmap rotateImage(Bitmap originalImage, int rotation) {
	    	Matrix matrix = new Matrix();
	    	matrix.postRotate(rotation);
	    	return Bitmap.createBitmap(originalImage, 0, 0, 
	    			originalImage.getWidth(), originalImage.getHeight(), 
	    	                              matrix, true);
	    }
}
