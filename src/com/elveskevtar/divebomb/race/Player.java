package com.elveskevtar.divebomb.race;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import com.elveskevtar.divebomb.gfx.Game;
import com.elveskevtar.divebomb.weapons.Weapon;

public abstract class Player {

	private static String[] humanFiles = {"res/img/human_male.png"};
	private static String[] cyborgFiles = {"res/img/cyborg_blue.png", "res/img/cyborg_purple.png"};
	private static Color[] cyborgColors = {Color.BLUE, new Color(76, 0, 153)};
	
	public static enum PlayerTypes {
		HUMAN(humanFiles, null), CYBORG(cyborgFiles, cyborgColors);

		private String[] files;
		private Color[] colors;
		
		PlayerTypes(String[] files, Color[] colors) {
			this.setFiles(files);
			this.setColors(colors);
		}

		public String[] getFiles() {
			return files;
		}

		public void setFiles(String[] files) {
			this.files = files;
		}

		public Color[] getColors() {
			return colors;
		}

		public void setColors(Color[] colors) {
			this.colors = colors;
		}
	}

	private Timer timer;
	private BufferedImage playerSpriteSheet;
	private BufferedImage playerSprite;
	private InetAddress ip;
	private Weapon inHand;
	private String name;
	private String color;
	private Game game;

	private boolean isStaminaRefilled = true;
	private boolean jumping;
	private boolean falling;
	private boolean running;
	private boolean walking;
	private boolean isFacingRight;
	private boolean isMovingRight;
	private boolean isDead;
	private double maxStamina;
	private double minStamina;
	private double stamina;
	private double health;
	private double maxHealth;
	private double initJumpSpeed;
	private double walkSpeed;
	private double runSpeed;
	private double xPosition;
	private double yPosition;
	private double veloX;
	private double veloY;
	private int walkingRightHandX;
	private int walkingRightHandY;
	private int standingRightHandX;
	private int standingRightHandY;
	private int walkingLeftHandX;
	private int walkingLeftHandY;
	private int standingLeftHandX;
	private int standingLeftHandY;
	private int spriteWidth;
	private int spriteHeight;
	private int height;
	private int weaponXTweak;
	private int weaponYTweak;
	private int spriteX;
	private int spriteY;
	private int team;
	private int level;
	private int port;
	private int kills;
	private int deaths;

	public Player(Game game, InetAddress ip, int port) {
		this.game = game;
		this.name = "Bob";
		this.ip = ip;
		this.port = port;
		this.timer = new Timer();
		try {
			this.playerSpriteSheet = ImageIO
					.read(new File("res/img/player.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setSpriteWidth(32);
		this.setSpriteHeight(64);
		this.setPlayerSprite(playerSpriteSheet.getSubimage(spriteX * 32,
				spriteY * 32, spriteWidth, spriteHeight));
		this.setMaxStamina(20);
		this.setMinStamina(5);
		this.setStamina(getMaxStamina());
		this.setMaxHealth(100);
		this.setHealth(getMaxHealth());
		this.setInitJumpSpeed(16);
		this.setWalkSpeed(3);
		this.setRunSpeed(6);
	}

	public Player(Game game, String name, InetAddress ip, int port) {
		this.game = game;
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.timer = new Timer();
		try {
			this.playerSpriteSheet = ImageIO
					.read(new File("res/img/player.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setSpriteWidth(32);
		this.setSpriteHeight(64);
		this.setPlayerSprite(playerSpriteSheet.getSubimage(0, 0, spriteWidth,
				spriteHeight));
		this.setMaxStamina(20);
		this.setMinStamina(5);
		this.setStamina(getMaxStamina());
		this.setMaxHealth(100);
		this.setHealth(getMaxHealth());
		this.setInitJumpSpeed(16);
		this.setWalkSpeed(3);
		this.setRunSpeed(6);
	}

	public void waitForStamina() {
		timer.scheduleAtFixedRate(new StaminaRegen(), 0, 16);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getxPosition() {
		return xPosition;
	}

	public void setxPosition(double xPosition) {
		this.xPosition = xPosition;
	}

	public double getVeloX() {
		return veloX;
	}

	public void setVeloX(double veloX) {
		this.veloX = veloX;
	}

	public double getyPosition() {
		return yPosition;
	}

	public void setyPosition(double yPosition) {
		this.yPosition = yPosition;
	}

	public double getVeloY() {
		return veloY;
	}

	public void setVeloY(double veloY) {
		this.veloY = veloY;
	}

	public boolean isFalling() {
		return falling;
	}

	public void setFalling(boolean falling) {
		this.falling = falling;
	}

	public Rectangle getBounds() {
		return new Rectangle((int) -getxPosition(), (int) -getyPosition(), 32,
				64);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean canFall() {
		for (Rectangle r : game.getCollisionRecs()) {
			if (new Rectangle(getBounds().x, getBounds().y + 2,
					getBounds().width, getBounds().height).intersects(r))
				return false;
		}
		return true;
	}

	/*
	 * 1 = left; 2 = right; 3 = up; 4 = down
	 */
	public ArrayList<Integer> checkCollisions() {
		ArrayList<Integer> ints = new ArrayList<Integer>();
		for (Rectangle r : game.getCollisionRecs()) {
			if (new Rectangle((int) (getBounds().x - getVeloX()),
					getBounds().y, getBounds().width, getBounds().height)
					.intersects(r))
				ints.add(1);
			if (new Rectangle((int) (getBounds().x - getVeloX()),
					getBounds().y, getBounds().width, getBounds().height)
					.intersects(r))
				ints.add(2);
			if (new Rectangle(getBounds().x,
					(int) (getBounds().y - 4 - getVeloY()), getBounds().width,
					getBounds().height).intersects(r))
				ints.add(3);
			if (new Rectangle(getBounds().x,
					(int) (getBounds().y - getVeloY()), getBounds().width,
					getBounds().height).intersects(r)) {
				ints.add(4);
			}
		}
		return ints;
	}

	public void correctInGround() {
		setyPosition(getyPosition() + 1);
	}

	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public double getWalkSpeed() {
		return walkSpeed;
	}

	public void setWalkSpeed(double walkSpeed) {
		this.walkSpeed = walkSpeed;
	}

	public double getRunSpeed() {
		return runSpeed;
	}

	public void setRunSpeed(double runSpeed) {
		this.runSpeed = runSpeed;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public double getStamina() {
		return stamina;
	}

	public void setStamina(double stamina) {
		this.stamina = stamina;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public BufferedImage getPlayerSprite() {
		return playerSprite;
	}

	public void setPlayerSprite(BufferedImage playerSprite) {
		this.playerSprite = playerSprite;
	}

	public BufferedImage getPlayerSpriteSheet() {
		return playerSpriteSheet;
	}

	public void setPlayerSpriteSheet(BufferedImage playerSpriteSheet) {
		this.playerSpriteSheet = playerSpriteSheet;
	}

	public int getSpriteWidth() {
		return spriteWidth;
	}

	public void setSpriteWidth(int spriteWidth) {
		this.spriteWidth = spriteWidth;
	}

	public int getSpriteHeight() {
		return spriteHeight;
	}

	public void setSpriteHeight(int spriteHeight) {
		this.spriteHeight = spriteHeight;
	}

	public int getSpriteX() {
		return spriteX;
	}

	public void setSpriteX(int spriteX) {
		this.spriteX = spriteX;
	}

	public int getSpriteY() {
		return spriteY;
	}

	public void setSpriteY(int spriteY) {
		this.spriteY = spriteY;
	}

	public boolean isFacingRight() {
		return isFacingRight;
	}

	public void setFacingRight(boolean isFacingRight) {
		this.isFacingRight = isFacingRight;
	}

	public boolean isMovingRight() {
		return isMovingRight;
	}

	public void setMovingRight(boolean isMovingRight) {
		this.isMovingRight = isMovingRight;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public boolean isWalking() {
		return walking;
	}

	public void setWalking(boolean walking) {
		this.walking = walking;
	}

	public double getMaxStamina() {
		return maxStamina;
	}

	public void setMaxStamina(double maxStamina) {
		this.maxStamina = maxStamina;
	}

	public double getMinStamina() {
		return minStamina;
	}

	public void setMinStamina(double minStamina) {
		this.minStamina = minStamina;
	}

	public boolean isStaminaRefilled() {
		return isStaminaRefilled;
	}

	public void setStaminaRefilled(boolean isStaminaRefilled) {
		this.isStaminaRefilled = isStaminaRefilled;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public double getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
	}

	public Weapon getInHand() {
		return inHand;
	}

	public void setInHand(Weapon inHand) {
		this.inHand = inHand;
	}

	public double getInitJumpSpeed() {
		return initJumpSpeed;
	}

	public void setInitJumpSpeed(double initJumpSpeed) {
		this.initJumpSpeed = initJumpSpeed;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeaponXTweak() {
		return weaponXTweak;
	}

	public void setWeaponXTweak(int weaponXTweak) {
		this.weaponXTweak = weaponXTweak;
	}

	public int getWeaponYTweak() {
		return weaponYTweak;
	}

	public void setWeaponYTweak(int weaponYTweak) {
		this.weaponYTweak = weaponYTweak;
	}

	public int getWalkingRightHandY() {
		return walkingRightHandY;
	}

	public void setWalkingRightHandY(int walkingRightHandY) {
		this.walkingRightHandY = walkingRightHandY;
	}

	public int getWalkingRightHandX() {
		return walkingRightHandX;
	}

	public void setWalkingRightHandX(int walkingRightHandX) {
		this.walkingRightHandX = walkingRightHandX;
	}

	public int getStandingRightHandX() {
		return standingRightHandX;
	}

	public void setStandingRightHandX(int standingRightHandX) {
		this.standingRightHandX = standingRightHandX;
	}

	public int getWalkingLeftHandX() {
		return walkingLeftHandX;
	}

	public void setWalkingLeftHandX(int walkingLeftHandX) {
		this.walkingLeftHandX = walkingLeftHandX;
	}

	public int getWalkingLeftHandY() {
		return walkingLeftHandY;
	}

	public void setWalkingLeftHandY(int walkingLeftHandY) {
		this.walkingLeftHandY = walkingLeftHandY;
	}

	public int getStandingLeftHandX() {
		return standingLeftHandX;
	}

	public void setStandingLeftHandX(int standingLeftHandX) {
		this.standingLeftHandX = standingLeftHandX;
	}

	public int getStandingLeftHandY() {
		return standingLeftHandY;
	}

	public void setStandingLeftHandY(int standingLeftHandY) {
		this.standingLeftHandY = standingLeftHandY;
	}

	public int getStandingRightHandY() {
		return standingRightHandY;
	}

	public void setStandingRightHandY(int standingRightHandY) {
		this.standingRightHandY = standingRightHandY;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetAddress getIP() {
		return ip;
	}

	public void setIP(InetAddress ip) {
		this.ip = ip;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	private class StaminaRegen extends TimerTask {

		@Override
		public void run() {
			if (stamina < minStamina) {
				setStaminaRefilled(false);
			} else {
				setStaminaRefilled(true);
				cancel();
			}
		}
	}
}
