package com.elveskevtar.divebomb.weapons;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.elveskevtar.divebomb.race.Player;

public class Bow extends ProjectileShooter {

	public Bow(Player p) {
		super(p);
		this.setDefense(1);
		try {
			this.setImage(ImageIO.read(new File("res/img/bow.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setWidth(32);
		this.setHeight(32);
		this.setHoldingLeftX(-23);
		this.setHoldingLeftY(-14);
		this.setHoldingRightX(-11);
		this.setHoldingRightY(-14);
		this.setxAdjustment(getHoldingLeftX());
		this.setyAdjustment(getHoldingLeftY());
		this.setSprite(getImage().getSubimage(getSpriteX() * getWidth(),
				getSpriteY() * getHeight(), getWidth(), getHeight()));
	}

	@Override
	public void addProjectile() {
		int id = (int) (Math.random() * 10000);
		while (getPlayer().getGame().getProjectileIDs().contains(id))
			id = (int) (Math.random() * 10000);
		getPlayer()
				.getGame()
				.getProjectiles()
				.add(new Arrow(getPlayer(), id, Math.cos(getrAngle())
						* getHoldingRightX() * -1, Math.sin(getrAngle())
						* getHoldingRightX() * -1, getrAngle()));
		getPlayer().getGame().getProjectileIDs().add(id);
	}
}
