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
				takeTurn(players[i]);
			}
		}
		turns --;
	}

	private void takeTurn(int player) {
		int[] dice = new int[N_DICE];
		ui.waitForPlayerToClickRoll(player);
		for (int i = 0; i < N_DICE; i ++) {
			dice[i] = rgen.nextInt(1, 6);
		}
		ui.displayDice(dice);
		ui.waitForPlayerToSelectDice();
		for (int i = 0; i< N_DICE; i ++) {
			if (ui.isDieSelected(i)) {
				dice[i] = rgen.nextInt(1, 6);
				ui.displayDice(dice);
			}
		}
		ui.waitForPlayerToClickRoll(player);
		int category = ui.waitForPlayerToSelectCategory();
}

/* Set the window dimensions */
public static final int APPLICATION_WIDTH = 800;
public static final int APPLICATION_HEIGHT = 500;

/* Private instance variables */

private int nPlayers;
private String[] playerNames;
private YahtzeeUI ui;
private RandomGenerator rgen = RandomGenerator.getInstance();
private int turns = N_SCORING_CATEGORIES;

}
