package com.finercollapse;

import com.finercollapse.GameView.States;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

public class PlayGame extends Activity {
	
    private GameView mGameView;
	
    /**
     * debugging identifier
     */
    private static final String TAG = "PlayGame";
	
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.game);
    	
    	mGameView = (GameView) findViewById(R.id.Board);
    	mGameView.setScoreView((TextView) findViewById(R.id.Score));
    	mGameView.setState(States.READY);
		
    	//new start().execute(0);
    }
    
    
    //TODO I do not think this is the most elegant solution, but this is what works for now
    private class start extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			SystemClock.sleep(1000);
			
			switch (getIntent().getIntExtra("btn_pressed", -1)) {
			case R.id.easyBtn: 
				mGameView.initNewGame(Constants.Difficulty.EASY);
				break;
			case R.id.medBtn: 
				mGameView.initNewGame(Constants.Difficulty.MEDIUM);
				break;
			default:
				Log.e(TAG, "No btn value was found in bundle");
				break;
			}
			
			return 0;
		}
    	
    }
    
    public void startGame() {
		switch (getIntent().getIntExtra("btn_pressed", -1)) {
		case R.id.easyBtn: 
			mGameView.initNewGame(Constants.Difficulty.EASY);
			break;
		case R.id.medBtn: 
			mGameView.initNewGame(Constants.Difficulty.MEDIUM);
			break;
		default:
			Log.e(TAG, "No btn value was found in bundle");
			break;
		}    	
    }
    
    
    
    
}
