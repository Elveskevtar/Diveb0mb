package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet18RespawnPlayer extends Packet {

	private String name;
	private double xPosition;
	private double yPosition;

	public Packet18RespawnPlayer(byte[] data) {
		super(18);
		String[] dataArray = readData(data).split(",");
		this.name = dataArray[0];
		this.xPosition = Double.parseDouble(dataArray[1]);
		this.yPosition = Double.parseDouble(dataArray[2]);
	}

	public Packet18RespawnPlayer(String name, double xPosition, double yPosition) {
		super(18);
		this.name = name;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
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
		return ("18" + name + "," + xPosition + "," + yPosition).getBytes();
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

	public double getyPosition() {
		return yPosition;
	}

	public void setyPosition(double yPosition) {
		this.yPosition = yPosition;
	}
}