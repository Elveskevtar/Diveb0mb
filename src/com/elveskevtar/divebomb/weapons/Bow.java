package com.elveskevtar.divebomb.weapons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.elveskevtar.divebomb.net.packets.Packet13SendNewProjectile;
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
					.add(new Arrow(getPlayer(), id, -Math.cos(getrAngle()) * 28
							+ getPlayer().getxPosition(), -Math
							.sin(getrAngle())
							* 28
							+ getPlayer().getyPosition()
							- 32, getrAngle()));
		else
			getPlayer()
					.getGame()
					.getProjectiles()
					.add(new Arrow(getPlayer(), id, Math.cos(getrAngle()) * 28
							+ getPlayer().getxPosition() - 16, Math
							.sin(getrAngle())
							* 28
							+ getPlayer().getyPosition()
							- 32, getrAngle()));
		getPlayer().getGame().getProjectileIDs().add(id);
	}

	@Override
	public void addProjectileToServer() {
		int id;
		do {
			id = (int) (Math.random() * 10000);
		} while (getPlayer().getGame().getProjectileIDs().contains(id));
		Packet13SendNewProjectile packet;
		if (getPlayer().isFacingRight())
			packet = new Packet13SendNewProjectile("arrow",
					-Math.cos(getrAngle()) * 28 + getPlayer().getxPosition(),
					-Math.sin(getrAngle()) * 28 + getPlayer().getyPosition()
							- 32, getrAngle(), getPlayer().getName(), id);
		else
			packet = new Packet13SendNewProjectile("arrow",
					Math.cos(getrAngle()) * 28 + getPlayer().getxPosition()
							- 16, Math.sin(getrAngle()) * 28
							+ getPlayer().getyPosition() - 32, getrAngle(),
					getPlayer().getName(), id);
		packet.writeData(getPlayer().getGame().getSocketClient());
	}

	@Override
	public void attack(ArrayList<Player> players, boolean server) {
		super.attack(players, server);
		new Thread(new Animation()).start();
	}

	private class Animation extends Thread {

		@Override
		public void run() {
			setSpriteX(1);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setSpriteX(0);
		}
	}
}
