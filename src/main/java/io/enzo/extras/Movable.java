package io.enzo.extras;

import io.enzo.app.AsteroidsMain;
import processing.core.PVector;

public abstract class Movable {
	protected PVector pos, velocity;

	protected void move(AsteroidsMain a) {
		pos.add(velocity);

		if (pos.x < 0)
			pos.x = a.width;
		else if (pos.x > a.width)
			pos.x = 0;

		if (pos.y < 0)
			pos.y = a.height;
		else if (pos.y > a.height)
			pos.y = 0;
	}

	
	public PVector getPos() {
		return this.pos.copy();
	}
	
	public PVector getVelocity() {
		return this.velocity.copy();
	}
}
