package com.elveskevtar.divebomb.weapons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.elveskevtar.divebomb.race.Player;

public class Sword extends Melee {

	public Sword(Player p) {
		super(p, "sword");
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
		this.setSprite(
				getImage().getSubimage(getSpriteX() * getWidth(), getSpriteY() * getHeight(), getWidth(), getHeight()));
	}

	@Override
	public void attack(ArrayList<Player> players, boolean server) {
		super.attack(players, server);
		new Thread(new Animation()).start();
	}

	private class Animation extends Thread {

		@Override
		public void run() {
			setSpriteX(1);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setSpriteX(0);
		}
	}
}
