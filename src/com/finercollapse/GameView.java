package com.finercollapse;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.finercollapse.Tile.AnimDirection;
import com.FinerCollapse.Constants.*;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
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

    /************* Private Attributes ******************/
	
    /**
     * debugging identifier
     */
    private static final String TAG = "GameView";

    /* magic numbers */
    private static final int TOP_ROW = 0;
    private static final int SECOND_ROW = 1;
    
    /**
     * Used to track user's score per game round 
     */
    private long mScore = 0;

    /**
     * Number of milliseconds between screen redraws
     */
    private long mDelay = 40;    

    /**
     * Current state of the board
     */
    private int mState = READY;
    
	/**
	 * Used to know which Tiles need to update their position during this screen redraw
	 */
	private ConcurrentLinkedQueue<Tile> mAnimating = new ConcurrentLinkedQueue<Tile>(); //TODO can we remove, and just ask EVERY Tile, if it wants to animate?
    
    /**
     * Test shown to the user in some run states
     */
    private TextView mStatusText;

    
    /**
     * Create a simple handler to be able to schedule method calls in the near future.
     */
    private Handler mHandler = new Handler();
    
    /**
     * Set of instructions to do when it is time to push up a new row of Tiles
     */
    private final Runnable mNewRow = new Runnable() {
    	public void run() {
    		setState(NEW_ROW);
    		newRow();
    	}
    };
    
    /**
     * Set of instructions to do when it is time to redraw the screen
     */
    private final Runnable mRedraw = new Runnable() {
    	public void run() {
    		GameView.this.invalidate();
    	}
    };
    
    /**
     * Set of instructions to do after the board has finished all animations
     */
    private final Runnable mPostUserClick = new Runnable() {
    	public void run() {
 	    	
 	    	//check if any tiles are filled in top row
 	    	if (rowHasTile(TOP_ROW)) {
 	    		setState(LOSE);
 	    		return;
 	    	}
 	    	
 	    	if (rowHasTile(SECOND_ROW)) {        	//TODO magic number
 	    		Log.i(TAG, "user warning: about to lose!");
 	    		//TODO warning by vibrate or screen alert?
 	    	}
 	    	
 	    	setState(RUNNING);
    	}
    };
        
    /*************** End Private Attributes *******************/
    
    
    
    
    /********************* Structures ***************************/
    
    /**
     * A child thread in charge of redrawing the screen and managing animations
     */
    class Animator extends Thread {
    	
    	/**
    	 * Figure out what the next instruction we should do next 
    	 * based on the Game's state 
    	 */
    	private void doNext() {
    		switch (mState) {
    		case DROP:
    			drop();
    			break;
    		case NEW_ROW:
    			mHandler.post(mPostUserClick);
    			break;
    		default:
    			Log.w(TAG, "Invalid Mode of GameView");
    		}
    	} 	
    	
    	/**
    	 * Look for more Tiles to drop. 
    	 * If there are any, then stay in DROP state, to animate them.
    	 * Otherwise proceed to the next State
    	 */
    	public void drop() {
    		boolean more = findTilesToAnimate();
    		
    		//if there isn't anything else to animate, do the post-animation stuff
    		if (!more) {
    			mHandler.post(mNewRow);
    		}
    	}
    	
    	@Override
    	public void run() {
    		boolean isDone;
    		
    		/* infinite loop */
    		while (true) {
    			if (!mAnimating.isEmpty()) {
	 				//for each Tile in the Queue
		        	for (Tile current : mAnimating) {
	
		        		// try to animate current Tile
		        		isDone = current.animate(mTileSize);
	            		
	            		if (isDone) {
	            			mAnimating.remove(current);
	            		}
		        	}
		        	
		        	//ran out of stuff to animate, do next step 
		        	if (mAnimating.isEmpty()) {
		        		doNext();
		        	}
		        	
    			}
	        	mHandler.post(mRedraw);      //redraw screen    
	        	SystemClock.sleep(mDelay);   //sleep
    		}
    	}
    }
    
    /**
     * Current mode of application: READY to run, RUNNING, or you have already
     * lost. static final ints are used instead of an enum for performance
     * reasons.
     */
    public static final int READY = 1;
    public static final int RUNNING = 2;
    public static final int LOSE = 3;
    public static final int ANIMATE = 4;
    public static final int DROP = 5;
    public static final int NEW_ROW = 6;
    
    /****************** End Structures ***********************/
    
    /****************** Public Methods ***********************/
    
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
    
    /**
     * Updates the current state of the application (RUNNING or PAUSED or the like)
     * as well as sets the visibility of textview for notification
     * 
     * @param newState
     */
    public void setState(int newState) {
        int oldMode = mState;
        mState = newState;

        if (newState == RUNNING && oldMode != RUNNING) {
            mStatusText.setVisibility(View.INVISIBLE);
            return;
        }

        Resources res = getContext().getResources();
        CharSequence str = "";
        if (newState == READY) {
            str = res.getText(R.string.mode_ready);
        }
        if (newState == LOSE) {
            str = res.getString(R.string.mode_lose_prefix) + " " + mScore
                  + res.getString(R.string.mode_lose_suffix);
            clearAllTiles();
        }
        if (newState == DROP || newState == NEW_ROW) {
        	return;
        }

        mStatusText.setText(str);
        mStatusText.setVisibility(View.VISIBLE);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.view.View#onKeyDown(int, android.os.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (mState == READY | mState == LOSE) {
                /*
                 * At the beginning of the game, or the end of a previous one,
                 * we should prep the board.
                 */
                initNewGame();
                setState(RUNNING);
            }
            
            return true;
        }
        
        // this is for debugging. TODO remove
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
        	this.printBoard();
        }

        return super.onKeyDown(keyCode, msg);
    }
    
    /* (non-Javadoc)
     * @see com.finercollapse.TileView#onTouchEvent(android.view.MotionEvent)
     */
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	int action = event.getAction();
    	boolean willAnim; //we found something to animate
    	Tile selected;
    	
    	if (MotionEvent.ACTION_DOWN == action) {
    	
	        int x = ((int)(event.getX() + mXOffset) / mTileSize) - 1;
	        int y = ((int)(event.getY() + mYOffset) / mTileSize) - 1;
	        
	        if (x < 0 || y < 0 || x >= mXTileCount || y >= mYTileCount) {
	        	Log.w(TAG, "user clicked out of bounds");
	        	return true;
	        }
	        
	        if (mState == RUNNING) {
	        	selected = findTile(x,y);
	        	//check that the tile clicked was not blank
	        	if (selected.getColor() != Color.BLANK) {
	        		setState(DROP);
		        	//all touching tiles that have the same color as the clicked tile will be set to BLANK
		        	setMatchingNeighbors(selected, Color.BLANK);
		        	willAnim = findTilesToAnimate();
		        	
		        	// if nothing to animate from this click, don't forget to do this stuff anyways!
		        	if (!willAnim) {
		        		mHandler.post(mPostUserClick);
		        	}
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

    /***************** End Public Methods *********************/
    
    /****************** Private Methods ***********************/
    
    
    /**
     * Initialize resources
     */
    private void initGameView() {
        setFocusable(true);

        Resources r = this.getContext().getResources();
        
        resetTiles(Color.getSize());
        loadTile(Color.RED, r.getDrawable(R.drawable.redstar));
        loadTile(Color.YELLOW, r.getDrawable(R.drawable.yellowstar));
        loadTile(Color.GREEN, r.getDrawable(R.drawable.greenstar));
        loadTile(Color.BLANK, r.getDrawable(R.drawable.blankstar));
        
        // spawn a child thread to handle animations
        Animator a = new Animator();
        a.start();
    }
    

    /**
     * Prepare the board for a new round of the game
     */
    private void initNewGame() {
    	clearAllTiles();

    	//add prefilled lines
    	for (int i = 0; i < 4; i++){  //TODO magic number
    		newRowStatic(); //TODO i think I can move the method inline here...
    	}
    	
        mScore = 0;
    }

    /**
     * Find all touching tiles that are the same color using
     * a breadth-first-search algorithm, and
     * change their color to BLANK
     * 
     * @param sourceX
     * @param sourceY
     */
    private void setMatchingNeighbors(Tile source, Color color) {
    	Queue<Tile> queue = new LinkedList<Tile>();
    	Tile[] adj; 
    	
    	/* initialize for breadth first search */
    	for (int x = 0; x < mXTileCount; x++) {
    		for (int y = 0; y < mYTileCount; y++) {
    				Tile current = findTile(x, y);
    				
    				current.setBFSStatus(Tile.BFS.UNDISCOVERED);
    		}
    	}
    	source.setBFSStatus(Tile.BFS.DISCOVERED);
    	
    	queue.add(source);
    	
    	while (!queue.isEmpty()) {
    		Tile current = queue.poll(); //return head and remove from queue
    		adj = current.getAdj();
    		
    		//discover more matching Tiles
    		for (Tile a : adj) {
    			if (a != null && 
    				a.getBFSStatus() == Tile.BFS.UNDISCOVERED &&
    				a.getColor() == current.getColor()) {
	    				a.setBFSStatus(Tile.BFS.DISCOVERED);
	    				queue.add(a);
    			}
    		}
    		
    		//what to do with each Tile Discovered
    		current.setColor(color);
    		current.setBFSStatus(Tile.BFS.HANDLED);
    	}
    	return;
    }
        
    /**
     * Shift up the existing rows of Tiles on the Board,
     * and create a new row of random Tiles in the bottom row.
     * This is all done WITHOUT animation.
     */
    private void newRowStatic() {
    	//shift existing rows up
    	for (int x = 0; x < mXTileCount; x++) {
    		for (int y = 0; y < mYTileCount - 1; y++) {
    			Tile tileBelow = findTile(x, y+1);
    			findTile(x, y).setColor(tileBelow.getColor());
    		}
    	}
    	
    	//new row on bottom
    	for (int x = 0; x < mXTileCount; x++) {
    		Color color = Color.getRandom();
    		findTile(x, mYTileCount - 1).setColor(color);
    	}
    }
    
    /**
     * Shift up the existing rows of Tiles on the Board,
     * and create a new row of random Tiles in the bottom row.
     * This is process is animated
     */
    private void newRow() {
    	//shift existing rows up
    	for (int x = 0; x < mXTileCount; x++) {
    		for (int y = 0; y < mYTileCount - 1; y++) {
    			Tile current = findTile(x,y);
    			Tile below = getBelow(current);
    			current.setColor(below.getColor());
    		}
    	}
    	
    	//new row on bottom
    	for (int x = 0; x < mXTileCount; x++) {
    		Color color = Color.getRandom();
    		findTile(x, mYTileCount - 1).setColor(color);
    	}
    	
    	for (int x = 0; x < mXTileCount; x++) {
    		for (int y = 0; y < mYTileCount; y++) {
    			Tile current = findTile(x,y);
    			
    			if (current.getColor() != Color.BLANK) {
	    			current.modYOffset(mTileSize);
	    			current.setAnimDirection(AnimDirection.UP);
	    			mAnimating.add(current);
    			}
    		}
    	}

    	
    }
    
    /**
     * Look for tiles that need to be dropped. If any are 
     * found, they are queued up to be animated
     * 
     * @return true if an animation needs to be done
     *         false otherwise
     */
    private boolean findTilesToAnimate() {
    	boolean retval = false;
	
    	// loop through rows from bottom to top
    	// Do not bother looking at the bottom row, as nothing can be "below" it
    	for (int x = 0; x < mXTileCount; x++) {
    		for (int y = mYTileCount-2; y >= 0; y--) {
    			if (!tileIsBlank(x, y) && emptyBelowHere(x, y)) {
    				mAnimating.add(findTile(x,y));
    				findTile(x,y).setAnimDirection(AnimDirection.DOWN);
    				retval = true;
    			}
    		}
    	}
    	
    	return retval;
    }
    
    /***************** End Private Methods *********************/
    
}
