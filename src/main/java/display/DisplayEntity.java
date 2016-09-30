package display;

import entity.Asteroid;
import entity.Bullet;
import entity.Particle;
import entity.Player;
import entity.Powerup;
import entity.Saucer;
import game.Launcher;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * This class displays all entities.
 * @author Kibo
 *
 */
public final class DisplayEntity {
	private static final float[][][] ASTEROID_SHAPES = {
		{
			{-2, -4, 0, -2},
			{0, -2, 2, -4},
			{2, -4, 4, -2},
			{4, -2, 3, 0},
			{3, 0, 4, 2},
			{4, 2, 1, 4},
			{1, 4, -2, 4},
			{-2, 4, -4, 2},
			{-4, 2, -4, -2},
			{-4, -2, -2, -4}
		},
		{
			{-2, -4, 0, -3},
			{0, -3, 2, -4},
			{2, -4, 4, -2},
			{4, -2, 2, -1},
			{2, -1, 4, 0},
			{4, 0, 2, 3},
			{2, 3, -1, 2},
			{-1, 2, -2, 3},
			{-2, 3, -4, 1},
			{-4, 1, -3, 0},
			{-3, 0, -4, -2},
			{-4, -2, -2, -4}
		},
		{
			{-2, -4, 1, -4},
			{1, -4, 4, -2},
			{4, -2, 4, -1},
			{4, -1, 2, 0},
			{2, 0, 4, 2},
			{4, 2, 2, 4},
			{2, 4, 1, 3},
			{1, 3, -2, 4},
			{-2, 4, -4, 1},
			{-4, 1, -4, -2},
			{-4, -2, -1, -2},
			{-1, -2, -2, -4}
		}
	};
	private static final float ASTEROID_SIZE = .25f;
	private static final float ASTEROID_WIDTH = 4;
	
	private static final float BULLET_SIZE = .5f;
	private static final float PARTICLE_SIZE = .5f;
	private static final float POWERUP_SIZE = .5f;
	
	private static final int PLAYER_RESPAWN_FLICKER_TIME = 250;
	private static final float[] PLAYER_TWO_CIRCLE = {11, 0, 9};
	private static final float[][] PLAYER_TWO_LINES = {
			{11, 0, -7,  0},
			{ -2,  0, -8, -6},
			{ -2,  0, -8, 6},
			{  0, -6, -19, -6},
			{  0, 6, -19, 6}
	};
	private static final float[][] PLAYER_TWO_BOOST = {
			{-9, 2, -9, -2},
			{-14, 2, -14, -2},
			{-19, 2, -19, -2}
	};
	private static final float PLAYER_TWO_SIZE = .5f;
	
	private static final float[][] PLAYER_ONE_LINES = {
			{10, 0, -8, 8},
			{-8, 8, -8, -8},
			{-8, -8, 10, 0}
	};
	private static final float[][] PLAYER_ONE_BOOST = {
			{-14, 0, -8, -6},
			{-14, 0, -8, 6}
	};
	private static final float PLAYER_ONE_SIZE = .5f;

	private static final double[][] SAUCER_SHAPE = {
			{1.25, -3.5, 2.5, -0.75},
			{2.5, -0.75, 5, 1},
			{5, 1, 2.5, 3},
			{2.5, 3, -2.5, 3},
			{-2.5, 3, -5, 1},
			{-5, 1, -2.5, -0.75},
			{-2.5, -0.75, -1.25, -3.5},
			{-1.25, -3.5, 1.25, -3.5},
			{2.5, -0.75, -2.5, -0.75},
			{5, 1, -5, 1}
	};
	private static final float SAUCER_SIZE = .20f;
	private static final float SAUCER_WIDTH = 4;
	
	/**
	 * private constructor for utility class.
	 */
	private DisplayEntity() {
	      //not called
	}

	/**
	 * Draw an asteroid.
	 * @param a - the asteroid
	 */
	public static void asteroid(final Asteroid a) {
		final Group group = new Group();
		for (final float[] f : ASTEROID_SHAPES[a.getShape()]) {
			final Line l = new Line(f[0] * (a.getRadius() * ASTEROID_SIZE), f[1] * (a.getRadius() * ASTEROID_SIZE), 
					f[2] * (a.getRadius() * ASTEROID_SIZE), f[1 + 2] * (a.getRadius() * ASTEROID_SIZE));
			l.setStroke(Color.WHITE);
			l.setStrokeWidth(ASTEROID_WIDTH * ASTEROID_SIZE);
			group.getChildren().add(l);
		}
		group.setTranslateX(a.getX());
		group.setTranslateY(a.getY());
		Launcher.getRoot().getChildren().add(group);
	}

	/**
	 * Display bullet on screen.
	 * @param b - the bullet
	 */
	public static void bullet(final Bullet b) {
		final Circle c = new Circle(0, 0, b.getRadius() * BULLET_SIZE);
		c.setFill(Color.WHITE);
		c.setTranslateX(b.getX());
		c.setTranslateY(b.getY());
		Launcher.getRoot().getChildren().add(c);
	}

	/**
	 * draw a particle on the screen.
	 * @param p - the particle
	 */
	public static void particle(final Particle p) {
		final float radius = p.getRadius();
		final Circle c = new Circle(0, 0, radius * PARTICLE_SIZE);
		c.setFill(Color.GREY);
		c.setTranslateX(p.getX());
		c.setTranslateY(p.getY());
		Launcher.getRoot().getChildren().add(c);
	}

	/**
	 * draw the player.
	 * @param p - the player
	 */
	public static void player(final Player p) {
		Paint color = Color.WHITE;
		if (p.invincible() && (System.currentTimeMillis() - p.getInvincibleStart()) 
				% (PLAYER_RESPAWN_FLICKER_TIME * 2) < PLAYER_RESPAWN_FLICKER_TIME) {
			color = Color.GREY;
		}
		if (p.isPlayerTwo()) {
			playerTwo(p, color);
		} else {
			playerOne(p, color);
		}
	}
	
	/**
	 * DisplayText Player two on screen.
	 * @param p - the player
	 * @param color - the color
	 * @return 
	 */
	private static void playerTwo(final Player p, final Paint color) {
		final Group group = new Group();
		for (final float[] f : PLAYER_TWO_LINES) {
			final Line l = new Line(f[0] * PLAYER_TWO_SIZE, f[1] * PLAYER_TWO_SIZE, 
					f[2] * PLAYER_TWO_SIZE, f[1 + 2] * PLAYER_TWO_SIZE);
			l.setStroke(color);
			l.setStrokeWidth(2 * PLAYER_TWO_SIZE);
			group.getChildren().add(l);
		}
		final Circle c = new Circle(PLAYER_TWO_CIRCLE[0] * PLAYER_TWO_SIZE, 
				PLAYER_TWO_CIRCLE[1] * PLAYER_TWO_SIZE, PLAYER_TWO_CIRCLE[2] * PLAYER_TWO_SIZE);
		c.setFill(color);
		group.getChildren().add(c);
		if (p.isBoost()) {
			for (final float[] f : PLAYER_TWO_BOOST) {
				final Line l = new Line(f[0] * PLAYER_TWO_SIZE, f[1] * PLAYER_TWO_SIZE, 
						f[2] * PLAYER_TWO_SIZE, f[1 + 2] * PLAYER_TWO_SIZE);
				l.setStroke(Color.WHITE);
				l.setStrokeWidth(2 * PLAYER_TWO_SIZE);
				group.getChildren().add(l);
			}
			p.setBoost(false);
		}
		group.setRotate(Math.toDegrees(-p.getRotation()));
		group.setTranslateX(p.getX());
		group.setTranslateY(p.getY());
		Launcher.getRoot().getChildren().add(group);
	}
	
	/**
	 * DisplayText Player one on screen.
	 * @param p - the player
	 * @param color - the color
	 * @return 
	 */
	private static void playerOne(final Player p, final Paint color) {
		final Group group = new Group();
		for (final float[] f : PLAYER_ONE_LINES) {
			final Line l = new Line(f[0] * PLAYER_ONE_SIZE, f[1] * PLAYER_ONE_SIZE, 
					f[2] * PLAYER_ONE_SIZE, f[1 + 2] * PLAYER_ONE_SIZE);
			l.setStroke(color);
			l.setStrokeWidth(2 * PLAYER_ONE_SIZE);
			group.getChildren().add(l);
		}
		if (p.isBoost()) {
			for (final float[] f : PLAYER_ONE_BOOST) {
				final Line l = new Line(f[0] * PLAYER_ONE_SIZE, f[1] * PLAYER_ONE_SIZE, 
						f[2] * PLAYER_ONE_SIZE, f[1 + 2] * PLAYER_ONE_SIZE);
				l.setStroke(Color.WHITE);
				l.setStrokeWidth(2 * PLAYER_ONE_SIZE);
				group.getChildren().add(l);
			}
			final Line l = new Line((-PLAYER_ONE_BOOST[0][0] + 2) * PLAYER_ONE_SIZE, 0, 
					(-PLAYER_ONE_BOOST[0][0] + 2) * PLAYER_ONE_SIZE, 1);
				//so rotation is not wrong
			group.getChildren().add(l);
			p.setBoost(false);
		}
		group.setRotate(Math.toDegrees(-p.getRotation()));
		group.setTranslateX(p.getX());
		group.setTranslateY(p.getY());
		
		Launcher.getRoot().getChildren().add(group);
	}

	/**
	 * DisplayText UFO on screen.
	 * @param s - the saucer
	 */
	public static void saucer(final Saucer s) {
		final Group group = new Group();
		for (final double[] f : SAUCER_SHAPE) {
			final Line l = new Line(f[0] * (s.getRadius() * SAUCER_SIZE), f[1] * (s.getRadius() * SAUCER_SIZE), 
					f[2] * (s.getRadius() * SAUCER_SIZE), f[1 + 2] * (s.getRadius() * SAUCER_SIZE));
			l.setStroke(Color.WHITE);
			l.setStrokeWidth(SAUCER_WIDTH * SAUCER_SIZE);
			group.getChildren().add(l);
		}
		group.setTranslateX(s.getX());
		group.setTranslateY(s.getY());
		Launcher.getRoot().getChildren().add(group);
	}
	
	/**
	 * draw powerup.
	 * @param p - the powerup
	 */
	public static void powerup(final Powerup p) {
		
		
		final float radius = p.getRadius();
		final Circle c = new Circle(0, 0, radius * POWERUP_SIZE);
		c.setFill(Color.WHITE);
		c.setTranslateX(p.getX());
		c.setTranslateY(p.getY());
		Launcher.getRoot().getChildren().add(c);
	}

	/**
	 * @return the powerupSize
	 */
	public static float getPowerupSize() {
		return POWERUP_SIZE;
	}

	/**
	 * @return the asteroidShapes
	 */
	public static float[][][] getAsteroidShapes() {
		return ASTEROID_SHAPES;
	}
}