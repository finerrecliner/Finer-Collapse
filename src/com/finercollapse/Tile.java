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
	private int color;
	private int distance;
    private Tile pred; //predecessor 
  
    public enum BFS_status {
      	UNDISCOVERED,
      	DISCOVERED,
      	HANDLED,
    }

    /* constructors */
    public Tile(int newColor, int newX, int newY) {
        color = newColor;
        x = newX;
        y = newY;
    }
    
    public Tile(int newX, int newY) {
        color = 0; //default to BLANK
        x = newX;
        y = newY;
    }
    
    /* accessors */
    public int getX() { return x; }
    public int getY() { return y; }
    public int getColor() { return color; }
    
    /* modifiers */
    public void setColor(int c) { color = c; }
    
    
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
