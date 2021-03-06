package entity;

import entity.builders.BulletBuilder;
import game.Game;
import game.Logger;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract version of the Boss class to allow for extension.
 * @author Dario
 *
 */
@Setter
@Getter
public abstract class AbstractBoss extends AbstractEntity {
	private int currentLives;
	private final BulletBuilder bBuilder;
	private int bullets;

	/**
	 * Constructor for boss.
	 * @param x location of boss along x-axis
	 * @param y location of boss along y-axis
	 * @param dX speed of boss along x-axis
	 * @param dY speed of boss along y-axis
	 * @param thisGame game the boss exists in
	 */
	public AbstractBoss(final float x, final float y, final float dX, final float dY, final Game thisGame) {
		super(x, y, dX, dY, thisGame);
		
		// Initialize the Bullet Builder
		bBuilder = new BulletBuilder();
		getBBuilder().setPierce(0);
		getBBuilder().setFriendly(false);
	}

	/**
	 * Makes the Boss shoot.
	 */
	protected abstract void shoot();

	/**
	 * fire a bullet in a direction.
	 * 
	 * @param direction
	 *            - the direction
	 */
	protected abstract void fireBullet(double direction);

	@Override
	public final void collide(final AbstractEntity e2) {
		if (e2 instanceof Player && !((Player) e2).invincible()) {
			((Player) e2).onHit();
			Logger.getInstance().log("Player hit a Boss.");
		} else if (e2 instanceof Bullet && ((Bullet) e2).isFriendly()) {
			getThisGame().destroy(e2);
			setCurrentLives(getCurrentLives() - 1);
			if (getCurrentLives() < 1) {
				getThisGame().destroy(this);
			}
			Logger.getInstance().log("Boss was hit.");
		}
	}

}
