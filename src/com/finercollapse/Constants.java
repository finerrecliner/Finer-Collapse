package com.finercollapse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Constants {
    
    /**
     * Random Number Generator
     */
    public static final Random RNG = new Random();
	
    /**
     * Label for drawables that the TileView class will use
     */
    public static enum Color {
    	BLANK,
    	RED,
    	YELLOW,
    	GREEN;

        private static final List<Color> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
    	
        /**
         * @return Tile's color represented a single character. <br>
         * 		   A space means the Tile is BLANK. <br>
         *         example: red = 'r'.
         */
        public char getChar() {
        	switch (this) {
	        	case BLANK: return ' ';
	        	case RED: return 'r';
	        	case YELLOW: return 'y';
	        	case GREEN: return 'g';
	        	default: return 'X';	//error case
        	}
        }
        

        /**
         * @return A random Color <br> 
         * NOTE: will not return BLANKs
         */
        public static Color getRandom()  {
        	/* get a random number 0 - (1-size),
        	 * then add one to it before getting its corresponding 
        	 * Color, so we get a Color other than the first defined.
        	 */ 
        	return VALUES.get(RNG.nextInt(SIZE-1) + 1);
        }
        
        /**
         * @return number of Colors available
         */
        public static int getSize() {
        	return SIZE;
        }
    	
    }
    
	
    public static enum Difficulty{
    	BEGINNER,
    	EASY,
    	MEDIUM,
    	HARD,
    }
	
	/**
	 * Uncallable Constructor
	 */
	private Constants(){
		//this prevents even the native class from 
		//calling this constructor as well :
		throw new AssertionError();
	}

}
