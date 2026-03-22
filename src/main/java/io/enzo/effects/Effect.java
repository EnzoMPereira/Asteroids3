package io.enzo.effects;

import io.enzo.app.Asteroid;
import io.enzo.app.AsteroidsMain;
import io.enzo.extras.Updatable;
import processing.core.PVector;

public class Effect implements Updatable {
	public float maxRad = 50;

	private PVector origin;
	private PVector points[];
	private PVector velocities[];

	public Effect(PVector origin, AsteroidsMain a) {
		this.origin = origin.copy();

		this.points = new PVector[(int) AsteroidsMain.round(a.random(20))];
		this.velocities = new PVector[points.length];

		for (int i = 0; i < points.length; i++) {
			points[i] = origin.copy();
			velocities[i] = PVector.random2D().setMag(a.random(0.25f, 0.5f));
		}
	}

	public Effect(PVector origin, PVector velocity, AsteroidsMain a) {
		this.origin = origin.copy();

		this.points = new PVector[(int) AsteroidsMain.round(a.random(20))];
		this.velocities = new PVector[points.length];

		for (int i = 0; i < points.length; i++) {
			points[i] = origin.copy();
			velocities[i] = PVector.add(PVector.random2D().setMag(a.random(0.25f, 0.5f)), velocity.normalize());
		}
	}

	public Effect(Asteroid as, AsteroidsMain a) {
		maxRad = AsteroidsMain.map(as.getRadius(), Asteroid.minRad, Asteroid.maxRad, 50, 100);

		this.origin = as.getPos().copy();

		this.points = new PVector[(int) AsteroidsMain.round(a.random(20))];
		this.velocities = new PVector[points.length];

		for (int i = 0; i < points.length; i++) {
			points[i] = origin.copy();
			velocities[i] = PVector.add(PVector.random2D().setMag(a.random(0.25f, maxRad/100.0f)), as.getVelocity().normalize());
		}
	}

	@Override
	public void update(AsteroidsMain a) {
		for (int i = 0; i < points.length; i++) {
			points[i].add(velocities[i]);
		}
	}

	@Override
	public void show(AsteroidsMain a) {
		a.fill(255, 150);
		a.noStroke();
		for (PVector p: points) {
			a.rect(p.x, p.y, 2, 2);
		}
	}

	public boolean delete() {
		float avg = 0;

		for (int i = 0; i < points.length; i++) {
			avg += PVector.dist(origin, points[i]);
		}

		return (avg/points.length)>=maxRad;
	}
}
