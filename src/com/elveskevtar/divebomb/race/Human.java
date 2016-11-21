package com.elveskevtar.divebomb.race;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.imageio.ImageIO;

import com.elveskevtar.divebomb.gfx.Game;

public class Human extends Player {

	public Human(Game game, InetAddress ip, int port) {
		super(game, ip, port);
		try {
			this.setPlayerSpriteSheet(ImageIO.read(new File("res/img/human_male.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setPlayerSprite(getPlayerSpriteSheet().getSubimage(getSpriteX() * 32, getSpriteY() * 32, getSpriteWidth(),
				getSpriteHeight()));
		this.setHeight(40);
		this.setStandingLeftHandX(14);
		this.setStandingLeftHandY(35);
		this.setWalkingLeftHandX(11);
		this.setWalkingLeftHandY(33);
		this.setStandingRightHandX(18);
		this.setStandingRightHandY(35);
		this.setWalkingRightHandX(22);
		this.setWalkingRightHandY(33);
		this.setWeaponXTweak(getStandingLeftHandX());
		this.setWeaponYTweak(getStandingLeftHandY());
	}

	public Human(Game game, String name, InetAddress ip, int port) {
		super(game, name, ip, port);
		try {
			this.setPlayerSpriteSheet(ImageIO.read(new File("res/img/human_male.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setPlayerSprite(getPlayerSpriteSheet().getSubimage(getSpriteX() * 32, getSpriteY() * 32, getSpriteWidth(),
				getSpriteHeight()));
		this.setHeight(40);
		this.setStandingLeftHandX(14);
		this.setStandingLeftHandY(35);
		this.setWalkingLeftHandX(11);
		this.setWalkingLeftHandY(33);
		this.setStandingRightHandX(18);
		this.setStandingRightHandY(35);
		this.setWalkingRightHandX(22);
		this.setWalkingRightHandY(33);
		this.setWeaponXTweak(getStandingLeftHandX());
		this.setWeaponYTweak(getStandingLeftHandY());
	}

	@Override
	public boolean canFall() {
		for (Rectangle r : getGame().getCollisionRecs()) {
			if (new Rectangle(getBounds().x + 10, getBounds().y + 16, getBounds().width - 20, getBounds().height - 14)
					.intersects(r))
				return false;
		}
		return true;
	}
}
