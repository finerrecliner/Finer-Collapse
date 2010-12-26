package com.finercollapse;

import com.finercollapse.Constants.Difficulty;
import com.finercollapse.GameView.States;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FinerCollapse extends Activity {

	
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        final Button easyButton = (Button) findViewById(R.id.easyBtn);
        final Button medButton = (Button) findViewById(R.id.medBtn);
        
        easyButton.setOnClickListener(startGame);
        medButton.setOnClickListener(startGame);
        
        //OLD

//        final Button loseButton = (Button) findViewById(R.id.LoseBtn);
//        
//        loseButton.setOnClickListener(goToMainMenu);
//        
//        gameView = (GameView) findViewById(R.id.Board);
//        gameView.setLoseView((LinearLayout) findViewById(R.id.LoseView));
//        gameView.setMainMenu((LinearLayout) findViewById(R.id.Main_Menu));
//        gameView.setScoreView((TextView) findViewById(R.id.Score));
//        
//        gameView.setState(States.READY);
        
    }
    
    private OnClickListener startGame = new OnClickListener() {
		public void onClick(View v) {
			int btn = v.getId();
			Intent intent = new Intent();
			
			intent.setClass(FinerCollapse.this, com.finercollapse.PlayGame.class);
			intent.putExtra("btn_pressed", btn);
			startActivity(intent);
		}
	};   
    
	
//    private OnClickListener goToMainMenu = new OnClickListener() {
//		public void onClick(View v) {			
//			gameView.setState(States.READY);
//		}
//	};
    
}