package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet05Health extends Packet {

	private String name;

	private double health;

	public Packet05Health(byte[] data) {
		super(05);
		String[] dataArray = readData(data).split(",");
		this.setName(dataArray[0]);
		this.setHealth(Double.parseDouble(dataArray[1]));
	}

	public Packet05Health(String name, double health) {
		super(05);
		this.setName(name);
		this.setHealth(health);
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
		return ("05" + name + "," + health).getBytes();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

}