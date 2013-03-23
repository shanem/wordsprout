package com.lauren.wordsprout;

import com.lauren.wordsprout.data.QuestionSets;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IdentifyActivity extends Activity {

	private int currentQuestionCategory;
	private int currentQuestionIndex;
	
	private String[] questions;
	
	private TextView questionView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        questionView = (TextView) findViewById(R.id.question_text);

        
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
        questionView.setText(questions[currentQuestionIndex]);
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
    }
}
