/**
 * 
 */
package com.finercollapse;

/**
 * @author davef
 *
 */
public class Tile {
    private int x;
    private int y;
    private int xOffset; //used for animation
    private int yOffset; //used for animation
	private int color;
	private int distance;
    private Tile pred; //predecessor
    private BFS BFSStatus;
	private AnimStatus animStatus;

	public enum AnimStatus {
		IDLE,
		DOWN,
	}
  
    public enum BFS {
      	UNDISCOVERED,
      	DISCOVERED,
      	HANDLED,
    }

    /* constructors */
    public Tile(int newColor, int newX, int newY) {
    	tileConstructor(newColor, newX, newY);
    }
    
    public Tile(int newX, int newY) {
    	tileConstructor(0, newX, newY);
    }
    
    private void tileConstructor(int newColor, int newX, int newY) {
        color = newColor;
        x = newX;
        y = newY;
        xOffset = 0;
        yOffset = 0;
        animStatus = AnimStatus.IDLE;
    }
    
    /* accessors */
    public int getX() { return x; }
    public int getY() { return y; }
    public int getXOffset() { return xOffset; }
    public int getYOffset() { return yOffset; }
    public int getColor() { return color; }
    public BFS getBFSStatus() { return BFSStatus; }
    public int getDistance() { return distance; }
    public AnimStatus getAnimStatus() { return animStatus; }
    
    /* modifiers */
    public void incXOffset(int i) { xOffset += i; }
    public void incYOffset(int i) { yOffset += i; }
    public void setColor(int c) { color = c; }
    public void setBFSStatus(BFS s) { BFSStatus = s; }
    public void setAnimStatus(AnimStatus s) { animStatus = s; }
    public void setDistance(int d) { distance = d; }
    public void setPred(Tile p) { pred = p; }
    public void resetOffset() { xOffset = yOffset = 0; }
    
    
    
    /* returns true only if it has finished an animation */
    public boolean animateDown(int height) {
//    	if (this.animStatus == AnimStatus.DOWN &&
//    		this.color != 0) {
	    	if (yOffset >= height) {
	    		this.animStatus = AnimStatus.IDLE;
	    		return true;
	    	} else {
	    		yOffset += 4; //TODO magic number
	    		return false;
	    	}
//    	} 
//    	return false;
    }
    


    /* returns Tile color as a single character 
     * returns a space if an empty tile.
     * Letter means colors. Example: Red = 'r' */
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
    	//return Character.forDigit(color,10);  //TODO remove
    }
    
    @Override
    public String toString() {
        return "Tile: [" + color + "," + x + "," + y + "]";
    }
}
