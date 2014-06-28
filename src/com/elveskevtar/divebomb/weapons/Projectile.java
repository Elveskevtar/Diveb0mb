package com.elveskevtar.divebomb.weapons;

import java.util.ArrayList;

import com.elveskevtar.divebomb.race.Player;

public class Projectile extends Weapon {

	private int id;
	private double xPosition;
	private double yPosition;
	private double airTime;
	private double startingVelocity;
	private double velox;
	private double veloy;
	private double rAngle;

	public Projectile(Player p, int id, double xPosition, double yPosition,
			double startingVelocity, double rAngle) {
		super(p);
		this.id = id;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.startingVelocity = startingVelocity;
		this.rAngle = rAngle;
		this.airTime = 0.05;
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

	public double getrAngle() {
		return rAngle;
	}

	public void setrAngle(double rAngle) {
		this.rAngle = rAngle;
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

	public double getAirTime() {
		return airTime;
	}

	public void setAirTime(double airTime) {
		this.airTime = airTime;
	}
}
