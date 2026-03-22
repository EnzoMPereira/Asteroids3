package io.enzo.gui;


import io.enzo.app.AsteroidsMain;
import io.enzo.extras.Action;
import processing.core.PApplet;
import processing.core.PVector;

public class CircleButton {
	private float radius;
	private float x, y;
	private Action action;
	private AsteroidsMain a;

	public CircleButton(float x, float y, int radius, Action action, AsteroidsMain a) {
		this.x = x;
		this.y = y;

		this.radius = radius;

		this.action = action;
		this.a = a;
	}

	public void show(PApplet a) {
		a.ellipse(x, y, radius * 2, radius * 2);
	}

	public boolean isPressed(float x, float y) {
		return PVector.dist(new PVector(x, y), new PVector(this.x, this.y)) <= radius;
	}

	public boolean update(float x, float y) {
		if (isPressed(x, y)) {
			if (action != null)
				action.execute(a);

			return true;
		}

		return false;
	}
}


