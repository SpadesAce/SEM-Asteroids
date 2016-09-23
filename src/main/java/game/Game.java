package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entity.AbstractEntity;
import entity.Asteroid;
import entity.Bullet;
import entity.Player;
import entity.Saucer;
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
	private final Spawner spawner;
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
	 * height of canvas in pixels.
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
	 * current score for arcade mode.
	 */
	private long arcadeScore;
	/**
	 * current score for survival mode.
	 */
	private long survivalScore;
	/**
	 * current highscore for arcade mode.
	 */
	private long arcadeHighscore;
	/**
	 * current highscore for survival mode.
	 */
	private long survivalHighscore;
	/**
	 * current gamemode.
	 */
	private int gamemode;
	/**
	 * the time at which the game was paused.
	 */
	private long pauseTime;
	/**
	 * The gamemode the game was in before the game was paused.
	 */
	private int prePauseGamemode;
	
	/**
	 * Size of canvas.
	 */
	private static final float CANVAS_SIZE = 500;
	/**
	 * Minimal restart time.
	 */
	private static final long MINIMAL_RESTART_TIME = 300;
	/**
	 * Number of points needed to gain a life.
	 */
	private static final int LIFE_SCORE = 10000;
	////////////////////////////////////////
	//                                    //
	//    here is where gamemodes start   //
	//                                    //
	////////////////////////////////////////
	/**
	 * the startscreen gamemode.
	 */
	private static final int GAMEMODE_START_SCREEN = 0;
	/**
	 * the "arcade" gamemode.
	 */
	private static final int GAMEMODE_ARCADE = 1;
	/**
	 * the arcade highscore screen.
	 */
	private static final int GAMEMODE_ARCADE_HIGHSCORE_SCREEN = 2;
	/**
	 * the survival highscore screen.
	 */
	private static final int GAMEMODE_SURVIVAL_HIGHSCORE_SCREEN = 3;
	/**
	 * the pause screen.
	 */
	private static final int GAMEMODE_PAUSE_SCREEN = 4;
	/**
	 * the "survival" gamemode.
	 */
	private static final int GAMEMODE_SURVIVAL = 5;
	////////////////////////////////////////
	//                                    //
	//    here is where gamemodes end     //
	//                                    //
	////////////////////////////////////////
	/**
	 * Minimal pause time.
	 */
	private static final long MINIMAL_PAUSE_TIME = 300;
	/**
	 * Size of a big asteroid in survival.
	 */
	private static final long SURVIVAL_ASTEROID_SIZE_BIG = 4;

	/**
	 * Constructor for a new game.
	 * 
	 * @param gc
	 *            - the GraphicsContext of the canvas
	 */
	public Game(final GraphicsContext gc) {
		this.gc = gc;
		Logger.getInstance().log("Game constructed.");
		screenX = CANVAS_SIZE;
		screenY = CANVAS_SIZE;
		spawner = new Spawner(this);
		entities = new ArrayList<>();
		destroyList = new ArrayList<>();
		createList = new ArrayList<>();
		random = new Random();
		readHighscores();
	}
	
	/**
	 * reads the highscore from file in resources folder.
	 */
	private void readHighscores() {
		long[] highscores = {0, 0};
		final String filePath = "src/main/resources/highscore.txt";
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(filePath),
						StandardCharsets.UTF_8))) {
			String sCurrentLine;
			int index = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				highscores[index] = Long.parseLong(sCurrentLine);
				index++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			Logger.getInstance().log("unable to read highscore from file");
		}
		arcadeHighscore = highscores[0];
		survivalHighscore = highscores[1];
	}

	/**
	 * writes the highscore to file in resources folder.
	 */
	private void writeHighscores() {
		final String arcadeContent = String.valueOf(arcadeHighscore);
		final String survivalContent = String.valueOf(survivalHighscore);
		final File file = new File("src/main/resources/highscore.txt");
		try (FileOutputStream fos =
					 new FileOutputStream(file.getAbsoluteFile())) {
			fos.write(arcadeContent.getBytes(StandardCharsets.UTF_8));
			fos.write("\n".getBytes(StandardCharsets.UTF_8));
			fos.write(survivalContent.getBytes(StandardCharsets.UTF_8));
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			Logger.getInstance().log("unable to write highscore to file");
		}
	}
	
	/**
	 * Starts or restarts the game, with initial entities.
	 */
	public final void startGame() {
		restartTime = System.currentTimeMillis();
		pauseTime = restartTime;
		entities.clear();
		player = new Player(screenX / 2, screenY / 2, 0, 0, this);
		entities.add(player);
		if (this.arcadeScore > arcadeHighscore) {
			arcadeHighscore = this.arcadeScore;
		}
		if (this.survivalScore > survivalHighscore) {
			survivalHighscore = this.survivalScore;
		}
		writeHighscores();
		arcadeScore = 0;
		gamemode = GAMEMODE_START_SCREEN;
		spawner.reset();
		Logger.getInstance().log("Game started.");
	}
	
	/**
	 * update runs every game tick and updates all necessary entities.
	 * 
	 * @param input
	 *            - all keys pressed at the time of update
	 */
	public final void update(final List<String> input) {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, screenX, screenY);
		
		switch(gamemode) {
		case GAMEMODE_START_SCREEN:
			updateStartScreen(input);
			break;
		case GAMEMODE_ARCADE:
		case GAMEMODE_SURVIVAL:
			updateGame(input);
			break;
		case GAMEMODE_ARCADE_HIGHSCORE_SCREEN:
		case GAMEMODE_SURVIVAL_HIGHSCORE_SCREEN:
			updateHighscoreScreen(input);
			break;
		case GAMEMODE_PAUSE_SCREEN:
			updatePauseScreen(input);
			break;
		default:
			gamemode = GAMEMODE_START_SCREEN;
		}
	}
	
	/**
	 * handles the update logic of the pause screen.
	 * @param input - input of keyboard
	 */
	private void updatePauseScreen(final List<String> input) {
		if (input.contains("P") && System.currentTimeMillis() 
				- pauseTime > MINIMAL_PAUSE_TIME) {
			pauseTime = System.currentTimeMillis();
			Logger.getInstance().log("Game unpaused.");
			gamemode = prePauseGamemode;
		} else if (input.contains("R") && System.currentTimeMillis() 
				- restartTime > MINIMAL_RESTART_TIME) {
			Logger.getInstance().log("Game stopped.");
			startGame();
			gamemode = prePauseGamemode;
		}
		Display.pauseScreen(gc);
	}
	

	/**
	 * handles the update logic of the start screen.
	 * 
	 * @param input
	 * 			  - all keys pressed at the time of update
	 */
	private void updateStartScreen(final List<String> input) {
		if (input.contains("A")) {
			startGame();
			gamemode = GAMEMODE_ARCADE;
		}
		if (input.contains("S")) {
			startGame();
			gamemode = GAMEMODE_SURVIVAL;
		}
		Display.startScreen(gc);
	}
	
	/**
	 * handles the update logic of the game itself.
	 * 
	 * @param input
	 * 			  - all keys pressed at the time of update
	 */
	private void updateGame(final List<String> input) {
		if (input.contains("R") && System.currentTimeMillis() 
				- restartTime > MINIMAL_RESTART_TIME) {
			Logger.getInstance().log("Game stopped.");
			gamemode = GAMEMODE_START_SCREEN;
		} else if (input.contains("P") && System.currentTimeMillis() 
				- pauseTime > MINIMAL_PAUSE_TIME) {
			pauseTime = System.currentTimeMillis();
			Logger.getInstance().log("Game paused.");
			prePauseGamemode = gamemode;
			gamemode = GAMEMODE_PAUSE_SCREEN;
		}
		for (final AbstractEntity e : entities) {
			e.update(input);
			checkCollision(e);
			e.draw(gc);
		}
		
		switch (gamemode) {
		case GAMEMODE_ARCADE:
			spawner.updateArcade();
			break;
		case GAMEMODE_SURVIVAL:
			spawner.updateSurvival();
			break;
		default:
			gamemode = GAMEMODE_START_SCREEN;
		} 
		
		destroyList.forEach(AbstractEntity::onDeath);
		entities.removeAll(destroyList);
		entities.addAll(createList);
		createList.clear();
		destroyList.clear();
		createList.clear();
		switch (gamemode) {
		case GAMEMODE_ARCADE:
			Display.score(arcadeScore, gc);
			Display.highscore(arcadeHighscore, gc);
			break;
		case GAMEMODE_SURVIVAL:
			Display.score(survivalScore, gc);
			Display.highscore(survivalHighscore, gc);
			break;
		default:
			gamemode = GAMEMODE_START_SCREEN;
		}
		Display.lives(player.getLives(), gc);
	}
	
	/**
	 * handles the update logic of the highscore screen.
	 * 
	 * @param input
	 * 			  - all keys pressed at the time of update
	 */
	private void updateHighscoreScreen(final List<String> input) {
		if (input.contains("R")) {
			Logger.getInstance().log("Game stopped.");
			startGame();
		}
		switch (gamemode) {
		case GAMEMODE_ARCADE_HIGHSCORE_SCREEN:
			Display.highscoreScreen(arcadeHighscore, gc);
			break;
		case GAMEMODE_SURVIVAL_HIGHSCORE_SCREEN:
			Display.highscoreScreen(survivalHighscore, gc);
			break;
		default:
			gamemode = GAMEMODE_START_SCREEN;
		}
	}

	/**
	 * checks all collisions of an entity, if there is a hit then collide of the
	 * entity class will be run.
	 * 
	 * @param e1
	 *            - the entity
	 */
	public final void checkCollision(final AbstractEntity e1) {
		entities
				.stream()
				.filter(e2 -> !e1.equals(e2)
						&& AbstractEntity.collision(e1, e2)
						&& !destroyList.contains(e1)
						&& !destroyList.contains(e2))
				.forEach(e1::collide);
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
	 * Game over function, destroys the player.
	 */
	public final void over() {
		destroy(player);

		long score;
		long highscore;
		
		switch (gamemode) {
		case GAMEMODE_ARCADE:
			score = arcadeScore;
			highscore = arcadeHighscore;
			break;
		case GAMEMODE_SURVIVAL:
			score = survivalScore;
			highscore = survivalHighscore;
			break;
		default:
			score = 0;
			highscore = 1;
		}
		
		Logger.getInstance().log("Game over.");
		if (score <= highscore) {
			gamemode = GAMEMODE_START_SCREEN;
		} else {
			highscore = score;
			writeHighscores();
			if (gamemode == GAMEMODE_ARCADE) {
				gamemode = GAMEMODE_ARCADE_HIGHSCORE_SCREEN;
			} else if (gamemode == GAMEMODE_SURVIVAL) {
				gamemode = GAMEMODE_SURVIVAL_HIGHSCORE_SCREEN;
			}
			Logger.getInstance().log("New highscore is " + highscore + ".");
		}
	}

	/**
	 * Adds score to this.score.
	 * @param score - the score to be added.
	 */
	public final void addScore(final int score) {
		int currentScore = 0;
		if (gamemode == GAMEMODE_ARCADE) {
			currentScore = (int) arcadeScore;
		} else if (gamemode == GAMEMODE_SURVIVAL) {
			currentScore = (int) survivalScore;
		}
		if (player.isAlive()) {
			if (currentScore % LIFE_SCORE + score >= LIFE_SCORE) {
			Logger.getInstance().log("Player gained " + score + " points.");
			
				player.gainLife();
				Logger.getInstance().log("Player gained an extra life.");
			}
			currentScore += score;
		}
		if (gamemode == GAMEMODE_ARCADE) {
			arcadeScore = currentScore;
		} else if (gamemode == GAMEMODE_SURVIVAL) {
			survivalScore = currentScore;
		}
	}
	
	/**
	 * Amount of bullets currently in game.
	 * @return amount of bullets
	 */
	public final int bullets() {
		int bullets = 0;
		for (final AbstractEntity entity : entities) {
			if (entity instanceof Bullet && ((Bullet) entity).isFriendly()) {
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
		for (final AbstractEntity entity : entities) {
			if (entity instanceof Asteroid || entity instanceof Saucer) {
				enemies++;
			}
		}
		return enemies;
	}
	
	/**
	 * Amount of big enemies, where 2 medium asteroids count as 1 big
	 * big asteroid, and 2 small asteroids count as 1 medium asteroid.
	 * @return amount of converted big enemies
	 */
	public final int convertedBigEnemies() {
		int enemies = 0;
		for (final AbstractEntity entity : entities) {
			if (entity instanceof Asteroid) {
				enemies += ((Asteroid) entity).getSurvivalSize();
			}
		}
		if (enemies % SURVIVAL_ASTEROID_SIZE_BIG == 0) {
			return (int) (enemies / SURVIVAL_ASTEROID_SIZE_BIG);
		}
		return (int) (enemies / SURVIVAL_ASTEROID_SIZE_BIG) + 1;
	}

	/**
	 * CanvasSize getter.
	 * @return canvas size
	 */
	public static float getCanvasSize() {
		return CANVAS_SIZE;
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
	 * Score getter.
	 * @return score
	 */
	public final long getScore() {
		if (gamemode == GAMEMODE_ARCADE) {
			return arcadeScore;
		} else if (gamemode == GAMEMODE_SURVIVAL) {
			return survivalScore;
		}
		return 0;
	}

	/**
	 * Player getter.
	 * @return the player
	 */
	public final Player getPlayer() {
		return player;
	}

	/**
	 * random getter.
	 * @return the random
	 */
	public final Random getRandom() {
		return random;
	}
}