package com.finercollapse;

import java.util.*;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

/**
 * SnakeView: implementation of a simple game of Snake
 * 
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
    public static final int PAUSE = 0;
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
    private long frameRate = 10;
    
    /**
     * mStatusText: text shows to the user in some run states
     */
    private TextView mStatusText;
    
    /**
     * mAnimateLayer: used to animate specific game tiles
     */
    private AnimateView mAnimateLayer;

    /**
     * board: an array that represents the entire board
     */
    private ArrayList<Coordinate> board = new ArrayList<Coordinate>(); 

    /**
     * Everyone needs a little randomness in their life
     */
    private static final Random RNG = new Random();
    

    /**
     * Create a simple handler that we can use to cause animation to happen.  We
     * set ourselves as a target and we can use the sleep()
     * function to cause an update/invalidate to occur at a later date.
     */
    private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            GameView.this.update();
            GameView.this.invalidate();
        }

        public void sleep(long delayMillis) {
        	this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };


    /**
     * Constructs a SnakeView based on inflation from XML
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
    	
    }
    

    private void initNewGame() {
    	board.clear();

    	for (int i = 0; i < 2; i++){  //TODO magic number
    		newRow();
    	}
    	
    	
        frameRate = 600;
        score = 0;
    }


    /**
     * Given a ArrayList of coordinates, we need to flatten them into an array of
     * ints before we can stuff them into a map for flattening and storage.
     * 
     * @param cvec : a ArrayList of Coordinate objects
     * @return : a simple array containing the x/y values of the coordinates
     * as [x1,y1,x2,y2,x3,y3...]
     */
    private int[] coordArrayListToArray(ArrayList<Coordinate> cvec) {
        int count = cvec.size();
        int[] rawArray = new int[count * 2];
        for (int index = 0; index < count; index++) {
            Coordinate c = cvec.get(index);
            rawArray[2 * index] = c.x;
            rawArray[2 * index + 1] = c.y;
        }
        return rawArray;
    }

    /**
     * Save game state so that the user does not lose anything
     * if the game process is killed while we are in the 
     * background.
     * 
     * @return a Bundle with this view's state
     */
    public Bundle saveState() {
        Bundle map = new Bundle();

        map.putLong("mMoveDelay", Long.valueOf(frameRate));
        map.putLong("mScore", Long.valueOf(score));

        return map;
    }

    /**
     * Given a flattened array of ordinate pairs, we reconstitute them into a
     * ArrayList of Coordinate objects
     * 
     * @param rawArray : [x1,y1,x2,y2,...]
     * @return a ArrayList of Coordinates
     */
    private ArrayList<Coordinate> coordArrayToArrayList(int[] rawArray) {
        ArrayList<Coordinate> coordArrayList = new ArrayList<Coordinate>();

        int coordCount = rawArray.length;
        for (int index = 0; index < coordCount; index += 2) {
            Coordinate c = new Coordinate(rawArray[index], rawArray[index + 1]);
            coordArrayList.add(c);
        }
        return coordArrayList;
    }

    /**
     * Restore game state if our process is being relaunched
     * 
     * @param icicle a Bundle containing the game state
     */
    public void restoreState(Bundle icicle) {
        setMode(PAUSE);

        frameRate = icicle.getLong("mMoveDelay");
        score = icicle.getLong("mScore");
    }

    /*
     * handles key events in the game. Update the direction our snake is traveling
     * based on the DPAD. Ignore events that would cause the snake to immediately
     * turn back on itself.
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
                update();
                return (true);
            }

            if (mMode == PAUSE) {
                /*
                 * If the game is merely paused, we should just continue where
                 * we left off.
                 */
                setMode(RUNNING);
                update();
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
        
//        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            if (mMode == RUNNING) {
//            	
//            	return (true);
//            }
//        }

        return super.onKeyDown(keyCode, msg);
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

    public void setAnimateView(AnimateView newView) {
    	mAnimateLayer = newView;
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

        if (newMode == RUNNING & oldMode != RUNNING) {
            mStatusText.setVisibility(View.INVISIBLE);
            mAnimateLayer.setVisibility(View.VISIBLE);
            update();
            return;
        }

        Resources res = getContext().getResources();
        CharSequence str = "";
        if (newMode == PAUSE) {
            str = res.getText(R.string.mode_pause);
        }
        if (newMode == READY) {
            str = res.getText(R.string.mode_ready);
        }
        if (newMode == LOSE) {
            str = res.getString(R.string.mode_lose_prefix) + score
                  + res.getString(R.string.mode_lose_suffix);
            clearTiles();
        }
        if (newMode == ANIMATE) {
        	return;
        }

        mStatusText.setText(str);
        mStatusText.setVisibility(View.VISIBLE);
        mAnimateLayer.setVisibility(View.INVISIBLE);
    }


    /**
     * Handles the basic update loop, checking to see if we are in the running
     * state, determining if a move should be made, updating the snake's location.
     */
    public void update() {
        if (mMode == RUNNING) {
            //long now = System.currentTimeMillis();
          
            mRedrawHandler.sleep(frameRate);
        }

    }

    //breadth-first-search algorithm
    private void breadthFirstSearch(int sourceX, int sourceY) {
    	Queue<Tile> queue = new LinkedList<Tile>();
    	Tile source = getTile(sourceX, sourceY);
    	Tile[] adj = new Tile[4];
    	
    	/* initialize */
    	for (int x = 0; x < mXTileCount; x++) {
    		for (int y = 0; y < mYTileCount; y++) {
    				Tile current = getTile(x, y);
    				
    				current.setStatus(Tile.BFS.UNDISCOVERED);
    				current.setDistance(-1);
    				current.setPred(null);
    		}
    	}
    	source.setStatus(Tile.BFS.DISCOVERED);
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
    				a.getStatus() == Tile.BFS.UNDISCOVERED &&
    				a.getColor() == current.getColor()) {
	    				a.setStatus(Tile.BFS.DISCOVERED);
	    				a.setDistance(current.getDistance() + 1);
	    				a.setPred(current);
	    				queue.add(a);
    			}
    		}
    		current.setColor(BLANK);
    		current.setStatus(Tile.BFS.HANDLED);
    	}
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
    				Tile currentTile = getTile(x, y);
    				Tile tileBelow = getTile(x, y+1);
    				
    				tileBelow.setFutureColor(currentTile.getColor());
    				currentTile.setColor(BLANK);
    				tileBelow.updateColor();
    				
    				retval = true;
    			}
    		}
    	}
    	   	
    	
    	//TODO shift columns to the right that have a BLANK column to the right of them
    	
    	return retval;
    }
    
    
    private boolean consolidateTiles() {
    	Queue<Tile> queue = new LinkedList<Tile>();
    	boolean retval = false;
    	
    	// drop tiles that have a BLANK below them
    	// loop through rows from bottom to top
    	// Do not bother looking at the last row
    	for (int x = 0; x < mXTileCount; x++) {
    		for (int y = mYTileCount-2; y >= 0; y--) {
    			if (!tileIsBlank(x, y) && emptyBelowHere(x, y)) {
    				Tile currentTile = getTile(x, y);
    				Tile tileBelow = getTile(x, y+1);
    				
    				queue.add(tileBelow);
    				mAnimateLayer.getTile(x, y).setColor(currentTile.getColor()); //copy color of the currentTile to the Animation layer
    				tileBelow.setFutureColor(currentTile.getColor());
    				currentTile.setColor(BLANK);
    				retval = true;
    			}
    		}
    	}
    	
    	if (retval) {
	    	mAnimateLayer.animate(queue);
    	}
    	
    	
    	//TODO shift columns to the right that have a BLANK column to the right of them
    	
    	return retval;
    }
    
   
    
    //TODO documentation
    @Override
	public boolean onTouchEvent(MotionEvent event) {
        int x = ((int)(event.getX() + mXOffset) / mTileSize) - 1;
        int y = ((int)(event.getY() + mYOffset) / mTileSize) - 1;
        
        if (x < 0 || y < 0 || x >= mXTileCount || y >= mYTileCount) {
        	/* user clicked out of bounds */
        	Log.w(TAG, "user clicked out of bounds");
        	return true;
        }
        
        if (mMode == RUNNING) {        	
        	//TODO check that the tile clicked was not blank!
        	
        	//all touching tiles that have the same color as the clicked tile will be set to BLANK
        	breadthFirstSearch(x, y);
        	
        	//TODO consolidate tiles
        	while(consolidateTiles());    
        		
        	//check if any tiles are filled in top row
        	if (rowHasTile(0)) {        	//TODO magic number
        		setMode(LOSE);
        		return true;
        	}
        	//TODO check if any tiles are filled in 2nd row --> warning
        	if (rowHasTile(1)) {        	//TODO magic number
        		Log.i(TAG, "user warning: about to lose!");
        	}
        	
        	//push up a new row of tiles
        	newRow();
	    }
        
		return super.onTouchEvent(event);
	}
    
    
    
     /**
     * Simple class containing two integer values and a comparison function.
     * There's probably something I should use instead, but this was quick and
     * easy to build.
     * 
     */
    private class Coordinate {
        public int x;
        public int y;


        public Coordinate(int newX, int newY) {
            x = newX;
            y = newY;
        }

        public boolean equals(Coordinate other) {
            if (x == other.x && y == other.y) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Coordinate: [" + x + "," + y + "]";
        }
    }


    
}
