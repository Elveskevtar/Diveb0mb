package com.elveskevtar.divebomb.weapons;

import java.util.concurrent.CopyOnWriteArrayList;

import com.elveskevtar.divebomb.gfx.GameDeathmatchMP;
import com.elveskevtar.divebomb.net.packets.Packet06Kill;
import com.elveskevtar.divebomb.race.Player;

public abstract class Projectile extends Weapon {

	private int id;
	private long deadTime;
	private double xPosition;
	private double yPosition;
	private double airTime;
	private double startingVelocity;
	private double velox;
	private double veloy;
	private double rAngle;

	public Projectile(Player p, int id, double xPosition, double yPosition, double startingVelocity, double rAngle,
			String name) {
		super(p, name);
		this.id = id;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.startingVelocity = startingVelocity;
		this.rAngle = rAngle;
		this.airTime = 0.05;
	}

	@Override
	public void attack(CopyOnWriteArrayList<Player> players, boolean server) {
		CopyOnWriteArrayList<Player> attackedPlayers = players;
		for (Player p : attackedPlayers) {
			p.setHealth(p.getHealth() + (Math.random() * -10) + p.getInHand().getDefense() - getDamage());
			if (p.getHealth() <= 0) {
				if (!p.getName().equalsIgnoreCase(getPlayer().getName())) {
					getPlayer().setKills(getPlayer().getKills() + 1);
				}
				p.setDeaths(p.getDeaths() + 1);
				if (server) {
					Packet06Kill packet = new Packet06Kill(getPlayer().getName(), p.getName());
					packet.writeData(getPlayer().getGame().getSocketServer());
					if (getPlayer().getGame() instanceof GameDeathmatchMP
							&& getPlayer().getGame().getSocketClient() == null
							&& (((GameDeathmatchMP) getPlayer().getGame()).getFirstPlaceName() == null || getPlayer()
									.getKills() > ((GameDeathmatchMP) getPlayer().getGame()).getFirstPlaceKills())) {
						((GameDeathmatchMP) getPlayer().getGame()).setFirstPlaceKills(getPlayer().getKills());
						((GameDeathmatchMP) getPlayer().getGame()).setFirstPlaceName(getPlayer().getName());
					}
				}
			}
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

	public long getDeadTime() {
		return deadTime;
	}

	public void setDeadTime(long deadTime) {
		this.deadTime = deadTime;
	}
}