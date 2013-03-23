package com.lauren.wordsprout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lauren.wordsprout.data.QuestionSets;

public class IdentifyActivity extends Activity {

	private int currentQuestionCategory;
	private int currentQuestionIndex;
	
	private String[] questions;
	
	private TextView questionView;
	private ImageView imageView;
	
	private final int FROM_CAMERA = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        questionView = (TextView) findViewById(R.id.question_text);
        imageView = (ImageView) findViewById(R.id.answer_photo);

        
        currentQuestionIndex = 0;
        currentQuestionCategory = this.getIntent().getExtras().getInt("currentQuestionCategory");
        switch (currentQuestionCategory) {
        	case 0:
        		questions = QuestionSets.shapeQuestions;
        		break;
        	case 1:
        		questions = QuestionSets.colorQuestions;
        		break;
        	case 2:
        		questions = QuestionSets.bodyQuestions;
        		break;
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	questionView.setText(questions[currentQuestionIndex]);
    	trySetPhoto();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void next(View view) {
    	if (currentQuestionIndex < questions.length - 1) {
    		currentQuestionIndex++;
    	}
    	else {
    		currentQuestionIndex = 0;
    	}
    	questionView.setText(questions[currentQuestionIndex]);
    	trySetPhoto();
    }
    
    public void takePhoto(View view) {
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		ContentValues values = new ContentValues();
		startActivityForResult(i, FROM_CAMERA);
    }
    
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        Intent intent;
        switch (reqCode) {
            case FROM_CAMERA:
            	Bitmap photo = (Bitmap) data.getExtras().get("data");
            	imageView.setImageBitmap(photo);
            	
//            	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.put(PreferenceConstants.LAST_NOTIFIED, new Date().getTime());
//                editor.commit();
                
                try {
                	FileOutputStream out = new FileOutputStream(getCurrentImagePath());
                	photo.compress(Bitmap.CompressFormat.PNG, 90, out);
                }
                catch (FileNotFoundException e) {
                	throw new RuntimeException(e);
                }
                break;
        }
    }
    
    private void trySetPhoto() {
    	File imageFile = new  File(getCurrentImagePath());
    	if (imageFile.exists()){
    	    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    	    imageView.setImageBitmap(bitmap);
    	    imageView.setVisibility(View.VISIBLE);
    	}
    	else {
    		imageView.setVisibility(View.INVISIBLE);
    	}
    }
    
    private String getCurrentImagePath() {
    	String path = Environment.getExternalStorageDirectory().toString();
        path += "/WordSprout-image-" + currentQuestionCategory + "_" + currentQuestionIndex + ".jpg";
        return path;
    }
}
