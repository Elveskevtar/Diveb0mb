package com.elveskevtar.divebomb.weapons;

import com.elveskevtar.divebomb.race.Player;

public class Bow extends ProjectileShooters {

	public Bow(Player p) {
		super(p);
	}

	@Override
	public void addProjectile() {
		getPlayer().getGame().getProjectiles().add(new Arrow(getPlayer()));
	}
}
