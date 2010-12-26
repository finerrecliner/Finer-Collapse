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
	
	
	
	/*************** End Private Attributes *******************/
	
	
	/****************** Public Methods ***********************/
	
	/**
	 * constructor
	 */
	public Settings(Difficulty newDifficulty,
					int newPrefilledRows,
					int newColors,
					int newAddRowTime)
	{
		difficulty = newDifficulty;
		prefilledRows = newPrefilledRows;
		colors = newColors;
		addRowTime = newAddRowTime;		
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
	
	
	
	
	/***************** End Public Methods *********************/
}