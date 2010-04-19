package com.finercollapse;

import java.util.LinkedList;
import java.util.Queue;

import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

public class AnimateView extends TileView {
    private static final String TAG = "AnimateView";
	
	
    /**
     * Labels for the drawables that will be loaded into the TileView class
     */
    private static final int BLANK = 0;
    private static final int RED_STAR = 1;
    private static final int YELLOW_STAR = 2;
    private static final int GREEN_STAR = 3;
    
    /* animation */
    private Animation mSlideDown;
    
	Queue<Tile> mQueue = new LinkedList<Tile>();
		
 
    public AnimateView(Context context, AttributeSet attrs) {
		super(context, attrs);
        initAnimateView();
	}
    
    
	private void initAnimateView() {
	    setFocusable(true);
	
	    Resources r = this.getContext().getResources();
	    
	    mSlideDown = AnimationUtils.loadAnimation(this.getContext(), R.anim.slide_down);
	    mSlideDown.setAnimationListener(new AnimListener());
	    
	    resetTiles(4); //TODO what does this do?
	    loadTile(RED_STAR, r.getDrawable(R.drawable.redstar));
	    loadTile(YELLOW_STAR, r.getDrawable(R.drawable.yellowstar));
	    loadTile(GREEN_STAR, r.getDrawable(R.drawable.greenstar));
		
	}
	
	public void animate(Queue<Tile> queue) {		
		mQueue = queue;
		this.startAnimation(mSlideDown);
	}
   
	
    private final class AnimListener implements Animation.AnimationListener {

        private AnimListener() {

        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
        	for (Tile t : mQueue) {
        		t.updateColor();
        	}
        	        	
    		clearTiles();
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }



}