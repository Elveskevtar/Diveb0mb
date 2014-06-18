package com.elveskevtar.divebomb.weapons;

import java.util.ArrayList;

import com.elveskevtar.divebomb.race.Player;

public abstract class ProjectileShooters extends Weapon {
	
	private int projectileSpawnRightX;
	private int projectileSpawnRightY;
	private int projectileSpawnLeftX;
	private int projectileSpawnLeftY;

	public ProjectileShooters(Player p) {
		super(p);
	}
	
	@Override
	public void attack(ArrayList<Player> players, boolean server) {
		if (getPlayer().getGame().getSocketClient() == null || server == true) {
			addProjectile();
		} else if (getPlayer().getName().equalsIgnoreCase(
				getPlayer().getGame().getUserName())
				&& !server) {
			
		}
	}
	
	public abstract void addProjectile();

	public int getProjectileSpawnRightX() {
		return projectileSpawnRightX;
	}

	public void setProjectileSpawnRightX(int projectileSpawnRightX) {
		this.projectileSpawnRightX = projectileSpawnRightX;
	}

	public int getProjectileSpawnRightY() {
		return projectileSpawnRightY;
	}

	public void setProjectileSpawnRightY(int projectileSpawnRightY) {
		this.projectileSpawnRightY = projectileSpawnRightY;
	}

	public int getProjectileSpawnLeftX() {
		return projectileSpawnLeftX;
	}

	public void setProjectileSpawnLeftX(int projectileSpawnLeftX) {
		this.projectileSpawnLeftX = projectileSpawnLeftX;
	}

	public int getProjectileSpawnLeftY() {
		return projectileSpawnLeftY;
	}

	public void setProjectileSpawnLeftY(int projectileSpawnLeftY) {
		this.projectileSpawnLeftY = projectileSpawnLeftY;
	}
}
