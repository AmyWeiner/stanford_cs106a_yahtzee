/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game from Assignment #5.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {

	/*
	 * The run method uses the IODialog class from the acm.io package to initialize
	 * the game.  It first allows the user to choose the number of players and then
	 * reads in the name of each player.  Once the initialization is complete, the
	 * run method initializes the Yahtzee display and calls the playGame method.
	 */

	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players", 1, MAX_PLAYERS);
		playerNames = new String[nPlayers];
		for (int i = 0; i < nPlayers; i++) {
			playerNames[i] = dialog.readLine("Enter name for player " + (i + 1));
		}
		ui = new YahtzeeUI(playerNames);
		int[] players = new int[nPlayers];
		playGame(players);
	}

	/*
	 * This method plays a single game of Yahtzee.
	 */
	private void playGame(int[] players) {
		while ( turns > 0) {
			for (int i = 0; i < nPlayers; i ++) {
				takeTurn(players[i], playerNames[i]);
			}
		}
		turns --;
	}

	private void takeTurn(int player, String name) {
		ui.printMessage(name + "'s turn! Click \"Roll Dice\" button to roll the dice.");
		int[] dice = new int[N_DICE];
		ui.waitForPlayerToClickRoll(player);
		for (int i = 0; i < N_DICE; i ++) {
			dice[i] = rgen.nextInt(1, 6);
		}
		ui.displayDice(dice);
		reRoll(player, name, dice);
		reRoll(player, name, dice);
		ui.printMessage("Select a category for this roll.");
		int category = ui.waitForPlayerToSelectCategory();
		int score = calculateScore(category);
		//ui.updateScorecard(category, player, score);
	}


	private void reRoll(int player, String name, int[] dice) {
		ui.printMessage("Select the dice you wish to re-roll and click \"Roll Again\".");
		ui.waitForPlayerToSelectDice();
		for (int i = 0; i< N_DICE; i ++) {
			if (ui.isDieSelected(i)) {
				dice[i] = rgen.nextInt(1, 6);
			}
			ui.displayDice(dice);
		}
	}
	
	private int calculateScore(int category) {
		switch (category) {
		case 0: break;
		case 1: break;
		case 2: break;
		case 3: break;
		case 4: break;
		case 5: break;
		case 6: break;
		case 7: break;
		case 8: break;
		case 9: break;
		case 10: break;
		case 11: break;
		case 12: break;
		case 13: break;
		case 14: break;
		case 15: break;
		case 16: break;
		default: break;
		}
		return category;
		
	}

	/* Set the window dimensions */
	public static final int APPLICATION_WIDTH = 800;
	public static final int APPLICATION_HEIGHT = 500;

	/* Private instance variables */

	private int nPlayers;
	private String[] playerNames;
	private int[] [] scoreCard;
	private YahtzeeUI ui;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int turns = N_SCORING_CATEGORIES;

}
