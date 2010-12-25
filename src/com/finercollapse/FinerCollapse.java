package com.finercollapse;

import com.finercollapse.GameView.States;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
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
        
        /* setup buttons on Main Menu */
        final Button easyButton = (Button) findViewById(R.id.easyBtn);
        final Button medButton = (Button) findViewById(R.id.medBtn);
        final Button loseButton = (Button) findViewById(R.id.LoseBtn);
        
        easyButton.setOnClickListener(startGame);
        medButton.setOnClickListener(startGame);
        loseButton.setOnClickListener(goToMainMenu);
        
        gameView = (GameView) findViewById(R.id.Board);
        gameView.setLoseView((LinearLayout) findViewById(R.id.LoseView));
        gameView.setMainMenu((LinearLayout) findViewById(R.id.Main_Menu));
        gameView.setScoreView((TextView) findViewById(R.id.Score));
        
        gameView.setState(States.READY);
        
    }
    
    private OnClickListener startGame = new OnClickListener() {
		public void onClick(View v) {
			int difficulty = v.getId();
			
			switch (difficulty) {
			case R.id.easyBtn: 
				gameView.initNewGame(Constants.Difficulty.EASY);
				break;
			case R.id.medBtn: 
				gameView.initNewGame(Constants.Difficulty.MEDIUM);
				break;
			default:
				return;
			}
		}
	};
	
    private OnClickListener goToMainMenu = new OnClickListener() {
		public void onClick(View v) {			
			gameView.setState(States.READY);
		}
	};
    
}