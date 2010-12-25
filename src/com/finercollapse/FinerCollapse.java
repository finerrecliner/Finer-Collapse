package com.finercollapse;

import com.finercollapse.GameView.States;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
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
        final Button easyButton = (Button) findViewById(R.id.Easy);
        final Button medButton = (Button) findViewById(R.id.Medium);
        
        easyButton.setOnClickListener(buttonListener);
        medButton.setOnClickListener(buttonListener);
        
        gameView = (GameView) findViewById(R.id.Board);
        //gameView.setTextView((TextView) findViewById(R.id.Text));
        gameView.setMainMenu((RelativeLayout) findViewById(R.id.Main_Menu));
        
        gameView.setState(GameView.States.READY);
        
    }
    
    private OnClickListener buttonListener = new OnClickListener() {
		
		public void onClick(View v) {
			int difficulty = v.getId();
			
			switch (difficulty) {
			case R.id.Easy: 
				gameView.initNewGame(Constants.Difficulty.EASY);
				break;
			case R.id.Medium: 
				gameView.initNewGame(Constants.Difficulty.MEDIUM);
				break;
			default:
				return;
			}
			

		}

	};
    
}