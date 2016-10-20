package game;

import java.util.List;

import display.DisplayText;
import game.highscore.HighscoreStore;
import game.highscore.model.HighScore;

/**
 * Class that maintains the score.
 * @author Esmee
 *
 */
public class ScoreCounter {
	private static final int LIFE_SCORE = 10000;

	private static final int THREE = 3;

	private long score;
	private final HighscoreStore highscoreStore;
	private final Game thisGame;
	
	/**
	 * Constructor for score counter.
	 * @param game this scorecounter belongs to
	 * @param highscoreStore the HighscoreStore used for managing the highscores
	 */
	public ScoreCounter(final Game game, final HighscoreStore highscoreStore) {
		this.thisGame = game;
		this.highscoreStore = highscoreStore;
	}

	/**
	 * Set score to 0 at start of game.
	 * Write existing score as highscore if larger than current highscore.
	 */
	protected final void startGame() {
		if (this.score > highscoreStore.getHighestScore(thisGame.getGamestate().getMode())) {
			highscoreStore.addHighScore(score, thisGame.getGamestate().getMode());
			highscoreStore.writeScores();
		}		
		score = 0;
	}
	
	/**
	 * Display score on screen.
	 */
	public final void displayScore() {
		DisplayText.score(score);
		DisplayText.highscore(highscoreStore.getHighestScore(thisGame.getGamestate().getMode()));
	}

	/**
	 * Convert highscores into readable strings for display.
	 * @return the highscore strings.
	 */
	public final String[][] highScoresToStrings() {
		final List<HighScore> highscores = highscoreStore.getHighScores();
		final String[][] out = new String[THREE][highscores.size()];
		final String[] modeString = Gamestate.getModeString();
		for (int i = 0; i < highscores.size(); i++) {
			out[0][i] = modeString[highscores.get(i).getGamemode()];
			out[1][i] = Long.toString(highscores.get(i).getScore());
			out[2][i] = highscores.get(i).getUserName();
		}
		return out;
	}
	
	/**
	 * @return the high score
	 */
	public final long getHighscore() {
		return highscoreStore.getHighestScore(thisGame.getGamestate().getMode());
	}
	
	/**
	 * Score getter.
	 *
	 * @return score
	 */
	public final long getScore() {
		return score;
	}
	
	/**
	 * @return true if the score is better than the highscore
	 */
	public final boolean isHighscore() {
		return score > highscoreStore.getHighestScore(thisGame.getGamestate().getMode());
	}
	
	/**
	 * @return true if the score is not better than the high score
	 */
	public final boolean isNotHighscore() {
		return !isHighscore();
	}
	
	/**
	 * Update the highscore.
	 */
	protected final void updateHighscore() {
		if (isHighscore()) {
			highscoreStore.addHighScore(score, thisGame.getGamestate().getMode());
			highscoreStore.writeScores();
		}
	}
	
	/**
	 * Add the amount of points to the current score.
	 * @param points amount of points to be added
	 */
	public final void addScore(final long points) {
		score += points;
	}
	
	/**
	 * Method to check if the player has enough points to gain a life.
	 * @param points amount of points that the player gains
	 * @return true when the player can gain a life
	 */
	public final boolean canGainLife(final int points) {
		return this.score % LIFE_SCORE + points >= LIFE_SCORE;
	}
	
	/**
	 * @param score the score to set
	 */
	public final void setScore(final long score) {
		this.score = score;
	}
	
	/**
	 * @param highscore the highscore to set
	 */
	public final void setHighscore(final long highscore) {
		highscoreStore.addHighScore(highscore, thisGame.getGamestate().getMode());
	}
	
	/**
	 * @return game this scorecounter belongs to
	 */
	public final Game getThisGame() {
		return thisGame;
	}

	/**
	 * clear all saved highscores, and reset the stored file.
	 */
	public final void clearHighscores() {
		highscoreStore.clear();
	}
}
