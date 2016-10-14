package entity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import game.Game;
import game.Launcher;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

/**
 * Tests for Boss
 * @author Dario
 *
 */

public class BossTest {
	private static final float X_START = 1;
	private static final float Y_START = 2;
	private static final float DX_START = 3;
	private static final float DY_START = 4;
	
	private Boss boss;
	private Game thisGame;
	
	@Before
	public void setUp() throws Exception {
		thisGame = new Game();
		thisGame.setCreateList(new ArrayList<>());
		thisGame.setDestroyList(new ArrayList<>());
		Launcher.getRoot().getChildren().clear();
		boss = new Boss(X_START, Y_START, DX_START, DY_START, thisGame);
	}

	@Test
	public final void testConstructor() {
		//Assert that the Boss was constructed.
		assertNotSame(boss, null);
		//Assert that the Boss's properties are correct.
		assertEquals(boss.getX(), X_START, 0);
		assertEquals(boss.getY(), Y_START, 0);
		//Can't test DX or DY, since they are randomly initialized internally.
	}
	
	@Test
	public void testUpdate() {
		//Test basic movement
		float DX = boss.getDX();
		float DY = boss.getDY();
		final List<String> input = new ArrayList<>(0);
		boss.update(input);
		assertEquals(boss.getX(), X_START + DX, 0);
		assertEquals(boss.getY(), Y_START + DY, 0);
	}
	
	@Test
	public void testUpdate2() {
		Boss boss2 = new Boss(-1, -1, 0, 0, thisGame);
		//Test resetting of movement values to keep inside screen.
		boss2.setDX(-2);
		boss2.setDY(-2);
		final List<String> input = new ArrayList<>(0);
		boss2.update(input);
		assertEquals(boss2.getDX(), 2.0,0);
		assertEquals(boss2.getDY(), 2.0,0);
	}

	@Test
	public void testDraw() {
		boss.draw();
		final Node c = Launcher.getRoot().getChildren().get(0);
		assertTrue(c instanceof Circle);
		assertEquals(X_START, c.getTranslateX(), 0);
	}

	@Test
	public void testCollide() {
		//Hit the boss with a bullet.
		Bullet bullet = new Bullet(X_START, Y_START, DX_START, DY_START, thisGame);
		bullet.setFriendly(true);
		boss.collide(bullet);
		//Show that the bullet is destroyed but the boss still has lives.
		assertTrue(boss.getThisGame().getDestroyList().contains(bullet));
		assertFalse(boss.getThisGame().getDestroyList().contains(boss));
		for(int i = 0; i < 9; i++) {
			boss.collide(bullet);
		}
		//Show that the boss' lives are exhausted.
		assertTrue(boss.getThisGame().getDestroyList().contains(boss));
	}

	@Test
	public void testCollide2() {
		//Hit the boss with a bullet.
		Player player = new Player(X_START, Y_START, DX_START, DY_START, thisGame, false);
		int initiallives = player.getLives();
		boss.collide(player);
		//Show that the player doesn't lose a life, since they are currently invinicible.
		assertNotEquals(initiallives - 1, player.getLives());
		assertFalse(boss.getThisGame().getDestroyList().contains(boss));
		//Make player not invincible and show that they lose a life.
		player.setInvincibleStart(0);
		boss.collide(player);
		assertEquals(initiallives - 1, player.getLives());
		assertFalse(boss.getThisGame().getDestroyList().contains(boss));
	}
	
	@Test
	public void testOnDeath() {
		double initialScore = boss.getThisGame().getScore();
		boss.onDeath();
		assertEquals(initialScore + 20000,boss.getThisGame().getScore(),0);
	}

	//Since each method calls the other, more than one test would be silly.
	@Test
	public void testSetPath() {
		int toRight = boss.getToRight();
		int pathtoset = 1;
		double predictedangle = (Math.PI * toRight) + ((pathtoset-1) * (Math.PI / 4));
		boss.setPath(pathtoset);
		assertEquals(boss.getDX(),Math.cos(predictedangle) * 2,0);
		assertEquals(boss.getDY(),-Math.sin(predictedangle) * 2,0);
	}

	@Test
	public void testCheckEdgeX() {
		Boss boss2 = new Boss(-1, -1, 0, 0, thisGame);
		boss2.setDX(-2);
		boss2.checkEdgeX();
		assertEquals(boss2.getDX(), 2.0,0);
	}

	@Test
	public void testCheckEdgeY() {
		Boss boss2 = new Boss(-1, -1, 0, 0, thisGame);
		boss2.setDY(-2);
		boss2.checkEdgeY();
		assertEquals(boss2.getDY(), 2.0,0);
	}

}
