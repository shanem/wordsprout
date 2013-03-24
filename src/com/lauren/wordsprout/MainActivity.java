package com.lauren.wordsprout;

import java.io.File;

import com.lauren.wordsprout.data.QuestionSets;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    public void identifyShape(View view) {
    	identify(0);
    }
    
    public void identifyColor(View view) {
    	identify(1);
    }
    
    public void identifyBody(View view) {
    	identify(2);
    }
    
    public void identify(int index) {
    	//Clear images before starting flow.
    	for (int i = 0; i < QuestionSets.shapeQuestions.length; i++) {
    		String path = Environment.getExternalStorageDirectory().toString();
            path += "/WordSprout-image-" + index + "_" + i + ".jpg";
            File f = new File(path);
            if (f.exists()) {
            	f.delete();
            }
    	}
    	
    	Intent intent = new Intent(this, IdentifyActivity.class);
    	intent.putExtra("currentQuestionCategory", index);
    	startActivity(intent);
    }
}
