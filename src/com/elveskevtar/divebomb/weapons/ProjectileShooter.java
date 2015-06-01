package com.elveskevtar.divebomb.weapons;

import java.util.ArrayList;

import com.elveskevtar.divebomb.race.Player;

public abstract class ProjectileShooter extends Weapon {

	private static String[] bowFiles = { "res/img/bow" };

	public static enum ProjectileShooterTypes {
		BOW(bowFiles, "bow");

		private String[] files;
		private String name;

		ProjectileShooterTypes(String[] files, String name) {
			this.setFiles(files);
			this.setName(name);
		}

		public String[] getFiles() {
			return files;
		}

		public void setFiles(String[] files) {
			this.files = files;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private double rAngle;
	private int projectileSpawnRightX;
	private int projectileSpawnRightY;
	private int projectileSpawnLeftX;
	private int projectileSpawnLeftY;

	public ProjectileShooter(Player p) {
		super(p);
	}

	@Override
	public void attack(ArrayList<Player> players, boolean server) {
		if (getPlayer().getGame().getSocketClient() == null || server == true) {
			addProjectile();
		} else if (getPlayer().getName().equalsIgnoreCase(
				getPlayer().getGame().getUserName())
				&& !server) {
			addProjectileToServer();
		}
	}

	public abstract void addProjectile();

	public abstract void addProjectileToServer();

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

	public double getrAngle() {
		return rAngle;
	}

	public void setrAngle(double rAngle) {
		this.rAngle = rAngle;
	}
}
