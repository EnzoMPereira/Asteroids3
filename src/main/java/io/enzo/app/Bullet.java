package io.enzo.app;

import java.util.ArrayList;

import io.enzo.extras.Movable;
import processing.core.PVector;

class Bullet extends Movable {
	private float dist = 0;

	public Bullet(PVector pos, PVector vel) {
		this.pos = pos.copy();

		this.velocity = vel.copy();
	}

	public boolean update(AsteroidsMain a) {
		move(a);

		dist += velocity.mag();

		return dist > a.width;
	}

	public void show(AsteroidsMain a) {
		a.fill(255, 100);
		a.noStroke();
		a.rect(pos.x - 1, pos.y - 1, 2, 2);
	}

	public int collide(ArrayList<Asteroid> u, AsteroidsMain a) {
		for (int i = 0; i < u.size(); i++) {
			if (u.get(i).isInside(this.pos, a))
				return i;
		}

		return -1;
	}
}
