package io.enzo.app;

import java.util.ArrayList;
import java.util.List;


import io.enzo.extras.Movable;
import io.enzo.extras.MyMath;
import io.enzo.extras.Updatable;
import io.enzo.gui.CircleButton;
import processing.core.PVector;

public class Asteroid extends Movable implements Updatable {
	public static float minSpeed = 2;
	public static float maxSpeed = 5;
	public static final float maxRad = 100;
	public static final float minRad = 10;
	private static final float noiseMultiplier = 150;

	private PVector vertices[] = new PVector[10];
	private float radius;

	public Asteroid(float x, float y, AsteroidsMain a) {
		pos = new PVector(x, y);

		radius = a.random(minRad, maxRad);

		velocity = PVector.random2D();
		velocity.setMag(AsteroidsMain.map(radius, minRad, maxRad, maxSpeed, minSpeed));

		float angles[] = generateAngles(vertices.length, a);
		float myNoiseMult = AsteroidsMain.map(radius, minRad, maxRad, 0.5f, noiseMultiplier);
		for (int i = 0; i < vertices.length; i++) {
			float rad = MyMath.sign(a.random(-1, 1)) * a.noise(pos.x, pos.y, angles[i]) * myNoiseMult / 2
					+ radius;

			float x2 = rad * AsteroidsMain.cos(angles[i]);
			float y2 = rad * AsteroidsMain.sin(angles[i]);

			vertices[i] = new PVector(x2, y2);
		}
	}

	public Asteroid(Asteroid other, AsteroidsMain a) {
		pos = other.getPos();

		radius = a.random(minRad, other.getRadius() / 2);

		velocity = PVector.add(other.getVelocity(), PVector.random2D());
		velocity.setMag(AsteroidsMain.map(radius, minRad, maxRad, maxSpeed, minSpeed));

		float angles[] = generateAngles(vertices.length, a);
		float myNoiseMult = AsteroidsMain.map(radius, minRad, maxRad, 0.5f, noiseMultiplier);
		for (int i = 0; i < vertices.length; i++) {
			float rad = MyMath.sign(a.random(-1, 1)) * a.noise(pos.x, pos.y, angles[i]) * myNoiseMult / 2
					+ radius;

			float x = rad * AsteroidsMain.cos(angles[i]);
			float y = rad * AsteroidsMain.sin(angles[i]);

			vertices[i] = new PVector(x, y);
			;
		}
	}

	public Asteroid(PVector velocity, Asteroid other, AsteroidsMain a) {
		pos = other.getPos();

		radius = a.random(minRad, other.getRadius() / 2);

		this.velocity = PVector.add(PVector.mult(PVector.random2D(), 0.8f),
				PVector.mult(PVector.add(other.getVelocity(), velocity), 0.2f));
		this.velocity.setMag(AsteroidsMain.map(radius, minRad, maxRad, maxSpeed - 1, minSpeed - 1) + a.random(-1, 1));

		float angles[] = generateAngles(vertices.length, a);
		float myNoiseMult = AsteroidsMain.map(radius, minRad, maxRad, 0.5f, noiseMultiplier);
		for (int i = 0; i < vertices.length; i++) {
			float rad = MyMath.sign(a.random(-1, 1)) * a.noise(pos.x, pos.y, angles[i]) * myNoiseMult / 2
					+ radius;

			float x = rad * AsteroidsMain.cos(angles[i]);
			float y = rad * AsteroidsMain.sin(angles[i]);

			vertices[i] = new PVector(x, y);
		}
	}

	public void update(AsteroidsMain a) {
		move(a);
	}

	public void show(AsteroidsMain a) {
		a.pushMatrix();
		a.translate(pos.x, pos.y);
		a.beginShape();
		for (PVector v : vertices)
			a.vertex(v.x, v.y);
		a.endShape(AsteroidsMain.CLOSE);
		a.popMatrix();
	}

	public List<Asteroid> boom(AsteroidsMain a) {
		if (this.getRadius() <= minRad)
			return new ArrayList<Asteroid>();

		ArrayList<Asteroid> as = new ArrayList<Asteroid>();

		as.add(new Asteroid(this, a));
		as.add(new Asteroid(this, a));

		return as;
	}

	public List<Asteroid> boom(Bullet b, AsteroidsMain a) {
		if (this.getRadius() / 2 <= minRad)
			return new ArrayList<Asteroid>();

		ArrayList<Asteroid> as = new ArrayList<Asteroid>();

		as.add(new Asteroid(b.getVelocity(), this, a));
		as.add(new Asteroid(b.getVelocity(), this, a));

		return as;
	}

	private float[] generateAngles(int n, AsteroidsMain a) {
		float angles[] = new float[n];
		angles[0] = a.random(-AsteroidsMain.PI / n, AsteroidsMain.PI / n);

		for (int i = 0; i < n; i++)
			angles[i] = i * AsteroidsMain.TWO_PI / n
					+ a.random(-0.8f * AsteroidsMain.PI / n, 0.8f * AsteroidsMain.PI / n);

		return angles;
	}

	// Returns true if the point p lies inside the vertices[] with n vertices 
	public boolean isInside(PVector p, AsteroidsMain a) {
		int n = vertices.length;
		PVector vert[] = new PVector[n];

		for (int i = 0; i < n; i++)
			vert[i] = PVector.add(vertices[i], pos);

		// There must be at least 3 vertices in vertices[] 
		if (n < 3)
			return false;

		// Create a point for line segment from p to infinite 
		PVector extreme = new PVector(a.width, p.y);

		// Count intersections of the above line with sides of vertices 
		int count = 0, i = 0;
		do {
			int next = (i + 1) % n;

			// Check if the line segment from 'p' to 'extreme' intersects 
			// with the line segment from 'vertices[i]' to 'vertices[next]' 
			if (MyMath.doIntersect(vert[i], vert[next], p, extreme)) {
				// If the point 'p' is colinear with line segment 'i-next', 
				// then check if it lies on segment. If it lies, return true, 
				// otherwise false 
				if (MyMath.orientation(vert[i], p, vert[next]) == 0)
					return MyMath.onSegment(vert[i], p, vert[next]);

				count++;
			}

			i = next;
		} while (i != 0);

		// Return true if count is odd, false otherwise 
		return count % 2 == 1;
	}

	public float getRadius() {
		return this.radius;
	}

    public PVector getPosition() {
        return pos;
    }
}
