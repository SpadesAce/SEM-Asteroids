import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Class that stores the information for a bullet.
 */
public class Bullet extends Entity {

	public Bullet(float X, float Y, float dX, float dY, Game thisGame) {
		super(X, Y, dX, dY, thisGame);
		radius = 5;
	}

	public void update(ArrayList<String> input) {
		X = X + dX;
		Y = Y + dY;
		wrapAround();
	}

	@Override
	public void collide(Entity e2) {
		if (e2 instanceof Asteroid) {
			thisGame.destroy(this);
			((Asteroid) e2).split();
		}
	}

	@Override
	public void draw(GraphicsContext gc) {
		gc.setFill(Color.WHITE);
		gc.fillOval(X - radius / 2, Y - radius / 2, radius * 2, radius * 2);
	}
}