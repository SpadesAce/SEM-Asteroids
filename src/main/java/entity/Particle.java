package entity;
import java.util.List;

import game.Game;
import game.Launcher;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * This class is a particle used in explosions.
 * @author Kibo
 *
 */
public class Particle extends AbstractEntity {
	private final long birthTime;
	
	private static final float SIZE = .5f;
	private static final long LIFETIME = 750;
	private static final int EXPLOSION_PARTICLES = 10;
	private static final float SPEED = .75f;
	
	/**
	 * Constructor of a particle.
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @param dX - horizontal speed
	 * @param dY - vertical speed
	 */
	public Particle(final float x, final float y, 
			final float dX, final float dY) {
		super(x, y, dX, dY);
		setRadius(1);
		birthTime = System.currentTimeMillis();
	}
	
	/**
	 * This function makes an explosion of particles.
	 * @param x - x coordinate of explosion
	 * @param y - y coordinate of explosion
	 * @param thisGame - the game this explosion is added to
	 */
	public static void explosion(final float x, final float y, 
			final Game thisGame) {
		for (int i = 0; i < EXPLOSION_PARTICLES; i++) {
			thisGame.create(randomParticle(x, y, thisGame));
		}
	}

	/**
	 * This method creates a random particle.
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @param thisGame - the game this particle is added to
	 * @return the random particle
	 */
	private static Particle randomParticle(final float x, final float y, 
			final Game thisGame) {
		return new Particle(x, y,
				(float) (Math.random() - .5) * SPEED,
				(float) (Math.random() - .5) * SPEED);
	}

	/**
	 * draw a particle on the screen.
	 */
	@Override
	public final void draw() {
		final float radius = getRadius();
		Circle c = new Circle(0, 0, radius * SIZE);
		c.setFill(Color.GREY);
		c.setTranslateX(getX());
		c.setTranslateY(getY());
		Launcher.getRoot().getChildren().add(c);
		
		/*root.setFill(Color.GREY);
		root.fillOval(getX() - radius / SIZE, 
				getY() - radius / SIZE, 
				radius * SIZE, 
				radius * SIZE);*/
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void collide(final AbstractEntity e2) {
		//no-op
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void onDeath() {
		//no-op
	}

	/**
	 * update the location of this particle, called every tick.
	 * @param input list of current key inputs this tick (not used)
	 */
	@Override
	public final void update(final List<String> input) {
		setX(getX() + getDX());
		setY(getY() + getDY());
		wrapAround();
		if (System.currentTimeMillis() - birthTime > LIFETIME) {
			Game.getInstance().destroy(this);
		}
	}
}
