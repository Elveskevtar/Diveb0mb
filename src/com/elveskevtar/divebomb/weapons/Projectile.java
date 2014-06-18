package com.elveskevtar.divebomb.weapons;

import java.util.ArrayList;

import com.elveskevtar.divebomb.race.Player;

public class Projectile extends Weapon {
	
	private double xPosition;
	private double yPosition;
	private double velox;
	private double veloy;

	public Projectile(Player p) {
		super(p);
	}

	@Override
	public void attack(ArrayList<Player> players, boolean server) {
		if (getPlayer().getGame().getSocketClient() == null || server == true) {
			
		} else if (getPlayer().getName().equalsIgnoreCase(
				getPlayer().getGame().getUserName())
				&& !server) {
			
		}
	}

	public double getxPosition() {
		return xPosition;
	}

	public void setxPosition(double xPosition) {
		this.xPosition = xPosition;
	}

	public double getyPosition() {
		return yPosition;
	}

	public void setyPosition(double yPosition) {
		this.yPosition = yPosition;
	}

	public double getVelox() {
		return velox;
	}

	public void setVelox(double velox) {
		this.velox = velox;
	}

	public double getVeloy() {
		return veloy;
	}

	public void setVeloy(double veloy) {
		this.veloy = veloy;
	}
}
