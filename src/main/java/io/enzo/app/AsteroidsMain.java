package io.enzo.app;

import java.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


import io.enzo.effects.Effect;
import io.enzo.extras.Action;
import io.enzo.extras.AudioPlayer;
import io.enzo.gui.CircleButton;
import processing.core.PApplet;
import processing.core.PVector;

public class AsteroidsMain extends PApplet {
	static Player p;

    boolean up, down, left, right;

	ArrayList<Bullet> bullets;
	ArrayList<Asteroid> asteroids;
	ArrayList<Effect> effects = new ArrayList<Effect>(); //TODO: explosion effects, shooting effects

	AudioPlayer shoot, explosion;

	CircleButton shootButton;

    CircleButton restartButton;

	long elapsedTime, startTime;
	boolean canShoot = true;

	public void settings() {
		size(displayWidth, displayHeight);

		orientation(LANDSCAPE);
		//TODO: decrease resolution
	}

	public void setup() {
		try {
			shoot = new AudioPlayer("bin/shoot.wav");
			explosion = new AudioPlayer("bin/explosion.wav");
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Action action = (AsteroidsMain a) -> a.shoot();
		shootButton = new CircleButton(width * 0.2f, height * 0.8f, 100, action, this);

        Action restartAction = (AsteroidsMain a) -> a.restartGame();

        restartButton = new CircleButton(width / 2, height / 2 + 100, 80, restartAction, this);

		bullets = new ArrayList<Bullet>();
		asteroids = new ArrayList<Asteroid>();

		for (int i = 0; i < 6; i++)
			asteroids.add(new Asteroid(random(width), random(height), this));

		p = new Player(width / 2, height / 2);

		frameRate(60);

		startTime = System.currentTimeMillis();
		elapsedTime = 0L;
	}

    void restartGame() {

        p = new Player(width / 2, height / 2);

        // limpar listas
        bullets.clear();
        asteroids.clear();
        effects.clear();


        for (int i = 0; i < 6; i++)
            asteroids.add(new Asteroid(random(width), random(height), this));


        startTime = System.currentTimeMillis();
        elapsedTime = 0;


        loop();
    }

	public void draw() {

        PVector mouse = new PVector(mouseX, mouseY);
        PVector dirToMouse = PVector.sub(mouse, p.getPosition());
        dirToMouse.normalize();

        p.setDirection(dirToMouse);

        if (mousePressed && mouseButton == LEFT) {
            shoot();
        }

		elapsedTime = (new Date()).getTime() - startTime;

		background(0);

		stroke(255);
		noFill();

		p.update(this);

        PVector dir = new PVector(0, 0);

        if (up) dir.y -= 1;
        if (down) dir.y += 1;
        if (left) dir.x -= 1;
        if (right) dir.x += 1;

        p.addForce(PVector.mult(dir, 0.5f));

         mouse = new PVector(mouseX, mouseY);
         dir = PVector.sub(mouse, p.getPosition());

        if (dir.mag() > 0) {
            dir.normalize();
            p.setDirection(dir);
        }

		p.show(this);

		for (int i = bullets.size() - 1; i >= 0; i--) {
			if (bullets.get(i).update(this)) {
				bullets.remove(i);
				continue;
			}

			int j = bullets.get(i).collide(asteroids, this);

			if (j != -1) {
				explosion.play();

				effects.add(new Effect(asteroids.get(j), this));

				asteroids.get(j).boom(bullets.get(i), this).forEach((a) -> {
					asteroids.add(a);
				});

				bullets.remove(i);

				asteroids.remove(j);

				continue;
			}

			bullets.get(i).show(this);
		}

		stroke(255);
		noFill();

        for (Asteroid a : asteroids) {
            a.update(this);
            a.show(this);

            float d = PVector.dist(a.getPosition(), p.getPosition());

            if (d < a.getRadius() + 20) {
                p.takeDamage();
            }
        }

		for (int i = effects.size() - 1; i >= 0; i--) {
			effects.get(i).update(this);

			if (effects.get(i).delete())
				effects.remove(i);
			else
				effects.get(i).show(this);
		}

        if (p.isDead()) {
            background(0);

            fill(255);
            textAlign(CENTER, CENTER);
            textSize(50);
            text("GAME OVER", width / 2, height / 2);


            noStroke();
            fill(255, 100);
            restartButton.show(this);

            fill(255);
            textSize(20);
            text("RESTART", width / 2, height / 2 + 100);


            if (mousePressed) {
                restartButton.update(mouseX, mouseY);
            }

            return;
        }

		GUI();
        drawLives();
	}

    void drawLives() {
        fill(255, 0, 0);
        noStroke();

        for (int i = 0; i < p.getLives(); i++) {
            ellipse(40 + i * 40, 40, 20, 20);
        }
    }



    public void keyPressed() {
        if (key == 'w' || key == 'W') up = true;
        if (key == 's' || key == 'S') down = true;
        if (key == 'a' || key == 'A') left = true;
        if (key == 'd' || key == 'D') right = true;
    }

    public void keyReleased() {
        if (key == 'w' || key == 'W') up = false;
        if (key == 's' || key == 'S') down = false;
        if (key == 'a' || key == 'A') left = false;
        if (key == 'd' || key == 'D') right = false;
    }

	PVector pMouse;

	public void mousePressed() {
        if (mouseButton == LEFT) {
            shoot();
        }
		if (pMouse == null)
			pMouse = new PVector(mouseX, mouseY);
	}

	public void mouseReleased() {
		pMouse = null;
		canShoot = true;
	}

	PVector dragDir = new PVector(0, 0);

	public void mouseDragged() {
		dragDir = PVector.sub(new PVector(mouseX, mouseY), pMouse);
	}

	void GUI() {
		noStroke();
		fill(255, 100);
		shootButton.show(this);

		if (mousePressed) {
			if (!shootButton.update(mouseX, mouseY)) {
				float padD = (float) (height * 0.1); //Pad diameter
				noStroke();
				fill(255, 30);
				ellipse(pMouse.x, pMouse.y, padD, padD);

				float d = min(pMouse.dist(new PVector(mouseX, mouseY)), padD / 2);
				PVector dir = PVector.add(PVector.mult(dragDir.normalize(), d), pMouse);

				stroke(0);
				fill(255, 50);
				ellipse(dir.x, dir.y, padD / 2, padD / 2);

				//p.addForce(PVector.mult(dragDir, 0.1f));
			}
		}
	}

	public void shoot() {
		if (elapsedTime > 300 && bullets.size() < 15 && canShoot) {
			bullets.add(p.shoot());
			shoot.play();
			canShoot = true;
			startTime = System.currentTimeMillis();
		}
	}


	public static void main(String[] args) {
		String[] processingArgs = { "Asteroids" };
		AsteroidsMain mySketch = new AsteroidsMain();
		PApplet.runSketch(processingArgs, mySketch);
	}
}
