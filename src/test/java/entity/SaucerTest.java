package entity;

import display.DisplayEntity;
import entity.builders.BulletBuilder;
import entity.builders.PlayerBuilder;
import game.Game;
import game.Launcher;
import javafx.scene.Group;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Tests for Saucer.
 * @author Kibo
 *
 */
public class SaucerTest {
	private static final float X_START = 1;
	private static final float Y_START = 2;
	private static final float DX_START = 3;
	private static final float DY_START = 4;

	private Game thisGame;
	private Saucer saucer;
	private PlayerBuilder pBuilder;
	private BulletBuilder bBuilder;

	@Before
	public final void setUp() {
		thisGame = new Game();
		thisGame.setPlayer(null);
		thisGame.getScorecounter().setScore(0);
		thisGame.setCreateList(new ArrayList<>());
		thisGame.setDestroyList(new ArrayList<>());
		Launcher.getRoot().getChildren().clear();
		saucer = new Saucer(X_START, Y_START, DX_START, DY_START, thisGame);
		saucer.setRadius(Saucer.getBigRadius());
		
		pBuilder = new PlayerBuilder();
		pBuilder.setX(X_START);
		pBuilder.setY(Y_START);
		pBuilder.setDX(DX_START);
		pBuilder.setDY(DY_START);
		pBuilder.setThisGame(thisGame);
		pBuilder.setPlayerTwo(false);
		
		bBuilder = new BulletBuilder();
		bBuilder.setX(X_START);
		bBuilder.setY(Y_START);
		bBuilder.setDX(DX_START);
		bBuilder.setDY(DY_START);
		bBuilder.setThisGame(thisGame);
		thisGame.getAudio().setMute(true);
	}
	
	@Test
	public final void testConstructor1(){
		assertNotSame(saucer, null);
		assertEquals(X_START, saucer.getX(), 0);
		assertEquals(Y_START, saucer.getY(), 0);
	}

	@Test
	public final void testConstructor2(){
		final Saucer saucer2 = new Saucer(Game.getCanvasSize() - 1, Game.getCanvasSize() - 1, 0, 0, thisGame);
		assertEquals(Game.getCanvasSize() - 1, saucer2.getX(), 0);
		assertEquals(1, saucer2.getToRight());
	}
	
	@Test
	public final void testSetPath(){
		saucer.setPath(1);
		assertEquals(0, saucer.getDY(), 0);
	}
	
	@Test
	public final void testUpdate(){
		saucer.setPath(1);
		saucer.update(null);
		assertEquals(X_START + 2, saucer.getX(), 0);
	}
	
	@Test
	public final void testCheckEnd1(){
		saucer.setX(Game.getCanvasSize() + 10);
		saucer.update(null);
		assertTrue(thisGame.getDestroyList().contains(saucer));
	}
	
	@Test
	public final void testCheckEnd2(){
		saucer.setX(-10);
		saucer.update(null);
		assertTrue(thisGame.getDestroyList().contains(saucer));
	}
	
	@Test
	public final void testChangeDirection(){
		saucer.setDirChangeTime(0);
		saucer.update(null);
		assertEquals(System.currentTimeMillis(), saucer.getDirChangeTime(), 0);
	}
	
	@Test
	public final void testDraw(){
		saucer.draw();
		final int strokesInGroup = ((Group)Launcher.getRoot().getChildren().get(0)).getChildren().size();
		final int strokesInShape = DisplayEntity.getSaucerShape().length;
		assertEquals(strokesInShape, strokesInGroup, 0);
	}
	
	@Test
	public final void testCollide(){
		final Player p = (Player) pBuilder.getResult();
		p.setInvincibleStart(0);
		saucer.collide(p);
		assertTrue(thisGame.getDestroyList().contains(saucer));
	}
	
	@Test
	public final void testCollide2(){
		final Player p = (Player) pBuilder.getResult();
		saucer.collide(p);
		assertFalse(thisGame.getDestroyList().contains(saucer));
	}
	
	@Test
	public final void testCollide3(){
		final Bullet b = (Bullet) bBuilder.getResult();
		saucer.collide(b);
		assertEquals(2, thisGame.getDestroyList().size(), 0);
	}
	
	@Test
	public final void testCollide4(){
		final Bullet b = (Bullet) bBuilder.getResult();
		b.setFriendly(false);
		saucer.collide(b);
		assertFalse(thisGame.getDestroyList().contains(saucer));
	}
	
	@Test
	public final void testCollide5(){
		final Asteroid a = new Asteroid(X_START, Y_START, DX_START, DY_START, thisGame);
		saucer.collide(a);
		assertEquals(2, thisGame.getDestroyList().size(), 0);
	}
	
	@Test
	public final void testOnDeath(){
		saucer.setRadius(Saucer.getSmallRadius());
		saucer.onDeath();
		assertEquals(Saucer.getSmallScore(), thisGame.getScorecounter().getScore(), 0);
	}
	
	@Test
	public final void testOnDeath2(){
		saucer.onDeath();
		assertEquals(Saucer.getBigScore(), thisGame.getScorecounter().getScore(), 0);
	}
}
