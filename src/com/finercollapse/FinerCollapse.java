package com.finercollapse;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FinerCollapse extends Activity {
    private GameView gameView;
	
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        gameView = (GameView) findViewById(R.id.board);
        gameView.setTextView((TextView) findViewById(R.id.text));
        
        gameView.setState(GameView.READY);
        
    }
}