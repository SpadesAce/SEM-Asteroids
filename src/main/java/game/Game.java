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
import java.util.Optional;

import display.DisplayHud;
import display.DisplayText;
import entity.AbstractEntity;
import entity.Asteroid;
import entity.Bullet;
import entity.Player;
import entity.Saucer;
import entity.builders.PlayerBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This class defines everything within the game.
 *
 * @author Kibo
 */
public final class Game {
	private Player player;
	private Player playerTwo;
	private List<AbstractEntity> entities;
	private List<AbstractEntity> destroyList;
	private List<AbstractEntity> createList;
	private final float screenX;
	private final float screenY;
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
	private final Spawner spawner;
	private final Gamestate gamestate = new Gamestate(this);

	private static final float CANVAS_SIZE = 500;
	
	/**
	 * amount of points needed to gain an extra life.
	 */
	private static final long POINTS_PER_LIFE = 10000;
	/**
	 * Size of a big asteroid in survival.
	 */
	private static final long SURVIVAL_ASTEROID_SIZE_BIG = 4;
	private final ScoreCounter scorecounter;

	private static final boolean LOG_SCORE = false;

	/**
	 * Constructor for a new game.
	 */
	public Game() {
		Logger.getInstance().log("Game constructed.");
		screenX = CANVAS_SIZE;
		screenY = CANVAS_SIZE;
		entities = new ArrayList<>();
		spawner = new Spawner(this);
		destroyList = new ArrayList<>();
		createList = new ArrayList<>();
		scorecounter = new ScoreCounter(this);
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
			Logger.getInstance().log("unable to read highscore from file", e);
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
			Logger.getInstance().log("unable to write highscore to file", e);
		}
	}

	/**
	 * Starts or restarts the game, with initial entities.
	 */
	public void startGame() {
		gamestate.start();
		entities.clear();
		if (this.arcadeScore > arcadeHighscore) {
			arcadeHighscore = this.arcadeScore;
		}
		if (this.survivalScore > survivalHighscore) {
			survivalHighscore = this.survivalScore;
		}
		writeHighscores();
		arcadeScore = 0;
		survivalScore = 0;
		
		final PlayerBuilder pBuilder = new PlayerBuilder();
		if (gamestate.isCoop()) {
			// Create player 1
			pBuilder.setX(screenX / 2 - Player.getSpawnOffset());
			pBuilder.setY(screenY / 2);
			pBuilder.setDX(0);
			pBuilder.setDY(0);
			pBuilder.setThisGame(this);
			pBuilder.setPlayerTwo(false);
			player = (Player) pBuilder.getResult();
			// Change the relevant player two statistics
			pBuilder.setPlayerTwo(true);
			pBuilder.setX(screenX / 2 + Player.getSpawnOffset());
			playerTwo = (Player) pBuilder.getResult();
			
			entities.add(player);
			entities.add(playerTwo);
		} else {
			pBuilder.setX(screenX / 2);
			pBuilder.setY(screenY / 2);
			pBuilder.setDX(0);
			pBuilder.setDY(0);
			pBuilder.setThisGame(this);
			pBuilder.setPlayerTwo(false);
			player = (Player) pBuilder.getResult();
			entities.add(player);
		} 
		scorecounter.startGame();
		spawner.reset();
		if (gamestate.getMode() == Gamestate.getModeArcade()) {
			Logger.getInstance().log("Arcade game started.");
		} else {
			Logger.getInstance().log("Coop game started.");
		}
	}

	/**
	 * update runs every game tick and updates all necessary entities.
	 *
	 * @param input - all keys pressed at the time of update
	 */
	public void update(final List<String> input) {
		Launcher.getRoot().getChildren().clear();
		final Rectangle r = new Rectangle(0, 0, screenX, screenY);
		r.setFill(Color.BLACK);
		Launcher.getRoot().getChildren().add(r);
		gamestate.update(input);
		DisplayText.wave(spawner.getWave());
	}

	/**
	 * handles the update logic of the game itself.
	 *
	 * @param input - all keys pressed at the time of update
	 */
	public void updateGame(final List<String> input) {
		entities.forEach(e -> {
			e.update(input);
			checkCollision(e);
			e.draw();
		});
		
		if (gamestate.isArcade()) {
			spawner.updateArcade();
		} else {
			spawner.updateSurvival();
		}
		
		destroyList.forEach(AbstractEntity::onDeath);
		entities.removeAll(destroyList);
		entities.addAll(createList);
		createList.clear();
		destroyList.clear();
		createList.clear();
		scorecounter.displayScore();
		if (gamestate.isCoop()) {
			if (playerTwo == null) {
				return;
			}
			DisplayHud.lives(playerTwo.getLives(), playerTwo.isPlayerTwo());
		}
		if (player == null) {
			return;
		}
		DisplayHud.lives(player.getLives(), player.isPlayerTwo());
		
	}

	/**
	 * checks all collisions of an entity, if there is a hit then collide of the
	 * entity class will be run.
	 *
	 * @param e1 - the entity
	 */
	public void checkCollision(final AbstractEntity e1) {
		entities.stream()
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
	 * @param e - the Entity
	 */
	public void destroy(final AbstractEntity e) {
		destroyList.add(e);
	}

	/**
	 * adds an Entity to the createList, and will be added to the game at the
	 * and of the current tick.
	 *
	 * @param e - the Entity
	 */
	public void create(final AbstractEntity e) {
		createList.add(e);
	}

	/**
	 * Game over function, destroys the player.
	 */
	public void over() {
		if (player == null) {
			return;
		}
		if (player.isAlive()) {
			destroy(playerTwo);
			return;
		} else if (gamestate.isCoop() && playerTwo.isAlive()) {
			destroy(player);
			return;
		}
		destroy(player);

		long score;
		long highscore;
		
		if (gamestate.isArcade()) {
			score = arcadeScore;
			highscore = arcadeHighscore;
		} else {
			score = survivalScore;
			highscore = survivalHighscore;
		}
		if (gamestate.isCoop()) {
			destroy(playerTwo);
		}
		Logger.getInstance().log("Game over.");
		if (scorecounter.isNotHighscore()) {
			gamestate.setState(Gamestate.getStateStartScreen());
		} else {
			scorecounter.updateHighscore();
			Logger.getInstance().log("New highscore is " + scorecounter.getHighscore() + ".");
			gamestate.setState(Gamestate.getStateHighscoreScreen());
		}
	}

	/**
	 * Adds score to this.score.
	 *
	 * @param score - the score to be added.
	 */
	public void addScore(final int score) {
		long currentScore = getScore();
		if (player == null) {
			scorecounter.addScore(score);
			return;
		}
		if (player.isAlive() || gamestate.isCoop() && playerTwo.isAlive()) {
			if (LOG_SCORE) {
				Logger.getInstance().log(score + " points gained.");
			}
			extraLife(score);
			scorecounter.addScore(score);
		}
	}

	/**
	 * handles the gaining of extra lives.
	 * @param score - the score that will be added
	 */
	private void extraLife(final int score) {
		if (scorecounter.canGainLife(score)) {
			player.gainLife();
			if (gamestate.isCoop()) {
				playerTwo.gainLife();
				Logger.getInstance().log("Player 2 gained an extra life.");
			}
			Logger.getInstance().log(player.getPlayerString() + " gained an extra life.");
		}
	}

	/**
	 * Amount of bullets currently in game for a specific player.
	 *
	 * @param player the player whose bullets to count
	 * @return amount of bullets
	 */
	public int bullets(final Player player) {
		return Math.toIntExact(entities.stream()
				.filter(e -> e instanceof Bullet)
				.map(e -> (Bullet) e)
				.filter(Bullet::isFriendly)
				.filter(bullet -> bullet.getPlayer().equals(player))
				.count());
	}

	/**
	 * Amount of enemies currently in game.
	 *
	 * @return amount of enemies
	 */
	public int enemies() {
		return Math.toIntExact(entities.stream()
				.filter(e -> e instanceof Asteroid || e instanceof Saucer)
				.count());
	}
	
	/**
	 * Amount of big enemies, where 2 medium asteroids count as 1 big
	 * big asteroid, and 2 small asteroids count as 1 medium asteroid.
	 * @return amount of converted big enemies
	 */
	public int convertedBigEnemies() {
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
	 *
	 * @return canvas size
	 */
	public static float getCanvasSize() {
		return CANVAS_SIZE;
	}

	/**
	 * getter for screenX.
	 *
	 * @return - screenX
	 */
	public float getScreenX() {
		return screenX;
	}

	/**
	 * getter for screenY.
	 *
	 * @return - screenY
	 */
	public float getScreenY() {
		return screenY;
	}

	/**
<<<<<<< HEAD
	 * Score getter.
	 * @return score
	 */
	public long getScore() {
		if (gamestate.isArcade()) {
			return arcadeScore;
		} else {
			return survivalHighscore;
		}
	}

	/**
=======
>>>>>>> master
	 * Player getter.
	 *
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(final Player player) {
		this.player = player;
	}

	/**
	 * @return the playerTwo
	 */
	public Optional<Player> getPlayerTwo() {
		return Optional.of(playerTwo);
	}

	/**
	 * @return the arcade highscore
	 */
	public long getArcadeHighscore() {
		return arcadeHighscore;
	}
	
	/**
	 * @return the survival highscore
	 */
	public long getSurvivalHighscore() {
		return survivalHighscore;
	}
	
	/**
	 * @param playerTwo - a new player two.
	 */
	public void setPlayerTwo(final Player playerTwo) {
		this.playerTwo = playerTwo;
	}

	/**
	 * @return the destroyList
	 */
	public List<AbstractEntity> getDestroyList() {
		return destroyList;
	}

	/**
	 * @param destroyList the destroyList to set
	 */
	public void setDestroyList(final List<AbstractEntity> destroyList) {
		this.destroyList = destroyList;
	}

	/**
	 * @return the createList
	 */
	public List<AbstractEntity> getCreateList() {
		return createList;
	}

	/**
	 * @param createList the createList to set
	 */
	public void setCreateList(final List<AbstractEntity> createList) {
		this.createList = createList;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(final List<AbstractEntity> entities) {
		this.entities = entities;
	}

	/**
	 * @return the entities
	 */
	public List<AbstractEntity> getEntities() {
		return entities;
	}	
	
	/**
	 * @return the gamestate
	 */
	public Gamestate getGamestate() {
		return gamestate;
	}

	/**
	 * @return the spawner
	 */
	public Spawner getSpawner() {
		return spawner;
	}
	
	/**
	 * @return the scorecounter
	 */
	public ScoreCounter getScoreCounter() {
		return scorecounter;
	}
}