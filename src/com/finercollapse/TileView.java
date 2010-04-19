package com.finercollapse;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * TileView: a View-variant designed for handling arrays of "icons" or other
 * drawables.
 * 
 */
public class TileView extends View {
		
    /**
     * Parameters controlling the size of the tiles and their range within view.
     * Width/Height are in pixels, and Drawables will be scaled to fit to these
     * dimensions. X/Y Tile Counts are the number of tiles that will be drawn.
     */

    protected static int mTileSize;

    protected static int mXTileCount;
    protected static int mYTileCount;

    protected static int mXOffset;
    protected static int mYOffset;

    
    /**
     * A hash that maps integer handles specified by the subclasser to the
     * drawable that will be used for that reference
     */
    private Bitmap[] mTileArray; 

    /**
     * A two-dimensional array of integers in which the number represents the
     * index of the tile that should be drawn at that locations
     */
    protected Tile[][] mTileGrid;

    private final Paint mPaint = new Paint();
    
    /* constructor */
    public TileView(Context context, AttributeSet attrs) {
    	super (context, attrs);
    	    	
    	TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);
        mTileSize = a.getInteger(R.styleable.TileView_tileSize, 36); //TODO doesnt work
        a.recycle();
    }
 
    
    
    /**
     * Rests the internal array of Bitmaps used for drawing tiles, and
     * sets the maximum index of tiles to be inserted
     * 
     * @param tilecount
     */
    
    public void resetTiles(int tilecount) {
    	mTileArray = new Bitmap[tilecount];
    }
    
    public Tile getTile(int x, int y) {
    	return mTileGrid[x][y];
    }
    
    public Tile getTile(Tile t) {
    	return mTileGrid[t.getX()][t.getY()];
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mXTileCount = (int) Math.floor(w / mTileSize);
        mYTileCount = (int) Math.floor(h / mTileSize);

        mXOffset = ((w - (mTileSize * mXTileCount)) / 2);
        mYOffset = ((h - (mTileSize * mYTileCount)) / 2);

        mTileGrid = new Tile[mXTileCount][mYTileCount];
        for (int x = 0; x < mXTileCount; x++) {
        	for (int y = 0; y < mYTileCount; y++) {
        		mTileGrid[x][y] = new Tile(0, x, y); //TODO: can use constructor for just x,y
        	}
        }
    }

    /**
     * Function to set the specified Drawable as the tile for a particular
     * integer key.
     * 
     * @param key
     * @param tile
     */
    public void loadTile(int key, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap(mTileSize, mTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, mTileSize, mTileSize);
        tile.draw(canvas);
        
        mTileArray[key] = bitmap;
    }

    /**
     * Resets all tiles to 0 (empty)
     * 
     */
    public void clearTiles() {
        for (int x = 0; x < mXTileCount; x++) {
            for (int y = 0; y < mYTileCount; y++) {
            	mTileGrid[x][y].setColor(0);
            }
        }
    }
    
    public Tile getAbove(Tile t) {
    	try {
    		return getTile(t.getX(), t.getY() + 1);
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		return null;
    	}
    }
    
    public Tile getBelow(Tile t) {
    	try {
    		return getTile(t.getX(), t.getY() - 1);
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		return null;
    	}
    }
    
    public Tile getRight(Tile t) {
    	try {
    		return getTile(t.getX() + 1, t.getY());
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		return null;
    	}
    }

    public Tile getLeft(Tile t) {
    	try {
    		return getTile(t.getX() - 1, t.getY());
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		return null;
    	}
    }

    
    

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
    
    //TODO documentation
    @Override
	public boolean onTouchEvent(MotionEvent event) {        
		return super.onTouchEvent(event);
	}


}
