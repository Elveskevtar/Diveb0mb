package com.elveskevtar.divebomb.weapons;

import java.awt.image.BufferedImage;
import java.util.concurrent.CopyOnWriteArrayList;

import com.elveskevtar.divebomb.race.Player;

public abstract class Weapon {

	private BufferedImage image;
	private BufferedImage sprite;
	private String name;

	private Player player;

	private int damage;
	private int defense;
	private int staminaUsage;
	private int holdingRightX;
	private int holdingRightY;
	private int holdingLeftX;
	private int holdingLeftY;
	private int xAdjustment;
	private int yAdjustment;
	private int width;
	private int height;
	private int spriteX;
	private int spriteY;

	public abstract void attack(CopyOnWriteArrayList<Player> players, boolean server);

	public Weapon(Player p, String name) {
		this.player = p;
		this.name = name;
	}

	public Player getPlayer() {
		return player;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getStaminaUsage() {
		return staminaUsage;
	}

	public void setStaminaUsage(int staminaUsage) {
		this.staminaUsage = staminaUsage;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public int getyAdjustment() {
		return yAdjustment;
	}

	public void setyAdjustment(int yAdjustment) {
		this.yAdjustment = yAdjustment;
	}

	public int getxAdjustment() {
		return xAdjustment;
	}

	public void setxAdjustment(int xAdjustment) {
		this.xAdjustment = xAdjustment;
	}

	public BufferedImage getSprite() {
		return sprite;
	}

	public void setSprite(BufferedImage sprite) {
		this.sprite = sprite;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
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

	public int getHoldingRightX() {
		return holdingRightX;
	}

	public void setHoldingRightX(int holdingRightX) {
		this.holdingRightX = holdingRightX;
	}

	public int getHoldingRightY() {
		return holdingRightY;
	}

	public void setHoldingRightY(int holdingRightY) {
		this.holdingRightY = holdingRightY;
	}

	public int getHoldingLeftX() {
		return holdingLeftX;
	}

	public void setHoldingLeftX(int holdingLeftX) {
		this.holdingLeftX = holdingLeftX;
	}

	public int getHoldingLeftY() {
		return holdingLeftY;
	}

	public void setHoldingLeftY(int holdingLeftY) {
		this.holdingLeftY = holdingLeftY;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}