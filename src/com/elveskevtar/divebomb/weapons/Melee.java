package com.elveskevtar.divebomb.weapons;

import com.elveskevtar.divebomb.race.Player;

public abstract class Melee extends Weapon {
	
	private int distance;

	public Melee(Player p) {
		super(p);
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
}
