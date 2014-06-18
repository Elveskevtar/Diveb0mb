package com.elveskevtar.divebomb.weapons;

import java.util.ArrayList;

import com.elveskevtar.divebomb.net.packets.Packet04Attack;
import com.elveskevtar.divebomb.net.packets.Packet06Kill;
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

	@Override
	public void attack(ArrayList<Player> players, boolean server) {
		if (getPlayer().getGame().getSocketClient() == null || server == true) {
			ArrayList<Player> attackedPlayers = new ArrayList<Player>();
			for (Player p : players)
				if (p != getPlayer() && !p.isDead()) {
					if (getPlayer().isFacingRight()
							&& p.getxPosition() - getPlayer().getxPosition() <= 0
							&& Math.sqrt(Math.pow(
									(p.getxPosition() - getPlayer()
											.getxPosition()), 2)
									+ Math.pow((p.getyPosition() - getPlayer()
											.getyPosition()), 2)) <= getDistance())
						attackedPlayers.add(p);
					if (!getPlayer().isFacingRight()
							&& p.getxPosition() - getPlayer().getxPosition() >= 0
							&& Math.sqrt(Math.pow(
									(p.getxPosition() - getPlayer()
											.getxPosition()), 2)
									+ Math.pow((p.getyPosition() - getPlayer()
											.getyPosition()), 2)) <= getDistance())
						attackedPlayers.add(p);
				}
			for (Player p : attackedPlayers) {
				p.setHealth(p.getHealth() + (Math.random() * -10)
						+ p.getInHand().getDefense() - getDamage());
				if (p.getHealth() <= 0) {
					getPlayer().setKills(getPlayer().getKills() + 1);
					p.setDeaths(p.getDeaths() + 1);
					if (server) {
						Packet06Kill packet = new Packet06Kill(getPlayer()
								.getName(), p.getName());
						packet.writeData(getPlayer().getGame()
								.getSocketServer());
					}
				}
			}
			for (int i = 0; i < attackedPlayers.size(); i++)
				attackedPlayers.remove(i);
		} else if (getPlayer().getName().equalsIgnoreCase(
				getPlayer().getGame().getUserName())
				&& !server) {
			Packet04Attack packet = new Packet04Attack(getPlayer().getName());
			packet.writeData(getPlayer().getGame().getSocketClient());
		}
	}
}
