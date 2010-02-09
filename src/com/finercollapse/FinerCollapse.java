package com.finercollapse;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FinerCollapse extends Activity {
    private GameView gameView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        gameView = (GameView) findViewById(R.id.board);
        gameView.setTextView((TextView) findViewById(R.id.text));
        
        gameView.setMode(GameView.READY);
        
    }
}