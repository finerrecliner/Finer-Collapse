package com.finercollapse;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

public class AnimateView extends TileView implements AnimationListener {
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
	
 
    public AnimateView(Context context, AttributeSet attrs) {
		super(context, attrs);
        initAnimateView();
	}


	private void initAnimateView() {
	    setFocusable(true);
	
	    Resources r = this.getContext().getResources();
	    
	    mSlideDown = AnimationUtils.loadAnimation(this.getContext(), R.anim.slide_down);
	    
	    resetTiles(4); //TODO what does this do?
	    loadTile(RED_STAR, r.getDrawable(R.drawable.redstar));
	    loadTile(YELLOW_STAR, r.getDrawable(R.drawable.yellowstar));
	    loadTile(GREEN_STAR, r.getDrawable(R.drawable.greenstar));
	    
	    getTile(0, 0).setColor(GREEN_STAR);
		
	}
	
	public void animate() {
		this.startAnimation(mSlideDown);
	}
    
    
	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
		
	}


}