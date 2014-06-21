package com.elveskevtar.divebomb.weapons;

import com.elveskevtar.divebomb.race.Player;

public class Arrow extends Projectile {

	public Arrow(Player p, int id, double xPosition, double yPosition) {
		super(p, id, xPosition, yPosition, 6);
	}
}
