package com.elveskevtar.divebomb.race;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.imageio.ImageIO;

import com.elveskevtar.divebomb.gfx.Game;

public class Cyborg extends Player {

	public Cyborg(Game game, InetAddress ip, int port) {
		super(game, ip, port);
		try {
			this.setPlayerSpriteSheet(ImageIO.read(new File("res/img/cyborg_blue.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setPlayerSprite(getPlayerSpriteSheet().getSubimage(getSpriteX() * 32, getSpriteY() * 32, getSpriteWidth(),
				getSpriteHeight()));
		this.setHeight(50);
		this.setStandingLeftHandX(9);
		this.setStandingLeftHandY(32);
		this.setWalkingLeftHandX(4);
		this.setWalkingLeftHandY(32);
		this.setStandingRightHandX(24);
		this.setStandingRightHandY(32);
		this.setWalkingRightHandX(27);
		this.setWalkingRightHandY(32);
		this.setWeaponXTweak(getStandingLeftHandX());
		this.setWeaponYTweak(getStandingLeftHandY());
	}

	public Cyborg(Game game, String color, InetAddress ip, int port) {
		super(game, ip, port);
		try {
			this.setPlayerSpriteSheet(ImageIO.read(new File("res/img/cyborg_" + color + ".png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setPlayerSprite(getPlayerSpriteSheet().getSubimage(getSpriteX() * 32, getSpriteY() * 32, getSpriteWidth(),
				getSpriteHeight()));
		this.setHeight(50);
		this.setColor(color);
		this.setStandingLeftHandX(9);
		this.setStandingLeftHandY(32);
		this.setWalkingLeftHandX(4);
		this.setWalkingLeftHandY(32);
		this.setStandingRightHandX(24);
		this.setStandingRightHandY(32);
		this.setWalkingRightHandX(27);
		this.setWalkingRightHandY(32);
		this.setWeaponXTweak(getStandingLeftHandX());
		this.setWeaponYTweak(getStandingLeftHandY());
	}

	public Cyborg(Game game, String color, String name, InetAddress ip, int port) {
		super(game, name, ip, port);
		try {
			this.setPlayerSpriteSheet(ImageIO.read(new File("res/img/cyborg_" + color + ".png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setPlayerSprite(getPlayerSpriteSheet().getSubimage(getSpriteX() * 32, getSpriteY() * 32, getSpriteWidth(),
				getSpriteHeight()));
		this.setHeight(50);
		this.setColor(color);
		this.setStandingLeftHandX(9);
		this.setStandingLeftHandY(32);
		this.setWalkingLeftHandX(4);
		this.setWalkingLeftHandY(32);
		this.setStandingRightHandX(24);
		this.setStandingRightHandY(32);
		this.setWalkingRightHandX(27);
		this.setWalkingRightHandY(32);
		this.setWeaponXTweak(getStandingLeftHandX());
		this.setWeaponYTweak(getStandingLeftHandY());
	}

	public Cyborg(Game game, String name, int ignore, InetAddress ip, int port) {
		super(game, name, ip, port);
		try {
			this.setPlayerSpriteSheet(ImageIO.read(new File("res/img/cyborg_blue.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setPlayerSprite(getPlayerSpriteSheet().getSubimage(getSpriteX() * 32, getSpriteY() * 32, getSpriteWidth(),
				getSpriteHeight()));
		this.setHeight(50);
		this.setStandingLeftHandX(9);
		this.setStandingLeftHandY(32);
		this.setWalkingLeftHandX(4);
		this.setWalkingLeftHandY(32);
		this.setStandingRightHandX(24);
		this.setStandingRightHandY(32);
		this.setWalkingRightHandX(27);
		this.setWalkingRightHandY(32);
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