package com.lauren.wordsprout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lauren.wordsprout.data.QuestionSets;

public class IdentifyActivity extends Activity {

	private int currentQuestionCategory = 0;
	private int currentQuestionIndex = 0;
	
	private String[] questions;
	private String[] followups;
	
	private TextView questionView;
	private TextView questionPrompt;
	private ImageView imageView;
	private Button retakeButton;
	private Button nextButton;
	private Uri photoUri;
	
	private final int FROM_CAMERA = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreInstanceState(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_identify);
        questionView = (TextView) findViewById(R.id.question_text);
        imageView = (ImageView) findViewById(R.id.answer_photo);
        retakeButton = (Button) findViewById(R.id.retake_button);
        nextButton = (Button) findViewById(R.id.next_button);
        questionPrompt = (TextView) findViewById(R.id.question_prompt);
        
        currentQuestionCategory = this.getIntent().getExtras().getInt("currentQuestionCategory");
        switch (currentQuestionCategory) {
        	case 0:
        		questions = QuestionSets.shapeQuestions;
        		followups = QuestionSets.shapeFollowup;
        		break;
        	case 1:
        		questions = QuestionSets.colorQuestions;
        		followups = QuestionSets.colorFollowup;
        		break;
        	case 2:
        		questions = QuestionSets.bodyQuestions;
        		followups = QuestionSets.bodyFollowup;
        		break;
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	questionView.setText(questions[currentQuestionIndex]);
    	questionPrompt.setVisibility(View.VISIBLE);
    	trySetPhoto();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (photoUri != null)
            savedInstanceState.putString("photoUri", photoUri.toString());
        savedInstanceState.putInt("currentQuestionIndex", currentQuestionIndex);
    }
    
    protected void restoreInstanceState(Bundle instanceState) {
        if (instanceState == null)
            return;
        if (instanceState.getString("photoUri") != null)
        	photoUri = Uri.parse(instanceState.getString("photoUri"));
        currentQuestionIndex = instanceState.getInt("currentQuestionIndex", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void next(View view) {
    	Log.d("HI", "NEXT!!!!!!!!!!!!");
    	if (currentQuestionIndex < questions.length - 1) {
    		currentQuestionIndex++;
    	}
    	else {
    		Intent img = new Intent(this, ImageActivity.class);
        	img.putExtra("cID", currentQuestionCategory);
    		startActivity(img);
    		return;
    	}
    	questionView.setText(questions[currentQuestionIndex]);
    	trySetPhoto();
    }
    
    public void tryTakePhoto(View view) {
    	File file = new File(getCurrentImagePath());
    	if(!file.exists()) {
    		takePhoto(view);
    	}
    }
    
    public void takePhoto(View view) {
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		ContentValues values = new ContentValues();
		values.put(MediaColumns.TITLE, "a title");
		values.put(ImageColumns.DESCRIPTION, "a description");
		try {
			photoUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
		} catch (UnsupportedOperationException e) {
			try {
				photoUri = getContentResolver().insert(Media.INTERNAL_CONTENT_URI, values);
			} catch (UnsupportedOperationException e2) {
				e2.printStackTrace();
			}
		}
		i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		startActivityForResult(i, FROM_CAMERA);
		Log.d("IDENTIFY", "Invoke: " + photoUri);
    }
    
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        Intent intent;
        switch (reqCode) {
            case FROM_CAMERA:
            	Log.d("IDENTIFY", "From camera: " + photoUri);
            	storeImage(getRealPathFromURI(this, photoUri), Environment.DIRECTORY_PICTURES, this);
            	trySetPhoto();
        }
    }
    
    private void storeImage(String path, String environmentDirectory, Context context) {
        String[] imagePath = path.split("/");
        String fileName = imagePath[imagePath.length - 1];
    	String dst = getCurrentImagePath();
    	copy(path, dst);
        MediaScannerConnection.scanFile(context,
	        new String[] {
	            dst
	        }, null,
	        new MediaScannerConnection.OnScanCompletedListener() {
	            public void onScanCompleted(String path, Uri uri) {
	                Log.d("FileManager", "scanned : " + path);
	            }
	        });
    }
    
    private static void copy(String src, String dst) {
    	try {
	        InputStream in = new FileInputStream(src);
	        OutputStream out = new FileOutputStream(dst);
	
	        // Transfer bytes from in to out
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
    	}
    	catch (IOException e) {
    		Log.d("FileManager", "Errory copying file: " + e.toString());
    	}
    }
    
    private String getRealPathFromURI(Activity activity, Uri contentUri) {
        if (contentUri == null) {
            return null;
        }
        if (contentUri.getScheme() == null) {
            return null;
        }
        if (contentUri.getScheme().equals("file")) {
            return contentUri.getPath();
        }

        String[] proj = {
                MediaColumns.DATA
        };
        android.database.Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
    }
    
    private void trySetPhoto() {
    	File imageFile = new  File(getCurrentImagePath());
    	if (imageFile.exists()){
    		BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
    	    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
    	    bitmap = rotateImage(bitmap, 90);
    	    imageView.setImageBitmap(bitmap);
    	    retakeButton.setVisibility(View.VISIBLE);
    	    nextButton.setVisibility(View.VISIBLE);
    	    questionView.setText(followups[currentQuestionIndex]);
    	    questionPrompt.setText("Now...");
    	}
    	else {
    		imageView.setImageDrawable(getResources().getDrawable(R.drawable.camera_button_camera));
    		retakeButton.setVisibility(View.GONE);
    		nextButton.setVisibility(View.GONE);
    		questionPrompt.setText("I spy");
    	}
    }
    
    private String getCurrentImagePath() {
    	String path = Environment.getExternalStorageDirectory().toString();
        path += "/WordSprout-image-" + currentQuestionCategory + "_" + currentQuestionIndex + ".jpg";
        return path;
    }
    
    private Bitmap rotateImage(Bitmap originalImage, int rotation) {
    	Matrix matrix = new Matrix();
    	matrix.postRotate(rotation);
    	return Bitmap.createBitmap(originalImage, 0, 0, 
    			originalImage.getWidth(), originalImage.getHeight(), 
    	                              matrix, true);
    }
    
    private int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
}
