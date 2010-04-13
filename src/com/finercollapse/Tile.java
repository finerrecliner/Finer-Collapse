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
    private int xOffset;
    private int yOffset;
	private int color;
	private int distance;
    private Tile pred; //predecessor
    private BFS status;

  
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
    }
    
    /* accessors */
    public int getX() { return x; }
    public int getY() { return y; }
    public int getXOffset() { return xOffset; }
    public int getYOffset() { return yOffset; }
    public int getColor() { return color; }
    public BFS getStatus() { return status; }
    public int getDistance() {return distance; }
    
    /* modifiers */
//    public void setXOffset(int xo) { xOffset = xo; }
//    public void setYOffset(int yo) { yOffset = yo; }
    public void setColor(int c) { color = c; }
    public void setStatus(BFS s) { status = s; }
    public void setDistance(int d) { distance = d; }
    public void setPred(Tile p) { pred = p; }
    
    
//    public boolean animateDown(int height) {
//    	yOffset += 10; //TODO magic number
//    	
//    	if (yOffset >= height) {
//    		yOffset = 0;
//    		return true;
//    	} else {
//    	   	return false;
//    	}
//    }
    
//    public boolean equals(Tile other) {
//        if (x == other.x && y == other.y) {
//            return true;
//        }
//        return false;
//    }

    @Override
    public String toString() {
        return "Tile: [" + color + "," + x + "," + y + "]";
    }
}
