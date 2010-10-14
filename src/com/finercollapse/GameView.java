package com.finercollapse;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * GameView: a simple puzzle game
 * 
 */
public class GameView extends TileView {

    private static final String TAG = "GameView";

    /**
     * Current mode of application: READY to run, RUNNING, or you have already
     * lost. static final ints are used instead of an enum for performance
     * reasons.
     */
    private int mMode = READY;
    public static final int READY = 1;
    public static final int RUNNING = 2;
    public static final int LOSE = 3;
    public static final int ANIMATE = 4;

    /**
     * Labels for the drawables that will be loaded into the TileView class
     */
    private static final int BLANK = 0; 
    private static final int RED_STAR = 1;
    private static final int YELLOW_STAR = 2;
    private static final int GREEN_STAR = 3;

    /**
     * mScore: used to track the number of apples captured mMoveDelay: number of
     * milliseconds between snake movements. This will decrease as apples are
     * captured.
     */
    private long score = 0;
    private long mMoveDelay = 20;
    /**
     * mLastMove: tracks the absolute time when the snake last moved, and is used
     * to determine if a move should be made based on mMoveDelay.
     */
    private long mLastMove;
    

	private ConcurrentLinkedQueue<Tile> mAnimating = new ConcurrentLinkedQueue<Tile>();
    
    
    /**
     * mStatusText: text shows to the user in some run states
     */
    private TextView mStatusText;
    

    /**
     * Everyone needs a little randomness in their life
     */
    private static final Random RNG = new Random();
    

    /**
     * Create a simple handler that we can use to cause animation to happen.  We
     * set ourselves as a target and we can use the sleep()
     * function to cause an update/invalidate to occur at a later date.
     */
    private Handler mHandler = new Handler();
    
    class Animator extends Thread {
    	public void run() {
    		boolean isDone;
    		long now;
    		
    		
    		while (true) {
    			now = System.currentTimeMillis();
    			if (!mAnimating.isEmpty()) {
	 				//for each Tile in the Queue
		        	for (Tile current : mAnimating) {
	
		        		isDone = current.animateDown(mTileSize);
	            		
	            		if (isDone) {
	            			mAnimating.remove(current); //remove current node
	            			getBelow(current).setColor(current.getColor());
	            			current.setColor(getAbove(current).getColor());
	            			current.resetOffset();
	            			//TODO send message 
	            		}
		        	}
		        	mHandler.post(mRedraw);
		        	System.out.println("animate: " + (System.currentTimeMillis() - now));
		        	
		        	now = System.currentTimeMillis();
		        	SystemClock.sleep(mMoveDelay);
		        	System.out.println("sleep: " + (System.currentTimeMillis() - now));
    			} else {
    				consolidateTiles();
    				System.out.println("consolidate: " + (System.currentTimeMillis() - now));
    			}
    		}
    	}
    }
    
    final Runnable mRedraw = new Runnable() {
    	public void run() {
    		GameView.this.invalidate();
    	}
    };
    
    
    final Runnable mPostUserClick = new Runnable() {
    	public void run() {
 	    	//push up a new row of tiles
 	    	//newRow();
 	    	
 	    	//check if any tiles are filled in top row
 	    	if (rowHasTile(0)) {	//TODO magic number
 	    		setMode(LOSE);
 	    		return;
 	    	}
 	    	//TODO check if any tiles are filled in 2nd row --> warning
 	    	if (rowHasTile(1)) {        	//TODO magic number
 	    		Log.i(TAG, "user warning: about to lose!");
 	    	}
    	}
    };
        


    /**
     * Constructs a GameView based on inflation from XML
     * 
     * @param context
     * @param attrs
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();
   }

    private void initGameView() {
        setFocusable(true);

        Resources r = this.getContext().getResources();
        
        resetTiles(4); //TODO what does this do?
        loadTile(RED_STAR, r.getDrawable(R.drawable.redstar));
        loadTile(YELLOW_STAR, r.getDrawable(R.drawable.yellowstar));
        loadTile(GREEN_STAR, r.getDrawable(R.drawable.greenstar));
        loadTile(BLANK, r.getDrawable(R.drawable.blankstar));
        
        Animator a = new Animator();
        a.start();
    }
    

    private void initNewGame() {
    	clearAllTiles();

    	for (int i = 0; i < 4; i++){  //TODO magic number
    		newRow();
    	}
    	
        score = 0;
    }


    /*
     * handles key events in the game.
     * 
     * (non-Javadoc)
     * 
     * @see android.view.View#onKeyDown(int, android.os.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (mMode == READY | mMode == LOSE) {
                /*
                 * At the beginning of the game, or the end of a previous one,
                 * we should start a new game.
                 */
                initNewGame();
                setMode(RUNNING);
                //update();
                return (true);
            }
            
            return (true);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (mMode == RUNNING) {
            	setRandomBoard();
            	return (true);
            }
        }
        
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
        	this.printAll();
        }

        return super.onKeyDown(keyCode, msg);
    }
    
    //TODO documentation
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	int action = event.getAction();
    	
    	if (MotionEvent.ACTION_DOWN == action) {
    	
	        int x = ((int)(event.getX() + mXOffset) / mTileSize) - 1;
	        int y = ((int)(event.getY() + mYOffset) / mTileSize) - 1;
	        
	        if (x < 0 || y < 0 || x >= mXTileCount || y >= mYTileCount) {
	        	Log.w(TAG, "user clicked out of bounds");
	        	return true;
	        }
	        
	        if (mMode == RUNNING) {
	        	//check that the tile clicked was not blank!
	        	if (getTile(x,y).getColor() != BLANK) {
		        	//all touching tiles that have the same color as the clicked tile will be set to BLANK
		        	breadthFirstSearch(x, y); 
		        	//consolidateTiles();
		        	//new postUserClick().execute(); 
	        	}
	        }
    	}
    	
    	return true;
	}
    
    /**
     * Sets the TextView that will be used to give information (such as "Game
     * Over" to the user.
     * 
     * @param newView
     */
    public void setTextView(TextView newView) {
        mStatusText = newView;
    }


    /**
     * Updates the current mode of the application (RUNNING or PAUSED or the like)
     * as well as sets the visibility of textview for notification
     * 
     * @param newMode
     */
    public void setMode(int newMode) {
        int oldMode = mMode;
        mMode = newMode;

        if (newMode == RUNNING && oldMode != RUNNING) {
            mStatusText.setVisibility(View.INVISIBLE);
            //update();
            return;
        }

        Resources res = getContext().getResources();
        CharSequence str = "";
        if (newMode == READY) {
            str = res.getText(R.string.mode_ready);
        }
        if (newMode == LOSE) {
            str = res.getString(R.string.mode_lose_prefix) + score
                  + res.getString(R.string.mode_lose_suffix);
            clearAllTiles();
        }
        if (newMode == ANIMATE) {
        	return;
        }

        mStatusText.setText(str);
        mStatusText.setVisibility(View.VISIBLE);
    }


    /**
     * Handles the basic update loop, checking to see if we are in the running
     * state, determining if a move should be made, updating the snake's location.
     */
    public void update() {
    	long now;
    	boolean isDone;
    	
    	if (mMode == ANIMATE) {
        	now = System.currentTimeMillis();
        	
    		if (now - mLastMove > mMoveDelay) {
    			//for each Tile in the Queue
	        	for (Tile current : mAnimating) {

	        		isDone = current.animateDown(mTileSize);
            		
            		if (isDone) {
            			mAnimating.remove(current); //remove current node
            			getBelow(current).setColor(current.getColor());
            			current.setColor(getAbove(current).getColor());
            			current.resetOffset();
            		}
	        	}
	        	
	        	// nothing left to animate. return to RUNNING mode
	        	if (mAnimating.isEmpty()) {
	        		mMode = RUNNING;
	        		//TODO set off another async queue!
	        	} else {
	                //mRedrawHandler.sleep(mMoveDelay);	        		
	        	}
    		}
    		mLastMove = now;
    	}
    	
    }

    /**
     * breadth-first-search algorithm
     * @param sourceX
     * @param sourceY
     */
    private void breadthFirstSearch(int sourceX, int sourceY) {
    	Queue<Tile> queue = new LinkedList<Tile>();
    	Tile source = getTile(sourceX, sourceY);
    	Tile[] adj = new Tile[4];
    	
    	/* initialize */
    	for (int x = 0; x < mXTileCount; x++) {
    		for (int y = 0; y < mYTileCount; y++) {
    				Tile current = getTile(x, y);
    				
    				current.setBFSStatus(Tile.BFS.UNDISCOVERED);
    				current.setDistance(-1);
    				current.setPred(null);
    		}
    	}
    	source.setBFSStatus(Tile.BFS.DISCOVERED);
    	source.setDistance(0);
    	
    	queue.add(source);
    	
    	while (!queue.isEmpty()) {
    		Tile current = queue.poll(); //return head and remove from queue
    		
    		adj[0] = getAbove(current);
    		adj[1] = getBelow(current);
    		adj[2] = getRight(current);
    		adj[3] = getLeft (current);
    		
    		//for each adjacent Tile
    		for (Tile a : adj) {
    			if (a != null && 
    				a.getBFSStatus() == Tile.BFS.UNDISCOVERED &&
    				a.getColor() == current.getColor()) {
	    				a.setBFSStatus(Tile.BFS.DISCOVERED);
	    				a.setDistance(current.getDistance() + 1); //TODO i don't think we care about distance
	    				a.setPred(current); //TODO or this
	    				queue.add(a);
    			}
    		}
    		current.setColor(BLANK);
    		current.setBFSStatus(Tile.BFS.HANDLED);
    	}
    	return;
    }
    
    
    /**
     * Draws some walls.
     * TODO: documentation
     */
    private void setRandomBoard() {
        for (int x = 0; x < mXTileCount; x++) {
        	for (int y = 0; y < mYTileCount; y++) {
        		int color = (RNG.nextInt(3)) + 1;
        		getTile(x, y).setColor(color);
        	}
        }
    }
    
    //TODO documentation    
    private void newRow() {
    	//shift existing rows up
    	for (int x = 0; x < mXTileCount; x++) {
    		for (int y = 0; y < mYTileCount - 1; y++) {
    			Tile tileBelow = getTile(x, y+1);
    			getTile(x, y).setColor(tileBelow.getColor());
    		}
    	}
    	
    	//new row on bottom
    	for (int x = 0; x < mXTileCount; x++) {
    		int color = (RNG.nextInt(3) + 1);  //TODO magic number
    		getTile(x, mYTileCount - 1).setColor(color);
    	}
    }
    
    private boolean tileIsBlank(int x, int y) {
    	return (getTile(x, y).getColor() == BLANK);
    }

    //TODO documentation
    //return true if there is a filled tile in given row
    private boolean rowHasTile(int row) {
    	for (int x = 0; x < mXTileCount; x++){
    		if (!tileIsBlank(x, row)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    private boolean emptyBelowHere(int col, int y) {
    	//no need to examine the first tile, it should be filled.
    	for (y++; y < mYTileCount; y++) {
    		if  (tileIsBlank(col, y)) {
    			return true;
    		}
    	}
    	    	
    	return false;
    }
    
    
    private boolean consolidateTilesStatic() {
    	boolean retval = false;
    	
    	// drop tiles that have a BLANK below them
    	// loop through rows from bottom to top
    	// Do not bother looking at the last row
    	for (int x = 0; x < mXTileCount; x++) {
    		for (int y = mYTileCount-2; y >= 0; y--) {
    			if (!tileIsBlank(x, y) && emptyBelowHere(x, y)) {
    				Tile current = getTile(x, y);
    				Tile below = getBelow(current);
    				below.setColor(current.getColor());
    				current.setColor(BLANK);
    				
    				retval = true;
    			}
    		}
    	}
    	
    	//TODO shift columns to the right that have a BLANK column to the right of them
    	
    	return retval;
    }
    
      
    //return true if animation needs to be done
    private boolean consolidateTiles() {
    	boolean retval = false;
    	

	    	// drop tiles one space that have a BLANK below them
	    	// loop through rows from bottom to top
	    	// Do not bother looking at the last row
	    	for (int x = 0; x < mXTileCount; x++) {
	    		for (int y = mYTileCount-2; y >= 0; y--) {
	    			if (!tileIsBlank(x, y) && emptyBelowHere(x, y)) {
	    				
	    				mAnimating.add(getTile(x,y));
	    				retval = true;
	    			}
	    		}
	    	}

    	// if at least one tile needs to be dropped
//    	if (retval) {
//    		setMode(ANIMATE); }
//    	} else {
//    		//nothing left to animate
//    		setMode(RUNNING);
//    	}
    	
    	//TODO shift columns to the right that have a BLANK column to the right of them
    	
    	return retval;
    }
    
        
}
