package com.lauren.wordsprout;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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
    	Intent intent = new Intent(this, IdentifyActivity.class);
    	intent.putExtra("currentQuestionCategory", index);
    	startActivity(intent);
    }
}
