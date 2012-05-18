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
		scoreCard = new int [N_CATEGORIES][nPlayers];
		initializeScoreCard();
		playGame();
	}

	/*
	 * This method plays a single game of Yahtzee.
	 */
	private void playGame() {
		int turns = N_SCORING_CATEGORIES;
		for (int i = 0; i < N_SCORING_CATEGORIES; i++) {
			for (int j = 0; j < nPlayers; j ++) {
				takeTurn(j, playerNames[j]);
			}
			turns --;
		}
		for (int i = 0; i < nPlayers; i++) {
			calculateUpperScore(i);
			calculateLowerScore(i);
		}
		displayWinner();
	}

	/*
	 * This method initializes the scoreCard with a value of -1 for all of the categories, for each player.
	 */
	private void initializeScoreCard() {
		for (int i = 0; i < N_CATEGORIES; i ++) {
			for ( int j = 0; j < nPlayers; j ++) {
				scoreCard[i][j] = -1;
			}
		}
	}

	private void takeTurn(int player, String name) {
		ui.printMessage(name + "'s turn! Click \"Roll Dice\" button to roll the dice.");				//prompts player to 
		int[] dice = new int[N_DICE];
		ui.waitForPlayerToClickRoll(player);
		for (int i = 0; i < N_DICE; i ++) {
			dice[i] = rgen.nextInt(1, 6);
		}
		ui.displayDice(dice);
		reRoll(player, dice);
		reRoll(player, dice);
		ui.printMessage("Select a category for this roll.");
		int category = ui.waitForPlayerToSelectCategory();
		while (!isAvailableCategory(category, player)){
			ui.printMessage("You already picked that category. Please choose another category");
			category = ui.waitForPlayerToSelectCategory();
		} 
		int score = calculateScore(category, dice);
		recordScore(category, player, score);
		ui.updateScorecard(category, player, score);
		int total = calculateTotal(player);
		ui.updateScorecard(TOTAL, player, total);
		//scoreCard[TOTAL][player] = total;
	}

	private void reRoll(int player, int[] dice) {
		ui.printMessage("Select the dice you wish to re-roll and click \"Roll Again\".");
		ui.waitForPlayerToSelectDice();
		for (int i = 0; i< N_DICE; i ++) {
			if (ui.isDieSelected(i) == true) {
				dice[i] = rgen.nextInt(1, 6);
			}
			ui.displayDice(dice);
		}
	}

	private boolean isAvailableCategory(int category, int player) {
		for (int i = 0; i < N_CATEGORIES; i ++) {
			for ( int j = 0; j < nPlayers; j ++) {
				if (i == category && j == player) {
					return (scoreCard[i][j] == -1); 
				} 
			}
		}
		return true;
	}

	private int calculateScore(int category, int[] dice) {
		int total = 0;
		switch (category) {
		case ONES: case TWOS: case THREES: case FOURS: case FIVES: case SIXES: 
			for (int i = 0; i < N_DICE; i ++) {
				if (dice[i] == category + 1) {
					total += dice[i];
				}
			}
			return total;
		case THREE_OF_A_KIND: 
			if (isNOfAKind(dice, 3)) {
				total = getDiceTotal(dice);
			} 
			return total;
		case FOUR_OF_A_KIND: 
			if (isNOfAKind(dice, 4)) {
				total = getDiceTotal(dice);
			} 
			return total;
		case FULL_HOUSE: 
			if (isFullHouse(dice)) {
				total = 25;
			}
			return total;
		case SMALL_STRAIGHT:
			if (isSmallStraight(dice)) {
				total = 30;
			}
			return total;
		case LARGE_STRAIGHT: 
			if(isLargeStraight(dice)) {
				total = 40;
			}
			return total;
		case YAHTZEE: 
			if (isNOfAKind(dice, 5)) {
				total = 50;
			}
			return total;
		case CHANCE: 
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
			for (int j = 0; j < nPlayers; j ++) {
				if (i == category && j == player) {
					if (scoreCard[category][player] == -1) {
						scoreCard[i][j] = score;
						ui.updateScorecard(category, player, score);
					} 
				}
			}
		}
	}

	private int calculateTotal(int player) {
		int total = 0;
		for (int i = 0; i < N_CATEGORIES; i ++) {
			if (scoreCard[i][player] != -1) {
				total += scoreCard[i][player];
			}
		}
		return total;
	}

	private boolean isNOfAKind(int[] dice, int n) {
		int[] numbers = new int[6];
		for (int i = 0; i < 6; i++) {
			numbers[i] = countDiceNumber(dice, i+1);
			if (numbers[i] >= n) {
				return true;
			}
		}
		return false;
	}

	private boolean isFullHouse(int[] dice) {
		return isNOfAKind(dice, 2) && isNOfAKind(dice, 3); 
	}

	private boolean isLargeStraight(int[] dice) {
		int[] numbers = new int[6];
		for (int i = 0; i < 6; i ++) {
			numbers[i] = -1;
		}
		for (int i = 0; i < N_DICE; i ++) {
			int x = dice[i];
			numbers[x-1] = 1;
		}
		return ((isLargeStraight(numbers, 0, 5)) || (isLargeStraight(numbers, 1, 6)));
	}

	private boolean isSmallStraight(int[] dice) {
		int[] numbers = new int[6];
		for (int i = 0; i < 6; i ++) {
			numbers[i] = -1;
		}
		for (int i = 0; i < N_DICE; i ++) {
			int x = dice[i];
			numbers[x-1] = 1;
		}
		return ((isSmallStraight(numbers, 0, 4)) || (isSmallStraight(numbers, 1, 5)) || (isSmallStraight(numbers, 2, 6)) 
		|| (isLargeStraight(numbers, 0, 5)) || (isLargeStraight(numbers, 1, 6)));
	}

	private int countDiceNumber(int[] dice, int n) {
		int total = 0;
		for (int i = 0; i < N_DICE; i ++) {
			if (dice[i] == n) {
				total ++;
			}
		}
		return total;
	}

	private int getDiceTotal(int[] dice) {
		int total = 0;
		for (int i = 0; i < N_DICE; i ++) {
			total += dice[i];
		} 
		return total;
	}

	private boolean isSmallStraight(int[] numbers, int start, int finish) {
		for (int i = start; i< finish; i++) {
			if (numbers[i] == -1) {
				return false;
			}
		}
		return true;
	}

	private boolean isLargeStraight(int[] numbers, int start, int finish) {
		for (int i = start; i< finish; i++) {
			if (numbers[i] == -1) {
				return false;
			}
		}
		return true;
	}
	
	private void calculateUpperScore(int player) {
		int total = 0;
		for (int i = 0; i < UPPER_SCORE; i ++) {
			total += scoreCard[i][player];
			if (total >= 63) {
				total += 35;
			}
			ui.updateScorecard(UPPER_SCORE, player, total);
		}
	}

	private void calculateLowerScore(int player) {
		int total = 0;
		for (int i = THREE_OF_A_KIND; i < LOWER_SCORE; i ++) {
			total += scoreCard[i][player];
			ui.updateScorecard(LOWER_SCORE, player, total);
		}
	}
	
	private void displayWinner() {
		int highest = 0;
		String winnerName = "";
		for (int i = 0; i < nPlayers; i ++) {
			int x = scoreCard[TOTAL][i];
			if (x > highest) {
				highest = x;
				winnerName = playerNames[i];
			}
		}
		ui.printMessage("Congratulations, " + winnerName + " you're the winner with a total score of " + highest +"!");
	}

	/* Set the window dimensions */
	public static final int APPLICATION_WIDTH = 800;
	public static final int APPLICATION_HEIGHT = 500;

	/* Private instance variables */

	private int nPlayers;
	private String[] playerNames;
	private int[][] scoreCard;
	private YahtzeeUI ui;
	private RandomGenerator rgen = RandomGenerator.getInstance();
}
