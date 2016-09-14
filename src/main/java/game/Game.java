package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entity.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


/**
 * This class defines everything within the game.
 * 
 * @author Kibo
 *
 */
public class Game {
	/**
	 * The player of this game.
	 */
	private Player player;
	/**
	 * The spawner of this game.
	 */
	private Spawner spawner;
	/**
	 * List of all entities currently in the game.
	 */
	private final List<AbstractEntity> entities;
	/**
	 * Object of random used to get random numbers.
	 */
	private final Random random;
	/**
	 * List of all entities to be destroyed at the and of the tick.
	 */
	private final List<AbstractEntity> destroyList;
	/**
	 * List of all entities to be created at the and of the tick.
	 */
	private final List<AbstractEntity> createList;
	/**
	 * length of canvas in pixels.
	 */
	private final float screenX;
	/**
	 * heigth of canvas in pixels.
	 */
	private final float screenY;
	/**
	 * the GraphicsContext, needed to draw things.
	 */
	private final GraphicsContext gc;
	/**
	 * the start time of the current game.
	 */
	private long restartTime;
	/**
	 * current score.
	 */
	private long score;
	/**
	 * Xcoordinates of the raster of the first digit.
	 */
	private final double[] scoreDisplayX = { 190, 200, 190, 200, 190, 200 }; 
	/**
	 * Ycoordinates of the raster of the first digit.
	 */
	private final double[] scoreDisplayY = { 10, 10, 20, 20, 30, 30 };
	/**
	 * the raster dots to be connected to form a digit.
	 */
	private final int[][] numberLines = { 
			{ 0, 1, 5, 4, 0 }, 
			{ 1, 5 }, 
			{ 0, 1, 3, 2, 4, 5 }, 
			{ 0, 1, 3, 2, 3, 5, 4 },
			{ 0, 2, 3, 1, 5 }, 
			{ 1, 0, 2, 3, 5, 4 }, 
			{ 1, 0, 4, 5, 3, 2 }, 
			{ 0, 1, 5 }, 
			{ 2, 0, 1, 5, 4, 2, 3 },
			{ 4, 5, 1, 0, 2, 3 } };
	
	/**
	 * Size of canvas.
	 */
	private static final float CANVAS_SIZE = 500;

	/**
	 * Radius of small Saucer.
	 */
	private static final float SMALL_SAUCER_RADIUS = 5;
	/**
	 * Speed multiplier of initial Asteroids.
	 */
	private static final float ASTEROID_SPEED = 1;
	/**
	 * Minimal restart time.
	 */
	private static final long MINIMAL_RESTART_TIME = 300;
	/**
	 * Ten.
	 */
	private static final int TEN = 10;
	/**
	 * Ofset of raster for digits in pixels.
	 */
	private static final int OFSET_PIXELS = 20;
	/**
	 * Number of points needed to gain a life.
	 */
	private static final int LIFE_SCORE = 10000;
	/**
	 * Step per difficulty level of score.
	 */
	private static final long DIFFICULTY_STEP = 10000;
	/**
	 * Max difficulty score.
	 */
	private static final long MAX_DIFFICULTY_SCORE = 10 * DIFFICULTY_STEP;

	/**
	 * Constructor for a new game.
	 * 
	 * @param gc
	 *            - the GraphicsContext of the canvas
	 */
	public Game(final GraphicsContext gc) {
		this.gc = gc;
		screenX = CANVAS_SIZE;
		screenY = CANVAS_SIZE;
		spawner = new Spawner(this);
		entities = new ArrayList<>();
		destroyList = new ArrayList<>();
		createList = new ArrayList<>();
		random = new Random();
		startGame();
	}

	/**
	 * Starts or restarts the game, with initial entities.
	 */
	public final void startGame() {
		restartTime = System.currentTimeMillis();
		entities.clear();
		player = new Player(screenX / 2, screenY / 2, 0, 0, this);
		entities.add(player);
		score = 0;
		spawner.reset();
	}
	
	/**
	 * adds a Saucer with random Y, side of screen, path and size.
	 */
	public final void addRandomSaucer() {
		Saucer newSaucer = new Saucer(((int) (random.nextInt(1) * 2)) * screenX,
				(float) Math.random() * screenY, 0, 0, this);
		if (Math.random() < smallSaucerRatio()) {
			newSaucer.setRadius(SMALL_SAUCER_RADIUS);
		}
		create(newSaucer);

	}

	/**
	 * Calculates the ratio of small saucers.
	 * @return the ratio
	 */
	private double smallSaucerRatio() {
		if (score < DIFFICULTY_STEP) {
			return .5;
		} else if (score < MAX_DIFFICULTY_SCORE) {
			return .5 + ((long) (score / DIFFICULTY_STEP)
					* .5 / (MAX_DIFFICULTY_SCORE / DIFFICULTY_STEP));
		} else {
			return 1;
		}
	}

	/**
	 * adds asteroids with random Y, side of screen and direction, but with
	 * radius 20.
	 * 
	 * @param times
	 *            - the number of asteroids
	 */
	public final void addRandomAsteroid(final int times) {
		for (int i = 0; i < times; i++) {
			entities.add(new Asteroid(0, screenY * (float) Math.random(), 
					(float) (Math.random() - .5) * ASTEROID_SPEED, 
					(float) (Math.random() - .5) * ASTEROID_SPEED, this));
		}
	}

	/**
	 * adds an asteroid.
	 * 
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @param dX - horizontal speed
	 * @param dY - vertical speed
	 * @param radius - size
	 */
	public final void addAsteroid(final float x, final float y, 
			final float dX, final float dY, final float radius) {
		final Asteroid newAsteroid = new Asteroid(x, y, dX, dY, this);
		newAsteroid.setRadius(radius);
		createList.add(newAsteroid);
	}

	/**
	 * update runs every game tick and updates all necessary entities.
	 * 
	 * @param input
	 *            - all keys pressed at the time of update
	 */
	public final void update(final List<String> input) {
		if (input.contains("R") && System.currentTimeMillis() 
				- restartTime > MINIMAL_RESTART_TIME) {
			startGame();
		}
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, screenX, screenY);
		for (final AbstractEntity e : entities) {
			e.update(input);
			checkCollision(e);
			e.draw(gc);
		}
		spawner.update();
		destroyList.forEach(AbstractEntity::onDeath);
		entities.removeAll(destroyList);
		entities.addAll(createList);
		createList.clear();
		destroyList.clear();
		createList.clear();
		drawScore(gc);
	}

	/**
	 * checks all collisions of an entity, if there is a hit then collide of the
	 * entity class will be run.
	 * 
	 * @param e1
	 *            - the entity
	 */
	public final void checkCollision(final AbstractEntity e1) {
		for (final AbstractEntity e2 : entities) {
			if (!e1.equals(e2) && AbstractEntity.collision(e1, e2)
					&& !destroyList.contains(e1) && !destroyList.contains(e2)) {
				e1.collide(e2);
			}
		}
	}

	/**
	 * draws the current score on the canvas.
	 * 
	 * @param gc
	 *            - the GraphicsContext of the canvas
	 */
	public final void drawScore(final GraphicsContext gc) {
		gc.setStroke(Color.WHITE);
		gc.setLineWidth(1);
		if (score == 0) {
			Display.draw(Display.SCORE_X, Display.SCORE_Y, 
					Display.SCORE_SIZE, "        00", gc);
			//drawDigit(gc, 0, 0);
		} else {
			String scoreString = String.format("%1$10s", score);
			Display.draw(Display.SCORE_X, Display.SCORE_Y, 
					Display.SCORE_SIZE, scoreString, gc);
			/*long rest = score;
			int digit;
			for (int i = 0; rest != 0; i++) {
				digit = (int) (rest % TEN);
				rest = (rest - digit) / TEN;
				drawDigit(gc, digit, i);
			}*/
		}
	}

	/**
	 * Draws a single digit on a predifined raster.
	 * 
	 * @param gc - the GraphicsContext of the canvas
	 * @param digit - the digit
	 * @param offset - the offset to the left of the starting position
	 *                 (the '1' in 12 should have an offset of 1 and the '2' 0)
	 */
	private void drawDigit(final GraphicsContext gc, 
			final int digit, final int offset) {
		final int l = numberLines[digit].length;
		double[] scoreX = new double[l];
		double[] scoreY = new double[l];
		for (int i = 0; i < l; i++) {
			scoreX[i] = scoreDisplayX[numberLines[digit][i]] 
					- offset * OFSET_PIXELS;
			scoreY[i] = scoreDisplayY[numberLines[digit][i]];
		}
		gc.setStroke(Color.WHITE);
		gc.strokePolyline(scoreX, scoreY, l);
	}

	/**
	 * adds an Entity to the destroy list and will be destroyed at the and of
	 * the current tick.
	 * 
	 * @param e
	 *            - the Entity
	 */
	public final void destroy(final AbstractEntity e) {
		destroyList.add(e);
	}

	/**
	 * adds an Entity to the createList, and will be added to the game at the
	 * and of the current tick.
	 * 
	 * @param e
	 *            - the Entity
	 */
	public final void create(final AbstractEntity e) {
		createList.add(e);
	}

	/**
	 * getter for screenX.
	 * @return - screenX
	 */
	public final float getScreenX() {
		return screenX;
	}

	/**
	 * getter for screenY.
	 * @return - screenY
	 */
	public final float getScreenY() {
		return screenY;
	}

	/**
	 * Game over function, destroys the player.
	 */
	public final void over() {
		destroy(player);
	}

	/**
	 * Adds score to this.score.
	 * @param score - the score to be added.
	 */
	public final void addScore(final int score) {
		if (this.score % LIFE_SCORE + score >= LIFE_SCORE) {
			player.gainLife();
		}
		this.score += score;
	}
	/**
	 * CanvasSize getter.
	 * @return canvas size
	 */
	public static float getCanvasSize() {
		return CANVAS_SIZE;
	}

	/**
	 * Amount of bullets currently in game.
	 * @return amount of bullets
	 */
	public final int bullets() {
		int bullets = 0;
		for (AbstractEntity e : entities) {
			if (e instanceof Bullet && ((Bullet) e).isFriendly()) {
				bullets++;
			}
		}
		return bullets;
	}

	/**
	 * Amount of enemies currently in game.
	 * @return amount of enemies
	 */
	public final int enemies() {
		int enemies = 0;
		for (AbstractEntity e : entities) {
			if (e instanceof Asteroid || e instanceof Saucer) {
				enemies++;
			}
		}
		return enemies;
	}

	/**
	 * Score getter.
	 * @return score
	 */

	public final long getScore() {
		return score;
	}

	/**
	 * Player getter.
	 * @return the player
	 */
	public final Player getPlayer() {
		return player;
	}

	/**
	 * Getter for difficultyStep.
	 * @return the difficultyStep
	 */
	public static long getDifficultyStep() {
		return DIFFICULTY_STEP;
	}
}