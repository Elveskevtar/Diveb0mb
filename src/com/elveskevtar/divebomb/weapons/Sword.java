package com.elveskevtar.divebomb.weapons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.elveskevtar.divebomb.race.Player;

public class Sword extends Melee {

	public Sword(Player p) {
		super(p);
		this.setDamage(12);
		this.setDefense(3);
		this.setDistance(45);
		try {
			this.setImage(ImageIO.read(new File("res/img/sword.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setWidth(25);
		this.setHeight(25);
		this.setHoldingLeftX(-23);
		this.setHoldingLeftY(-21);
		this.setHoldingRightX(-3);
		this.setHoldingRightY(-21);
		this.setxAdjustment(getHoldingLeftX());
		this.setyAdjustment(getHoldingLeftY());
		this.setSprite(getImage().getSubimage(getSpriteX() * getWidth(),
				getSpriteY() * getHeight(), getWidth(), getHeight()));
	}

	@Override
	public void attack(ArrayList<Player> players) {
		ArrayList<Player> attackedPlayers = new ArrayList<Player>();
		for (Player p : players)
			if (p != getPlayer()) {
				if (getPlayer().isMovingRight()
						&& p.getxPosition() - getPlayer().getxPosition() <= 0
						&& Math.sqrt(Math.pow((p.getxPosition() - getPlayer()
								.getxPosition()), 2)
								+ Math.pow((p.getyPosition() - getPlayer()
										.getyPosition()), 2)) <= getDistance())
					attackedPlayers.add(p);
				if (!getPlayer().isMovingRight()
						&& p.getxPosition() - getPlayer().getxPosition() >= 0
						&& Math.sqrt(Math.pow((p.getxPosition() - getPlayer()
								.getxPosition()), 2)
								+ Math.pow((p.getyPosition() - getPlayer()
										.getyPosition()), 2)) <= getDistance())
					attackedPlayers.add(p);
			}
		for (Player p : attackedPlayers)
			p.setHealth(p.getHealth() + (Math.random() * -10)
					+ p.getInHand().getDefense() - getDamage());
		for (int i = 0; i < attackedPlayers.size(); i++)
			attackedPlayers.remove(i);
	}
}
