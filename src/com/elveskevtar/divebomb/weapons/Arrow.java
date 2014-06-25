package com.elveskevtar.divebomb.weapons;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.elveskevtar.divebomb.race.Player;

public class Arrow extends Projectile {

	public Arrow(Player p, int id, double xPosition, double yPosition, double rAngle) {
		super(p, id, xPosition, yPosition, 6, rAngle);
		this.setDamage(10);
		try {
			this.setImage(ImageIO.read(new File("res/img/arrow.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
