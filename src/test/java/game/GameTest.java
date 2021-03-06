package game;

import display.DisplayText;
import entity.AbstractEntity;
import entity.Asteroid;
import entity.Bullet;
import entity.Player;
import entity.Saucer;
import entity.builders.BulletBuilder;
import entity.builders.PlayerBuilder;
import javafx.scene.shape.Rectangle;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Tests for Asteroid.
 * @author Kibo
 *
 */
public class GameTest {

	private final Game thisGame = new Game();
	private final Gamestate gamestate = thisGame.getGamestate();
	private final List<String> noInput = new ArrayList<>();
	
	private PlayerBuilder pBuilder;
	private BulletBuilder bBuilder;

	@Before
	public final void setUp() {
		thisGame.getScorecounter().clearHighscores();
		gamestate.setCurrentMode(Gamestate.ARCADEMODE);
		thisGame.getScorecounter().setScore(0);
		thisGame.setEntities(new ArrayList<>());
		Launcher.getRoot().getChildren().clear();
		thisGame.setDestroyList(new ArrayList<>());
		thisGame.setCreateList(new ArrayList<>());
		thisGame.setPlayer(null);
		thisGame.setPlayerTwo(null);
		thisGame.getScorecounter().setHighscore("", 0);
		
		pBuilder = new PlayerBuilder();
		pBuilder.setX(0);
		pBuilder.setY(0);
		pBuilder.setDX(0);
		pBuilder.setDY(0);
		pBuilder.setThisGame(thisGame);
		pBuilder.setPlayerTwo(false);
		
		bBuilder = new BulletBuilder();
		bBuilder.setX(0);
		bBuilder.setY(1);
		bBuilder.setDX(0);
		bBuilder.setDY(0);
		bBuilder.setThisGame(thisGame);
		
		DisplayText.setTest(true);
		thisGame.getAudio().setMute(true);
	}
	
	@Test
	public final void testStartGame1(){
		gamestate.setCurrentMode(Gamestate.COOPARCADEMODE);
		thisGame.getScorecounter().setScore(10);
		thisGame.startGame();
		assertEquals(0, thisGame.getScorecounter().getScore(), 0);
		assertEquals(0, thisGame.getScorecounter().getHighscore(), 0);
	}
	
	@Test
	public final void testStartGame2(){
		thisGame.getScorecounter().setScore(5);
		thisGame.getScorecounter().setHighscore("", 20);
		thisGame.startGame();
		assertEquals(0, thisGame.getScorecounter().getScore(), 0);
		assertEquals(20, thisGame.getScorecounter().getHighscore(), 0);
	}
	
	@Test
	public final void testUpdate1(){
		thisGame.update(noInput);
		assertTrue(Launcher.getRoot().getChildren().get(0) instanceof Rectangle);
	}

	@Test
	public final void testUpdate2(){
		thisGame.getGamestate().setCurrentMode(Gamestate.SURVIVALMODE);
		thisGame.update(noInput);
		assertTrue(Launcher.getRoot().getChildren().get(0) instanceof Rectangle);
	}
	
	@Test
	public final void testUpdateGame1(){
		final Asteroid a = new Asteroid(0, 0, 1, 0, thisGame);
		addToEntities(a);
		thisGame.updateGame(noInput);
		assertEquals(1, a.getX(), 0);
	}
	
	@Test
	public final void testUpdateGame2(){
		gamestate.setCurrentMode(Gamestate.ARCADEMODE);
		final Player p = (Player) pBuilder.getResult();
		thisGame.setPlayer(p);
		thisGame.updateGame(noInput);
		assertTrue(Launcher.getRoot().getChildren().size() > 0);
	}
	
	@Test
	public final void testUpdateGame3(){
		gamestate.setCurrentMode(Gamestate.COOPARCADEMODE);
		final Player p = (Player) pBuilder.getResult();
		thisGame.setPlayer(p);
		thisGame.setPlayerTwo(p);
		thisGame.updateGame(noInput);
		assertTrue(Launcher.getRoot().getChildren().size() > 0);
	}

	@Test
	public final void testUpdateGame4(){
		gamestate.setCurrentMode(Gamestate.BOSSMODE);
		final Player p = (Player) pBuilder.getResult();
		thisGame.setPlayer(p);
		thisGame.setPlayerTwo(p);
		thisGame.updateGame(noInput);
		assertTrue(Launcher.getRoot().getChildren().size() > 0);
	}
	
	@Test
	public final void testUpdateGame5(){
		gamestate.setCurrentMode(Gamestate.COOPSURVIVALMODE);
		final Player p = (Player) pBuilder.getResult();
		thisGame.setPlayer(p);
		thisGame.setPlayerTwo(p);
		thisGame.updateGame(noInput);
		assertTrue(Launcher.getRoot().getChildren().size() > 0);
	}
	
	@Test
	public final void testCollision1(){
		final Asteroid e1 = new Asteroid(0, 0, 1, 0, thisGame);
		final Bullet e2 = (Bullet) bBuilder.getResult();
		addToEntities(e2);
		thisGame.checkCollision(e1);
		assertEquals(2, thisGame.getDestroyList().size(), 0);
	}
	
	@Test
	public final void testCollision2(){
		final Asteroid e1 = new Asteroid(0, 0, 1, 0, thisGame);
		final Bullet e2 = (Bullet) bBuilder.getResult();
		thisGame.destroy(e2);
		addToEntities(e2);
		thisGame.checkCollision(e1);
		assertEquals(1, thisGame.getDestroyList().size(), 0);
	}
	
	@Test
	public final void testCollision3(){
		final Asteroid e1 = new Asteroid(0, 0, 1, 0, thisGame);
		final Bullet e2 = (Bullet) bBuilder.getResult();
		thisGame.destroy(e1);
		addToEntities(e2);
		thisGame.checkCollision(e1);
		assertEquals(1, thisGame.getDestroyList().size(), 0);
	}
	
	@Test
	public final void testCollision4(){
		final Asteroid e1 = new Asteroid(0, 0, 1, 0, thisGame);
		bBuilder.setY(100);
		final Bullet e2 = (Bullet) bBuilder.getResult();
		addToEntities(e2);
		thisGame.checkCollision(e1);
		assertEquals(0, thisGame.getDestroyList().size(), 0);
	}

	private void addToEntities(final AbstractEntity a) {
		final List<AbstractEntity> entities = thisGame.getEntities();
		entities.add(a);
		thisGame.setEntities(entities);
	}

	@Test
	public final void testCreate() {
		final Asteroid a = new Asteroid(0, 0, 0, 0, thisGame);
		thisGame.create(a);
		assertTrue(thisGame.getCreateList().contains(a));
	}

	@Test
	public final void testOver1() {
		thisGame.over();
		assertEquals(0, thisGame.getDestroyList().size(), 0);
	}

	@Test
	public final void testOver2() {
		final Player p1 = (Player) pBuilder.getResult();
		p1.setLives(0);
		thisGame.setPlayer(p1);
		final Player p2 = (Player) pBuilder.getResult();
		thisGame.setPlayerTwo(p2);
		gamestate.setCurrentMode(Gamestate.COOPARCADEMODE);
		thisGame.over();
		assertTrue(thisGame.getDestroyList().contains(p1));
	}

	@Test
	public final void testOver3() {
		final Player p1 = (Player) pBuilder.getResult();
		p1.setLives(0);
		thisGame.setPlayer(p1);
		final Player p2 = (Player) pBuilder.getResult();
		p2.setLives(0);
		thisGame.setPlayerTwo(p2);
		gamestate.setCurrentMode(Gamestate.COOPARCADEMODE);
		thisGame.over();
		assertEquals(2, thisGame.getDestroyList().size(), 0);
	}

	@Test
	public final void testOver4() {
		final Player p1 = (Player) pBuilder.getResult();
		p1.setLives(0);
		thisGame.setPlayer(p1);
		final Player p2 = (Player) pBuilder.getResult();
		thisGame.setPlayerTwo(p2);
		thisGame.over();
		assertTrue(thisGame.getDestroyList().contains(p1));
	}

	@Test
	public final void testOver5() {
		final Player p1 = (Player) pBuilder.getResult();
		thisGame.setPlayer(p1);
		final Player p2 = (Player) pBuilder.getResult();
		thisGame.setPlayerTwo(p2);
		thisGame.over();
		assertTrue(thisGame.getDestroyList().contains(p2));
	}

	@Test
	public final void testOverSwitch() {
		final Player p1 = (Player) pBuilder.getResult();
		p1.setLives(0);
		thisGame.setPlayer(p1);
		final Player p2 = (Player) pBuilder.getResult();
		thisGame.setPlayerTwo(p2);
		thisGame.getScorecounter().setScore(10);
		thisGame.over();
		assertTrue(thisGame.getDestroyList().contains(p1));
	}
	
	@Test
	public final void testAddScore1() {
		thisGame.addScore(10);
		assertEquals(10, thisGame.getScorecounter().getScore(), 0);
	}

	@Test
	public final void testAddScore2() {
		final Player p1 = (Player) pBuilder.getResult();
		thisGame.setPlayer(p1);
		final Player p2 = (Player) pBuilder.getResult();
		thisGame.setPlayerTwo(p2);
		gamestate.setCurrentMode(Gamestate.COOPARCADEMODE);
		thisGame.addScore(10000);
		assertEquals(10000, thisGame.getScorecounter().getScore(), 0);
		assertEquals(4, p1.getLives(), 0);
		assertEquals(4, p2.getLives(), 0);
	}

	@Test
	public final void testAddScore3() {
		final Player p1 = (Player) pBuilder.getResult();
		thisGame.setPlayer(p1);
		thisGame.addScore(10000);
		assertEquals(10000, thisGame.getScorecounter().getScore(), 0);
		assertEquals(4, p1.getLives(), 0);
	}

	@Test
	public final void testAddScore4() {
		final Player p1 = (Player) pBuilder.getResult();
		p1.setLives(0);
		thisGame.setPlayer(p1);
		final Player p2 = (Player) pBuilder.getResult();
		p2.setLives(0);
		thisGame.setPlayerTwo(p2);
		gamestate.setCurrentMode(Gamestate.COOPARCADEMODE);
		thisGame.addScore(10000);
		assertEquals(0, thisGame.getScorecounter().getScore(), 0);
		assertEquals(0, p1.getLives(), 0);
	}

	@Test
	public final void testAddScore5() {
		final Player p1 = (Player) pBuilder.getResult();
		p1.setLives(0);
		thisGame.setPlayer(p1);
		thisGame.addScore(10000);
		assertEquals(0, thisGame.getScorecounter().getScore(), 0);
		assertEquals(0, p1.getLives(), 0);
	}

	@Test
	public final void testAddScore6() {
		final Player p1 = (Player) pBuilder.getResult();
		p1.setLives(0);
		thisGame.setPlayer(p1);
		final Player p2 = (Player) pBuilder.getResult();
		thisGame.setPlayerTwo(p2);
		gamestate.setCurrentMode(Gamestate.COOPARCADEMODE);
		thisGame.addScore(10000);
		assertEquals(10000, thisGame.getScorecounter().getScore(), 0);
		assertEquals(1, p1.getLives(), 0);
		assertEquals(4, p2.getLives(), 0);
	}
	
	@Test
	public final void testExtraLife(){
		final Player p1 = (Player) pBuilder.getResult();
		p1.setLives(0);
		thisGame.setPlayer(p1);
		final Player p2 = (Player) pBuilder.getResult();
		thisGame.setPlayerTwo(p2);
		gamestate.setCurrentMode(Gamestate.COOPARCADEMODE);
		thisGame.addScore(5000);
		assertEquals(5000, thisGame.getScorecounter().getScore(), 0);
		assertEquals(0, p1.getLives(), 0);
		assertEquals(3, p2.getLives(), 0);
	}
	
	@Test
	public final void testBullets1(){
		final Player p1 = (Player) pBuilder.getResult();
		p1.setLives(0);
		thisGame.setPlayer(p1);
		final Bullet b = (Bullet) bBuilder.getResult();
		b.setShooter(p1);
		addToEntities(b);
		assertEquals(1, thisGame.bullets(p1), 0);
	}
	
	@Test
	public final void testBullets2(){
		final Player p1 = (Player) pBuilder.getResult();
		final Player p2 = (Player) pBuilder.getResult();
		p1.setLives(0);
		thisGame.setPlayer(p1);
		final Bullet b = (Bullet) bBuilder.getResult();
		b.setShooter(p2);
		addToEntities(b);
		assertEquals(0, thisGame.bullets(p1), 0);
	}
	
	@Test
	public final void testBullets3(){
		final Player p1 = (Player) pBuilder.getResult();
		p1.setLives(0);
		thisGame.setPlayer(p1);
		final Bullet b = (Bullet) bBuilder.getResult();
		b.setFriendly(false);
		addToEntities(b);
		assertEquals(0, thisGame.bullets(p1), 0);
	}
	
	@Test
	public final void testBullets4(){
		final Player p1 = (Player) pBuilder.getResult();
		p1.setLives(0);
		thisGame.setPlayer(p1);
		final Asteroid b = new Asteroid(0, 0, 0, 0, thisGame);
		addToEntities(b);
		assertEquals(0, thisGame.bullets(p1), 0);
	}
	
	@Test
	public final void testEnemies(){
		final Bullet b = (Bullet) bBuilder.getResult();
		final Saucer s = new Saucer(0, 0, 0, 0, thisGame);
		final Asteroid a = new Asteroid(0, 0, 0, 0, thisGame);
		addToEntities(b);
		addToEntities(s);
		addToEntities(a);
		assertEquals(2, thisGame.enemies(), 0);
	}
	
	@Test
	public final void testConvertedBigEnemies1(){
		final Asteroid a = new Asteroid(0, 0, 0, 0, thisGame);
		addToEntities(a);
		final int actual = thisGame.convertedBigEnemies();
		assertEquals(1, actual, 0);
	}
	
	@Test
	public final void testConvertedBigEnemies2(){
		final Bullet b = (Bullet) bBuilder.getResult();
		addToEntities(b);
		final int actual = thisGame.convertedBigEnemies();
		assertEquals(0, actual, 0);
	}
	
	@Test
	public final void testConvertedBigEnemies3(){
		final Asteroid a = new Asteroid(0, 0, 0, 0, thisGame);
		a.setRadius(Asteroid.getMediumRadius());
		addToEntities(a);
		final int actual = thisGame.convertedBigEnemies();
		assertEquals(1, actual, 0);
	}
}
