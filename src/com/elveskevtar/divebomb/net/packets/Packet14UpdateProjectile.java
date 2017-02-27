package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet14UpdateProjectile extends Packet {

	private double xPosition;
	private double yPosition;
	private double rAngle;
	private int id;

	public Packet14UpdateProjectile(byte[] data) {
		super(14);
		String[] dataArray = readData(data).split(",");
		this.xPosition = Double.parseDouble(dataArray[0]);
		this.yPosition = Double.parseDouble(dataArray[1]);
		this.rAngle = Double.parseDouble(dataArray[2]);
		this.id = Integer.parseInt(dataArray[3]);
	}

	public Packet14UpdateProjectile(double xPosition, double yPosition, double rAngle, int id) {
		super(14);
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.rAngle = rAngle;
		this.id = id;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public byte[] getData() {
		return ("14" + xPosition + "," + yPosition + "," + rAngle + "," + id).getBytes();
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

	public double getrAngle() {
		return rAngle;
	}

	public void setrAngle(double rAngle) {
		this.rAngle = rAngle;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}