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
		while (turns > 0) {
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
	 * This method initializes the score card with a value of -1 for all of the categories, for each player.
	 * During the course of the game, the score card is filled in with point values, and the -1 values are 
	 * replaced accordingly.
	 */
	private void initializeScoreCard() {
		for (int i = 0; i < N_CATEGORIES; i ++) {
			for ( int j = 0; j < nPlayers; j ++) {
				scoreCard[i][j] = -1;
			}
		}
	}

	/*
	 * This method allows the player to take a single turn, alternating between players in the case of a multiplayer game.
	 */
	private void takeTurn(int player, String name) {
		ui.printMessage(name + "'s turn! Click \"Roll Dice\" button to roll the dice.");	//prompts player to roll dice
		int[] dice = new int[N_DICE];
		ui.waitForPlayerToClickRoll(player);
		for (int i = 0; i < N_DICE; i ++) {
			dice[i] = rgen.nextInt(1, 6);
		}
		ui.displayDice(dice);
		reRoll(player, dice);
		reRoll(player, dice);
		ui.printMessage("Select a category for this roll.");								//prompts player to select scoring category
		int category = ui.waitForPlayerToSelectCategory();
		while (!isAvailableCategory(category, player)){
			ui.printMessage("You already picked that category. Please choose another category");  //notifies player category already chosen
			category = ui.waitForPlayerToSelectCategory();
		} 
		int score = calculateScore(category, dice);
		recordScore(category, player, score);
		int total = calculateTotal(player);
		scoreCard[TOTAL][player] = total;
		ui.updateScorecard(category, player, score);
		ui.updateScorecard(TOTAL, player, total);
	}

	/*
	 * This method allows the player to reroll the dice.
	 */
	private void reRoll(int player, int[] dice) {
		ui.printMessage("Select the dice you wish to re-roll and click \"Roll Again\".");  //prompts player to roll again
		ui.waitForPlayerToSelectDice();
		for (int i = 0; i< N_DICE; i ++) {
			if (ui.isDieSelected(i)) {
				dice[i] = rgen.nextInt(1, 6);
			}
		}
		ui.displayDice(dice);
	}

	/*
	 * This method determines whether or not the category selected by the payer is an available
	 * category by checking whether or not its corresponding grid cell on the score card contains
	 * the value -1. If so, a category is still valid. If not, that category is no longer valid, since
	 * it now contains a points value, indicating that the player has already chosen this particular
	 * category on a previous turn.
	 */
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

	/*
	 * This method calculates the score earned for each player during each turn of the game. If the
	 * category chosen by the player is valid for the given dice roll at the end of the turn, the score
	 * is calculated according to the rules of the Yahtzee game. If the player picks a category that
	 * is not valid for that particular dice roll, then the player earns a score of zero for that turn.
	 */
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

	/*
	 * This method records the score on the game score card for the given category for that turn.
	 */
	private void recordScore(int category, int player, int score) {
		if (scoreCard[category][player] == -1) {
			scoreCard[category][player] = score;
			ui.updateScorecard(category, player, score);
		} 
	}

	/*
	 * This method calculates the running game total.
	 */
	private int calculateTotal(int player) {
		int total = 0;
		for (int i = 0; i < CHANCE; i ++) {
			if (scoreCard[i][player] != -1) {
				total += scoreCard[i][player];
			}
		}
		return total;
	}

	/*
	 * This method determines whether or not a given dice roll contains the indicated number of matching
	 * dice values from amongst all of the dice. 
	 */
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

	/*
	 * This method determines whether or not a given dice roll contains a distinct two of a kind
	 * and three of a kind. 
	 */
	private boolean isFullHouse(int[] dice) {
		return isNOfAKind(dice, 2) && isNOfAKind(dice, 3); 
	}

	/*
	 * This method determines whether or not a given dice roll contains a small straight by checking for
	 * each of the three possible combinations of small straights, as well both of the combinations for
	 * a large straight. 
	 */
	private boolean isSmallStraight(int[] dice) {
		boolean[] numbers = new boolean[6];
		numbers = markDieNumbers(dice, numbers);
		return ((isStraight(numbers, 0, 4)) || (isStraight(numbers, 1, 5)) || (isStraight(numbers, 2, 6)) 
				|| (isStraight(numbers, 0, 5)) || (isStraight(numbers, 1, 6)));
	}

	/*
	 * This method determines whether or not a given dice roll contains a large straight by checking for
	 * both of the possible combinations for a large straight. 
	 */
	private boolean isLargeStraight(int[] dice) {
		boolean[] numbers = new boolean[6];
		numbers = markDieNumbers(dice, numbers);
		return ((isStraight(numbers, 0, 5)) || (isStraight(numbers, 1, 6)));
	}

	/*
	 * This method creates an array of six elements, one for each possible die value, and initializes each element in
	 * the array to false. As each die value is checked for the given roll, that value of the corresponding index false of the
	 * initialized array is changed from false to true. 
	 */
	private boolean[] markDieNumbers(int[] dice, boolean[] numbers) {
		for (int i = 0; i < N_DICE; i ++) {
			int x = dice[i];
			numbers[x-1] = true;
		}
		return numbers;
	}

	/*
	 * This method determines whether or not a given dice roll contains a straight. 
	 */
	private boolean isStraight(boolean[] numbers, int start, int finish) {
		for (int i = start; i< finish; i++) {
			if (numbers[i] == false) {
				return false;
			}
		}
		return true;
	}

	/*
	 * This method determines the number of dice containing the indicated value. 
	 */
	private int countDiceNumber(int[] dice, int n) {
		int total = 0;
		for (int i = 0; i < N_DICE; i ++) {
			if (dice[i] == n) {
				total ++;
			}
		}
		return total;
	}

	/*
	 * This method determines the sum of all of the values on the dice. 
	 */
	private int getDiceTotal(int[] dice) {
		int total = 0;
		for (int i = 0; i < N_DICE; i ++) {
			total += dice[i];
		} 
		return total;
	}

	/*
	 * This method calculates the upper score at the end of the game. 
	 */
	private void calculateUpperScore(int player) {
		int total = 0;
		for (int i = 0; i < UPPER_SCORE; i ++) {
			total += scoreCard[i][player];
			if (total >= 63) {
				total += 35;
				scoreCard[UPPER_BONUS][player] = 35;
				ui.updateScorecard(UPPER_BONUS, player, 35);
				scoreCard[UPPER_SCORE][player] = total;
				ui.updateScorecard(UPPER_SCORE, player, total);
			} else {
			scoreCard[UPPER_BONUS][player] = 0;
			ui.updateScorecard(UPPER_BONUS, player, 0);
			scoreCard[UPPER_SCORE][player] = total;
			ui.updateScorecard(UPPER_SCORE, player, total);
			}
		}
	}

	/*
	 * This method calculates the lower score at the end of the game. 
	 */
	private void calculateLowerScore(int player) {
		int total = 0;
		for (int i = THREE_OF_A_KIND; i < LOWER_SCORE; i ++) {
			total += scoreCard[i][player];
		}
		scoreCard[LOWER_SCORE][player] = total;
		ui.updateScorecard(LOWER_SCORE, player, total);
	}

	/*
	 * This method displays the winner at the end of the game. 
	 */
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
		ui.printMessage("Congratulations, " + winnerName + " you're the winner with a total score of " + highest +"!");  //displays winner
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
