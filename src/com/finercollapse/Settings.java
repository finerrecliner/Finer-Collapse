package com.finercollapse;

import com.finercollapse.Constants.*;

public class Settings {

	/************* Private Attributes ******************/
	
	/**
	 * Level of difficulty
	 */
	private Difficulty difficulty;
	
	/**
	 * number of rows that are prefilled at start of game
	 */
	private int prefilledRows;
	
	/**
	 * number of different color tiles available for use
	 */
	private int colors;
	
	/**
	 * Time until a new row is pushed up on the board.
	 * Measured in seconds.
	 */
	private int addRowTime;
	
	/**
	 * Minimum number of Tiles that must cleared on a click to 
	 * avoid a new row being added
	 */
	private int avoidNewRow;
	
	
	
	/*************** End Private Attributes *******************/
	
	
	/****************** Public Methods ***********************/
	
	/**
	 * constructor
	 */
	public Settings(Difficulty newDifficulty,
					int newPrefilledRows,
					int newColors,
					int newAddRowTime,
					int newAvoidNewRow)
	{
		difficulty = newDifficulty;
		prefilledRows = newPrefilledRows;
		colors = newColors + 1; //add 1 to compensate for BLANKs taking up a spot in the enum
		addRowTime = newAddRowTime;
		avoidNewRow = newAvoidNewRow;
		
	}



	/**
	 * @return the difficulty
	 */
	public Difficulty getDifficulty() {
		return difficulty;
	}



	/**
	 * @return the prefilledRows
	 */
	public int getPrefilledRows() {
		return prefilledRows;
	}



	/**
	 * @return the colors
	 */
	public int getColors() {
		return colors;
	}



	/**
	 * @return the addRowTime
	 */
	public int getAddRowTime() {
		return addRowTime;
	}



	/**
	 * @return the avoidNewRow
	 */
	public int getAvoidNewRow() {
		return avoidNewRow;
	}
	
	
	
	
	/***************** End Public Methods *********************/
}
