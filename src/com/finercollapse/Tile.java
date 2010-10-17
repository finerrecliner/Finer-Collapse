/**
 * 
 */
package com.finercollapse;

/**
 * Represents an individual item on the game board
 */
public class Tile {
	
    /******************* Attributes **********************/
	
    /**
     * Location in mTileGrid
     */
    private int x;
    private int y;
    
    /**
     * Offset from default drawing location on screen.
     * Used to simulate animations 
     */
    private int xOffset; 
    private int yOffset;
    
	/**
	 * Distinguishes different types of Tiles from each other,
	 * when being drawn.
	 * (example: red, green, yellow, etc)
	 */
	private int color;
	
    /**
     * Used to support Breadth First Search algorithm 
     */
    private BFS BFSStatus;
    
    /**
     * Animation Direction (i.e. which direction the Tile is going, if any) 
     */
    private AnimDirection animDirection;
    
    /**
     * Adjacent Tiles
     */
    private Tile up;
    private Tile down;
    private Tile left;
    private Tile right;
    
    /******************* End Attributes **********************/
    
    /********************* Structures ***************************/


    /**
     * Tile's state while used in a Breadth First Search algorithm
     */
    public enum BFS {
      	UNDISCOVERED,
      	DISCOVERED,
      	HANDLED,
    }
    
    /**
     * directions a Tile can move
     */
    public enum AnimDirection {
    	NONE,
    	UP,
    	DOWN,
    	LEFT,
    	RIGHT,
    }
    
    /****************** End Structures ***********************/
    
    /********************* Methods ***************************/
    

    /**
     * Constructor
     * 
     * @param newColor set initial color
     * @param newX set X location in mTileGrid
     * @param newY set Y location in mTileGrid
     */
    public Tile(int newColor, int newX, int newY) {
    	tileConstructor(newColor, newX, newY);
    }
    
    /**
     * Constructor
     * 
     * @param newX set X location in mTileGrid
     * @param newY set Y location in mTileGrid
     */
    public Tile(int newX, int newY) {
    	tileConstructor(0, newX, newY);
    }
    
    /**
     * Constructor helper method
     * 
     * @param newColor set initial color
     * @param newX set X location in mTileGrid
     * @param newY set Y location in mTileGrid
     */
    private void tileConstructor(int newColor, int newX, int newY) {
        color = newColor;
        x = newX;
        y = newY;
        xOffset = 0;
        yOffset = 0;
    }
    
    /* accessors */
    public int getX() { return x; }
    public int getY() { return y; }
    public int getXOffset() { return xOffset; }
    public int getYOffset() { return yOffset; }
    public int getColor() { return color; }
    public BFS getBFSStatus() { return BFSStatus; }

    
    /* modifiers */
    public void setColor(int c) { color = c; }
    public void setBFSStatus(BFS s) { BFSStatus = s; }
    public void setAnimDirection(AnimDirection ad) { animDirection = ad; }
    public void setAdjacents(Tile u, Tile d, Tile l, Tile r) { up = u; down = d; left = l; right = r; }
    
    /**
     * reset Tile to initial position 
     */
    public void resetOffset() { xOffset = yOffset = 0; }
    
    /**
     * modify the Offset
     * @param i amount to adjust the offset by (relative to current position)
     */
    public void modXOffset(int i) { xOffset += i; }
    public void modYOffset(int i) { yOffset += i; }
    
    
    
    
    /**
     * Update this tile's Offsets, so when the screen is redrawn, 
     * it will have appeared to have moved positions
     * @param distance how far to move
     * @return true if done moving one space <br>
     *         false if it has not reached its final destination yet
     */
    public boolean animate(int distance) {
    	switch (animDirection) {
    	case UP:
    		return animateUp(distance);
    		
    	case DOWN: 
    		return animateDown(distance);
    		
    	case NONE: //fall through
    	default: 
    		return false;
    	
    	}
    }
    
    /**
     * Moves a Tile up a little bit per redraw.
     * @param height number of pixels to move in complete animation
     * @return True when it reaches it final position. <br> 
     * 		   False if there is more to animate
     */
    public boolean animateUp(int height) {
    		int offset = 4; //TODO magic number

    		/* animation is complete */
	    	if (yOffset - offset <= 0) {
	    		animDirection = AnimDirection.NONE;
	    		up.setColor(color);
	    		if (down != null) {
	    			color = down.getColor();
	    		} else {
	    			color = 0;
	    		}
	    		resetOffset();
	    		return true;
	    	} else {
	    		yOffset -= offset; 
	    		return false;
	    	}
    }    
    
    /**
     * Moves a Tile down a little bit per redraw
     * @param height number of pixels to move in complete animation
     * @return True when it reaches it final position. <br> 
     * 		   False if there is more to animate
     */
    public boolean animateDown(int height) {
    		int offset = 4; //TODO magic number

    		/* animation is complete */
	    	if (yOffset + offset >= height) {
	    		animDirection = AnimDirection.NONE;
	    		down.setColor(color);
	    		color = up.getColor(); //TODO see up()
	    		resetOffset();
	    		return true;
	    	} else {
	    		yOffset += offset; 
	    		return false;
	    	}
    }
    

    /**
     * @return Tile's color represented a single character. <br>
     * 		   A space means the Tile is BLANK. <br>
     *         example: red = 'r'.
     */
    public char getColorChar() {
    	char retval;
    	
    	switch (color) {
    	case 0: retval = ' '; break;
    	case 1: retval = 'r'; break;
    	case 2: retval = 'y'; break;
    	case 3: retval = 'g'; break;
    	default: retval = 'X';	//error case
    	}
    	
    	return retval;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Tile: [" + color + "," + x + "," + y + "]";
    }
    
    /****************** End Methods ***********************/
    
}
