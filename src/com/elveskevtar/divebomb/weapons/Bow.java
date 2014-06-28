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
		this.setrAngle(0);
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
		if (getPlayer().isFacingRight())
			getPlayer()
					.getGame()
					.getProjectiles()
					.add(new Arrow(getPlayer(), id, Math.cos(getrAngle())
							* getHoldingRightX() + getPlayer().getxPosition()
							- 16, Math.sin(getrAngle()) * getHoldingRightX()
							+ getPlayer().getyPosition() - 32, getrAngle()));
		else
			getPlayer()
					.getGame()
					.getProjectiles()
					.add(new Arrow(getPlayer(), id, Math.cos(getrAngle())
							* getHoldingRightX() * -1
							+ getPlayer().getxPosition() - 16, Math
							.sin(getrAngle())
							* getHoldingRightX()
							* -1
							+ getPlayer().getyPosition() - 32, getrAngle()));
		getPlayer().getGame().getProjectileIDs().add(id);
	}
}
