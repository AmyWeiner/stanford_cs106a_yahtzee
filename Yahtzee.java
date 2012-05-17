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
		scoreCard = new int [N_CATEGORIES][nPlayers];
		initializeScoreCard();
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

	private void initializeScoreCard() {
		for (int i = 0; i < N_CATEGORIES; i ++) {
			for ( int j = 0; j < nPlayers; j ++) {
				scoreCard[i][j] = -1;
			}
		}
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
		int score = calculateScore(category, dice);
		recordScore(category, player, score);
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

	private int calculateScore(int category, int[] dice) {
		int total = 0;
		switch (category) {
		case 0: case 1: case 2: case 3: case 4: case 5: 
			for (int i = 0; i < N_DICE; i ++) {
				if (dice[i] == category + 1) {
					total += dice[i];
				}
			}
			return total;
		case 8: 
			for (int i = 0; i < N_DICE; i ++) {
				total += dice[i];
			}
			return total;
		case 9: 
			for (int i = 0; i < N_DICE; i ++) {
				total += dice[i];
			}
			return total;
		case 10: 
			total = 25;
			return total;
		case 11: 
			total = 30;
			return total;
		case 12: 
			total = 40;
			return total;
		case 13: 
			total = 50;
			return total;
		case 14: 
			for (int i = 0; i < N_DICE; i ++) {
				total += dice[i];
			}
			return total;
		default: break;
		}
		return category;
	}

	private void recordScore(int category, int player, int score) {
		for (int i = 0; i < N_CATEGORIES; i ++) {
			for ( int j = 0; j < nPlayers; j ++) {
				if (i == category && j == player) {
					if (scoreCard[i][j] == -1) {
					scoreCard[i][j] = score;
					ui.updateScorecard(category, player, score);
					}
				}
			}
		}
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
