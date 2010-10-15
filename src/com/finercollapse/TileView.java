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


/**
 * TileView: a View-variant designed for handling arrays of drawables.
 * 
 */
public class TileView extends View {
		
    /******************* Attributes **********************/
	
	private static final String TAG = "TileView";
	
    /**
     * Size of the Drawable. Width and Height will be equal.
     * Width/Height are measured in pixels, and Drawables will be 
     * scaled to fit to these dimensions.
     */
    protected static int mTileSize;

    /**
     * Number of Tiles to Draw horizontally & vertically in the View
     */
    protected static int mXTileCount;
    protected static int mYTileCount;

    /**
     * Offset for entire board
     */
    protected static int mXOffset;
    protected static int mYOffset;

    
    /**
     * A hash that maps integer handles specified by the subclasser to the
     * drawable that will be used for that reference
     */
    private Bitmap[] mTileArray; 

    /**
     * A two-dimensional array of Tiles that represents the
     * game board of which tiles should be drawn at those locations
     */
    protected Tile[][] mTileGrid;

    /**
     * Used to draw Bitmaps
     */
    private final Paint mPaint = new Paint();
    
    /******************* End Attributes **********************/
    
    
    /********************* Methods ***************************/
    
    
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
     * Resets the internal array of Bitmaps used for drawing tiles
     * 
     * @param tilecount max number of tiles to be inserted
     */
    public void resetTiles(int tilecount) {
    	mTileArray = new Bitmap[tilecount];
    }
    
    /**
     * Find a Tile on the board with given coordinates
     * 
     * @param x X position
     * @param y Y position
     * @return Tile found at specified location
     */
    public Tile findTile(int x, int y) {
    	try {
    		return mTileGrid[x][y];
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		Log.w(TAG + ":findTile(" + x + "," + y + ")", e.toString());
    		return null;
    	}
    }
    
    /* (non-Javadoc)
     * @see android.view.View#onSizeChanged(int, int, int, int)
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mXTileCount = (int) Math.floor(w / mTileSize);
        mYTileCount = (int) Math.floor(h / mTileSize);

        mXOffset = ((w - (mTileSize * mXTileCount)) / 2);
        mYOffset = ((h - (mTileSize * mYTileCount)) / 2);

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
        		t.setAdjacents(getAbove(t), getBelow(t), getLeft(t), getRight(t));
        	}
        }
    }

    /**
     * Function to set the specified Drawable as the tile for a particular
     * integer key.
     * 
     * @param key integer to associate with a Drawable
     * @param img Drawable to associate with key value
     */
    public void loadTile(int key, Drawable img) {
        Bitmap bitmap = Bitmap.createBitmap(mTileSize, mTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        img.setBounds(0, 0, mTileSize, mTileSize);
        img.draw(canvas);
        
        mTileArray[key] = bitmap;
    }


    /**
     * Sets all Tiles on the board to BLANK
     */
    public void clearAllTiles() {
        for (int x = 0; x < mXTileCount; x++) {
            for (int y = 0; y < mYTileCount; y++) {
            	mTileGrid[x][y].setColor(0);
            }
        }
    }
    
    /* 0,0 is at upper left */
    public Tile getAbove(Tile t) {
    	try {
    		return findTile(t.getX(), t.getY() - 1);
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		return null;
    	}
    }
    
    public Tile getBelow(Tile t, int i) {
    	try {
    		return findTile(t.getX(), t.getY() + i);
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		return null;
    	}
    }
    
    public Tile getBelow(Tile t) {
    	return getBelow(t,1);
    }
    
    public Tile getRight(Tile t) {
    	try {
    		return findTile(t.getX() + 1, t.getY());
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		return null;
    	}
    }

    public Tile getLeft(Tile t) {
    	try {
    		return findTile(t.getX() - 1, t.getY());
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		return null;
    	}
    }

    /**
     * Debug method to print a string representation of the current
     * game board to the console
     */
    public void printBoard() {
    	String buffer = ""; 
    	
    	for (int y = 0; y < mYTileCount; y++) {
    		for (int x = 0; x < mXTileCount; x++) {
            	buffer += mTileGrid[x][y].getColorChar() + " ";
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
                if (mTileGrid[x][y].getColor() > 0) { 
                    canvas.drawBitmap(mTileArray[mTileGrid[x][y].getColor()], 
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
    
    /****************** End Methods ***********************/
}
