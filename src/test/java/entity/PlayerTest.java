package entity;

import display.DisplayEntity;
import entity.builders.BulletBuilder;
import entity.builders.PlayerBuilder;
import game.Game;
import game.Launcher;
import javafx.scene.Group;
import javafx.scene.shape.Polygon;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class PlayerTest {
	private static final float X_START = 1;
	private static final float Y_START = 2;
	private static final float DX_START = 3;
	private static final float DY_START = 4;
	private static final String SPACE = "SPACE";
	private Player player;
	private Player player2;
	private Game thisGame;
	private BulletBuilder bBuilder;
	private PlayerBuilder pBuilder;

	@Before
	public final void setUp() {
		thisGame = new Game();
		thisGame.setCreateList(new ArrayList<>());
		thisGame.setDestroyList(new ArrayList<>());
		thisGame.getGamestate().setCurrentMode(thisGame.getGamestate().ARCADEMODE);
		Launcher.getRoot().getChildren().clear();
		
		pBuilder = new PlayerBuilder();
		pBuilder.setX(X_START);
		pBuilder.setY(Y_START);
		pBuilder.setDX(DX_START);
		pBuilder.setDY(DY_START);
		pBuilder.setThisGame(thisGame);
		pBuilder.setPlayerTwo(false);
		player = (Player) pBuilder.getResult();
		pBuilder.setDX(DX_START + 2);
		pBuilder.setDY(DY_START + 2);
		pBuilder.setPlayerTwo(true);
		player2 = (Player) pBuilder.getResult();
		
		bBuilder = new BulletBuilder();
		bBuilder.setX(X_START);
		bBuilder.setY(Y_START);
		bBuilder.setDX(DX_START);
		bBuilder.setDY(DY_START);
		bBuilder.setThisGame(thisGame);
		thisGame.getAudio().setMute(true);
	}
	
	@Test
	public void testConstructor1() {
		assertNotSame(player, null);
		assertEquals(X_START, player.getX(), 0);
		assertEquals(Y_START, player.getY(), 0);
	}
	
	@Test
	public void testConstructor2() {
		assertEquals(DX_START, player.getDX(), 0);
		assertEquals(DY_START, player.getDY(), 0);
		assertFalse(player.isPlayerTwo());
	}

	@Test
	public void testOnDeath() {
		player.onDeath();
		assertTrue(thisGame.getDestroyList().isEmpty());
	}

	@Test
	public void testOnHit1() {
		player.onHit();
		assertEquals(2, player.getLives(), 0);
	}

	@Test
	public void testOnHit2() {
		player.setShielding(1);
		player.onHit();
		assertEquals(3, player.getLives(), 0);
		assertEquals(0, player.getShielding(), 0);
	}
	
	@Test
	public void testOnHit3() {
		player.setLives(1);
		player.onHit();
		assertEquals(0, player.getLives(), 0);
	}

	@Test
	public void testOnHit4() {
		player2.onHit();
		assertEquals(thisGame.getScreenX() / 2 + Player.getSpawnOffset(), player2.getX(), 0);
	}

	@Test
	public void testOnHit5() {
		thisGame.getGamestate().setCurrentMode(thisGame.getGamestate().COOPARCADEMODE);
		player.onHit();
		assertEquals(thisGame.getScreenX() / 2 - Player.getSpawnOffset(), player.getX(), 0);
	}
	
	@Test
	public void testGainLife1() {
		player.gainLife();
		assertEquals(4,player.getLives(),0);
	}
	
	@Test
	public void testGainLife2() {
		player.setLives(0);
		player.gainLife();
		assertEquals(1,player.getLives(),0);
		assertEquals(thisGame.getScreenX() / 2, player.getX(), 0);
	}
	
	@Test
	public void testGainLife3() {
		player2.setLives(0);
		player2.gainLife();
		assertEquals(1,player2.getLives(),0);
		assertEquals(thisGame.getScreenX() / 2 + Player.getSpawnOffset(), player2.getX(), 0);
	}
	
	@Test
	public void testGainLife4() {
		thisGame.getGamestate().setCurrentMode(thisGame.getGamestate().COOPARCADEMODE);
		player.setLives(0);
		player.gainLife();
		assertEquals(1,player.getLives(),0);
		assertEquals(thisGame.getScreenX() / 2 - Player.getSpawnOffset(), player.getX(), 0);
	}
	
	@Test
	public void testUpdate() {
		final List<String> input = new ArrayList<>();
		player.update(input);
		assertEquals(X_START+DX_START,player.getX(),0);
		assertEquals(Y_START+DY_START,player.getY(),0);
	}
	
	@Test
	public void testKeyHandler1() {
		final String[] input = {"LEFT"};
		update(player, input, false);
		assertEquals(Player.getRotationSpeed(),player.getRotation(),0);
	}

	@Test
	public void testKeyHandler2() {
		final String[] input = {"RIGHT"};
		update(player, input, false);
		assertEquals(-Player.getRotationSpeed(),player.getRotation(),0);
	}

	@Test
	public void testKeyHandler3() {
		final String[] input = {"A"};
		update(player, input, false);
		assertEquals(Player.getRotationSpeed(),player.getRotation(),0);
	}

	@Test
	public void testKeyHandler4() {
		final String[] input = {"D"};
		update(player, input, false);
		assertEquals(-Player.getRotationSpeed(),player.getRotation(),0);
	}

	@Test
	public void testKeyHandler5() {
		final String[] input = {"UP"};
		update(player, input, false);
		assertTrue(player.isBoost());
	}

	@Test
	public void testKeyHandler6() {
		final String[] input = {"W"};
		update(player, input, false);
		assertTrue(player.isBoost());
	}

	@Test
	public void testKeyHandler7() {
		final String[] input = {"DOWN"};
		update(player, input, false);
		assertTrue(0 != player.getHyperspaceStart() || player.getLives() == 2);
	}

	@Test
	public void testKeyHandler8() {
		final String[] input = {"S"};
		update(player, input, false);
		assertTrue(0 != player.getHyperspaceStart() || player.getLives() == 2);
	}
	
	@Test
	public void testKeyHandlerTwo1() {
		final String[] input = {"LEFT"};
		update(player2, input, true);
		assertEquals(Player.getRotationSpeed(),player2.getRotation(),0);
	}

	@Test
	public void testKeyHandlerTwo2() {
		final String[] input = {"RIGHT"};
		update(player2, input, true);
		assertEquals(-Player.getRotationSpeed(),player2.getRotation(),0);
	}

	@Test
	public void testKeyHandlerTwo3() {
		final String[] input = {"A"};
		update(player, input, true);
		assertEquals(Player.getRotationSpeed(),player.getRotation(),0);
	}

	@Test
	public void testKeyHandlerTwo4() {
		final String[] input = {"D"};
		update(player, input, true);
		assertEquals(-Player.getRotationSpeed(),player.getRotation(),0);
	}

	@Test
	public void testKeyHandlerTwo5() {
		final String[] input = {"UP"};
		update(player2, input, true);
		assertTrue(player2.isBoost());
	}

	@Test
	public void testKeyHandlerTwo6() {
		final String[] input = {"W"};
		update(player, input, true);
		assertTrue(player.isBoost());
	}

	@Test
	public void testKeyHandlerTwo7() {
		final String[] input = {"DOWN"};
		update(player2, input, true);
		assertTrue(0 !=  player2.getHyperspaceStart() || player2.getLives() == 2);
	}

	@Test
	public void testKeyHandlerTwo8() {
		final String[] input = {"S"};
		update(player, input, true);
		assertTrue(0 !=  player.getHyperspaceStart() || player.getLives() == 2);
	}


	
	@Test
	public void testAccelerate() {
		final String[] input = {"W"};
		player.setDX(0);
		player.setDY(0);
		update(player, input, false);
		assertNotSame(Player.getMaxSpeed(), player.speed());
	}
	
	@Test
	public void testGoHyperspace() {
		final String[] input = {"S"};
		player.setChanceOfDying(1);
		update(player, input, false);
		assertEquals(2, player.getLives(), 0);
	}
	
	@Test
	public void testCollide() {
		final AbstractEntity ae = new Asteroid(X_START, Y_START, DX_START, DY_START, thisGame);
		player.setInvincibleStart(0);
		player.collide(ae);
		assertEquals(1, thisGame.getDestroyList().size(), 0);
		assertEquals(2, player.getLives(), 0);
	}
	
	@Test
	public void testCollide2() {
		final AbstractEntity ae = new Asteroid(X_START, Y_START, DX_START, DY_START, thisGame);
		player.collide(ae);
		assertEquals(System.currentTimeMillis(), player.getInvincibleStart(), 2);
	}
	
	@Test
	public void testCollide3() {
		final AbstractEntity ae = new Asteroid(X_START, Y_START, DX_START, DY_START, thisGame);
		player.setHyperspaceStart(System.currentTimeMillis());
		player.collide(ae);
		assertEquals(System.currentTimeMillis(), player.getInvincibleStart(), 2);
	}
	
	@Test
	public void testCollide4() {
		final AbstractEntity ae = new Asteroid(X_START, Y_START, DX_START, DY_START, thisGame);
		player.setInvincibleStart(0);
		player.collide(ae);
		assertEquals(System.currentTimeMillis(), player.getInvincibleStart(), 2);
	}
	
	@Test
	public void testCollide5() {
		final AbstractEntity ae = new Asteroid(X_START, Y_START, DX_START, DY_START, thisGame);
		player.setHyperspaceStart(System.currentTimeMillis());
		player.setInvincibleStart(0);
		player.collide(ae);
		assertEquals(System.currentTimeMillis(), player.getInvincibleStart(), 2);
	}
	
	@Test
	public void testCollide6() {
		final AbstractEntity ae = bBuilder.getResult();
		player.collide(ae);
		assertEquals(3, player.getLives(), 0);
	}
	
	@Test
	public void testCollide7() {
		final AbstractEntity ae = bBuilder.getResult();
		((Bullet) ae).setFriendly(false);
		player.collide(ae);
		assertEquals(2, player.getLives(), 0);
	}
	
	@Test
	public void testCollide8() {
		pBuilder.setDX(DX_START);
		pBuilder.setDY(DY_START);
		pBuilder.setPlayerTwo(false);
		final AbstractEntity ae = pBuilder.getResult();
		player.collide(ae);
		assertEquals(3, player.getLives(), 0);
	}
	
	@Test
	public void testDraw(){
		player.draw();
		final int strokesInGroup = ((Polygon) ((Group) Launcher.getRoot().getChildren().get(0))
				.getChildren().get(0)).getPoints().size();
		final int strokesInShape = DisplayEntity.getPlayerOneLines().length;
		assertEquals(strokesInShape, strokesInGroup, 0);
	}
	
	@Test
	public void testIsAlive1(){
		assertTrue(player.isAlive());
	}
	
	@Test
	public void testIsAlive2(){
		player.setLives(0);
		assertFalse(player.isAlive());
	}
	
	@Test
	public void testFire() {
		final String[] input = {SPACE};
		player.getShooter().setTripleShot(true);
		player.getShooter().setLastShot(0);
		update(player, input, false);
		assertEquals(3, thisGame.getCreateList().size(), 0);
	}
	
	@Test
	public void testFire2() {
		final String[] input = {SPACE};
		player.getShooter().setLastShot(0);
		thisGame.getGamestate().setCurrentMode(thisGame.getGamestate().ARCADEMODE);
		update(player, input, false);
		assertEquals(1, thisGame.getCreateList().size(), 0);
	}
	
	@Test
	public void testFire3() {
		final String[] input = {SPACE};
		player.getShooter().setMaxBullets(0);
		player.getShooter().setLastShot(0);
		update(player, input, false);
		assertEquals(0, thisGame.getCreateList().size(), 0);
	}
	
	private void update(final Player player, final String[] in, final boolean coop){
		final List<String> input = new ArrayList<>();
		Collections.addAll(input, in);
		if (coop) {
			thisGame.getGamestate().setCurrentMode(thisGame.getGamestate().COOPARCADEMODE);
		}
		player.setInvincibleStart(0);
		player.update(input);
	}
}
