package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet13SendNewProjectile extends Packet {

	private String type;
	private String name;
	private double xPosition;
	private double yPosition;
	private double rAngle;
	private int id;

	public Packet13SendNewProjectile(byte[] data) {
		super(13);
		String[] dataArray = readData(data).split(",");
		this.type = dataArray[0];
		this.xPosition = Double.parseDouble(dataArray[1]);
		this.yPosition = Double.parseDouble(dataArray[2]);
		this.rAngle = Double.parseDouble(dataArray[3]);
		this.name = dataArray[4];
		this.id = Integer.parseInt(dataArray[5]);
	}

	public Packet13SendNewProjectile(String type, double xPosition, double yPosition, double rAngle, String name,
			int id) {
		super(13);
		this.type = type;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.rAngle = rAngle;
		this.name = name;
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
		return ("13" + type + "," + xPosition + "," + yPosition + "," + rAngle + "," + name + "," + id).getBytes();
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}