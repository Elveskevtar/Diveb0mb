package com.elveskevtar.divebomb.weapons;

import java.util.ArrayList;

import com.elveskevtar.divebomb.race.Player;

public class Projectile extends Weapon {
	
	private int id;
	private double xPosition;
	private double yPosition;
	private double startingVelocity;

	public Projectile(Player p, int id, double xPosition, double yPosition, double startingVelocity) {
		super(p);
		this.id = id;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.startingVelocity = startingVelocity;
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

	public double getStartingVelocity() {
		return startingVelocity;
	}

	public void setStartingVelocity(double startingVelocity) {
		this.startingVelocity = startingVelocity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
