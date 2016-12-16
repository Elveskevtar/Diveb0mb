package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet03Move extends Packet {

	private String name;
	private String weaponInHand;

	private long timeStamp;
	private double x;
	private double y;
	private double veloX;
	private double veloY;
	private double rAngle;
	private boolean isWalking;
	private boolean isRunning;
	private boolean isMovingRight;
	private boolean isFacingRight;

	public Packet03Move(byte[] data) {
		super(03);
		String[] dataArray = readData(data).split(",");
		this.setName(dataArray[0]);
		this.setX(Double.parseDouble(dataArray[1]));
		this.setY(Double.parseDouble(dataArray[2]));
		this.setVeloX(Double.parseDouble(dataArray[3]));
		this.setVeloX(Double.parseDouble(dataArray[4]));
		this.setrAngle(Double.parseDouble(dataArray[5]));
		this.setWalking(Boolean.parseBoolean(dataArray[6]));
		this.setRunning(Boolean.parseBoolean(dataArray[7]));
		this.setMovingRight(Boolean.parseBoolean(dataArray[8]));
		this.setFacingRight(Boolean.parseBoolean(dataArray[9]));
		this.setWeaponInHand(dataArray[10]);
		this.setTimeStamp(Long.parseLong(dataArray[11]));
	}

	public Packet03Move(String name, double x, double y, double veloX, double veloY, double rAngle, boolean isWalking,
			boolean isRunning, boolean isMovingRight, boolean isFacingRight, String weaponInHand) {
		super(03);
		this.name = name;
		this.x = x;
		this.y = y;
		this.rAngle = rAngle;
		this.isWalking = isWalking;
		this.isRunning = isRunning;
		this.isMovingRight = isMovingRight;
		this.isFacingRight = isFacingRight;
		this.weaponInHand = weaponInHand;
		this.timeStamp = System.nanoTime();
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(GameServer server) {

	}

	@Override
	public byte[] getData() {
		return ("03" + name + "," + x + "," + y + "," + veloX + "," + veloY + "," + rAngle + "," + isWalking + ","
				+ isRunning + "," + isMovingRight + "," + isFacingRight + "," + weaponInHand + "," + timeStamp)
						.getBytes();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public boolean isWalking() {
		return isWalking;
	}

	public void setWalking(boolean isWalking) {
		this.isWalking = isWalking;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public boolean isMovingRight() {
		return isMovingRight;
	}

	public void setMovingRight(boolean isMovingRight) {
		this.isMovingRight = isMovingRight;
	}

	public boolean isFacingRight() {
		return isFacingRight;
	}

	public void setFacingRight(boolean isFacingRight) {
		this.isFacingRight = isFacingRight;
	}

	public double getVeloX() {
		return veloX;
	}

	public void setVeloX(double veloX) {
		this.veloX = veloX;
	}

	public double getVeloY() {
		return veloY;
	}

	public void setVeloY(double veloY) {
		this.veloY = veloY;
	}

	public double getrAngle() {
		return rAngle;
	}

	public void setrAngle(double rAngle) {
		this.rAngle = rAngle;
	}

	public String getWeaponInHand() {
		return weaponInHand;
	}

	public void setWeaponInHand(String weaponInHand) {
		this.weaponInHand = weaponInHand;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}