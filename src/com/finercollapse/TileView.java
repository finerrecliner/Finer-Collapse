package com.finercollapse;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.FinerCollapse.Constants.*;


/**
 * TileView: a View-variant designed for handling arrays of drawables.
 * 
 */
public class TileView extends View {
		
    /***************** Private Attributes *********************/
	
	/**
	 * debugging identifier
	 */
	private static final String TAG = "TileView";
	
    /**
     * A hash that maps integer handles specified by the subclasser to the
     * drawable that will be used for that reference
     */
    private Bitmap[] mTileArray; 

    /**
     * Used to draw Bitmaps
     */
    private final Paint mPaint = new Paint();
    
    /*************** End Private Attributes *******************/
	
    /**************** Protected Attributes ********************/
    
    /**
     * Size of the Drawable. Width and Height will be equal.
     * Width/Height are measured in pixels, and Drawables will be 
     * scaled to fit to these dimensions.
     */
    protected static int mTileSize;

    /**
     * Number of Tiles to Draw horizontally & vertically in the View
     */
    protected int mXTileCount;
    protected int mYTileCount;

    /**
     * Offset for entire board
     */
    protected int mXOffset;
    protected int mYOffset;

    /**
     * A two-dimensional array of Tiles that represents the
     * game board of which tiles should be drawn at those locations
     */
    protected Tile[][] mTileGrid;
    
    /*************** End Protected Attributes *****************/

    

    
    
    
    /******************** Public Methods ***********************/
    
    /**
     * Constructor
     * 
     * @param context
     * @param attrs
     */
    public TileView(Context context, AttributeSet attrs) {
    	super (context, attrs);
    	    	
    	TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);
        mTileSize = a.getInteger(R.styleable.TileView_tileSize, 36); //TODO doesnt work
        a.recycle();
    }
    
    /**
     * Function to set the specified Drawable as the tile for a particular
     * integer key.
     * 
     * @param key integer to associate with a Drawable
     * @param img Drawable to associate with key value
     */
    public void loadTile(Color c, Drawable img) {
        Bitmap bitmap = Bitmap.createBitmap(mTileSize, mTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        img.setBounds(0, 0, mTileSize, mTileSize);
        img.draw(canvas);
        
        mTileArray[c.ordinal()] = bitmap;
    }
    


    /**
     * Debug method to print a string representation of the current
     * game board to the console
     */
    public void printBoard() {
    	String buffer = ""; 
    	
    	for (int y = 0; y < mYTileCount; y++) {
    		for (int x = 0; x < mXTileCount; x++) {
            	buffer += mTileGrid[x][y].getColor().getChar() + " ";
            }
            System.out.println(buffer + "\n");
            buffer = "";	//clear buffer for next line
        }
    	System.out.println("---------------\n");
    }
    

    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x = 0; x < mXTileCount; x += 1) {
            for (int y = 0; y < mYTileCount; y += 1) {
                if (mTileGrid[x][y].getColor() != Color.BLANK) { 
                    canvas.drawBitmap(mTileArray[mTileGrid[x][y].getColor().ordinal()], 
                    		mTileGrid[x][y].getXOffset() + mXOffset + (x * mTileSize),
                    		mTileGrid[x][y].getYOffset() + mYOffset + (y * mTileSize),
                    		mPaint);
                }
            }
        }
    }   
    
    /* (non-Javadoc)
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
	public boolean onTouchEvent(MotionEvent event) {        
		return super.onTouchEvent(event);
	}
    
    @Override
    public String toString() {
    	String buffer = ""; 
    	
    	for (int y = 0; y < mYTileCount; y++) {
    		for (int x = 0; x < mXTileCount; x++) {
            	buffer += mTileGrid[x][y].getColor().getChar() + " ";
            }
            buffer += "\n";
        }
    	buffer += "---------------\n"; //TODO magic number of -'s
    	return buffer;
    }
    
    
    /******************* End Public Methods *********************/
    
    
    /******************* Protected Methods **********************/
    
    /* (non-Javadoc)
     * @see android.view.View#onSizeChanged(int, int, int, int)
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mXTileCount = (int) Math.floor(w / mTileSize);
        mYTileCount = (int) Math.floor(h / mTileSize);

        mXOffset = ((w - (mTileSize * mXTileCount)) / 2);	//horizontally centered
        mYOffset = ((h - (mTileSize * mYTileCount)));		//vertically aligned against the bottom of the view

        mTileGrid = new Tile[mXTileCount][mYTileCount];
        
        Tile t;
        
        for (int x = 0; x < mXTileCount; x++) {
        	for (int y = 0; y < mYTileCount; y++) {
        		mTileGrid[x][y] = new Tile(x, y);
        	}
        }
        
        for (int x = 0; x < mXTileCount; x++) {
        	for (int y = 0; y < mYTileCount; y++) {
        		t = findTile(x,y);
        		t.setAdjacents(getNearbyTile(t,  0, -1),  //up
        					   getNearbyTile(t,  0,  1),  //down
        					   getNearbyTile(t, -1,  0),  //left
        					   getNearbyTile(t,  1,  0)); //right
        	}
        }
    }
    
    /**
     * Resets the internal array of Bitmaps used for drawing tiles
     * 
     * @param tilecount max number of tiles to be inserted
     */
    protected void resetTiles(int tilecount) {
    	mTileArray = new Bitmap[tilecount];
    }
    
    /**
     * Find a Tile on the board with given coordinates
     * 
     * @param x X position
     * @param y Y position
     * @return Tile found at specified location
     */
    protected Tile findTile(int x, int y) {
    	try {
    		return mTileGrid[x][y];
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		Log.w(TAG + ":findTile(" + x + "," + y + ")", e.toString());
    		return null;
    	}
    }
    
    
    /**
     * Checks if a Tile's color is BLANK
     * @param x
     * @param y
     * @return true if BLANK. false otherwise.
     */
    protected boolean tileIsBlank(int x, int y) {
    	return (findTile(x, y).getColor() == Color.BLANK);
    }
        
    /**
     * Check if a row has at least one non-BLANK Tile in it
     * @param row
     * @return true if there is a filled tile in given row. <br>
     *         false if the row has only BLANKs 
     */
    protected boolean rowHasTile(int row) {
    	for (int x = 0; x < mXTileCount; x++){
    		if (!tileIsBlank(x, row)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * Checks if there is at least one BLANK tile somewhere below 
     * the provided Tile coordinates
     * 
     * @param col
     * @param y
     * @return true if there is a BLANK below the provided Tile coordinates <br>
     *         false if a BLANK was not found 
     */
    protected boolean emptyBelowHere(int column, int y) {
    	//no need to examine the first tile, it should be filled.
    	for (y += 1; y < mYTileCount; y++) {
    		if  (tileIsBlank(column, y)) {
    			return true;
    		}
    	}
    	    	
    	return false;
    }
    

    /**
     * Sets all Tiles on the board to BLANK
     */
    protected void clearAllTiles() {
        for (int x = 0; x < mXTileCount; x++) {
            for (int y = 0; y < mYTileCount; y++) {
            	mTileGrid[x][y].setColor(Color.BLANK);
            }
        }
    }
    
    /***************** End Protected Methods ********************/
    
    
    /********************* Private Methods **********************/
    /**
     * Find a Tile that is some distance away from a Tile you 
     * already know about
     * 
     * @param source Starting Tile
     * @param xDistance number of horizontal Tiles away from Source. 
     *        Enter a positive number to move right; negative to move left
     * @param yDistance number of veritical Tiles away from Source.
     *        Enter a positive number to move down; negative to move up
     * @return Tile you landed on, or null if you went out of bounds
     */
    private Tile getNearbyTile(Tile source, int xDistance, int yDistance) {
    	try {
    		return findTile(source.getX() + xDistance, source.getY() + yDistance);
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		return null;
    	}
    }
    
    /****************** End Private Methods ********************/
    

}
