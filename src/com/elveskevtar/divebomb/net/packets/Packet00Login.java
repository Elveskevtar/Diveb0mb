package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet00Login extends Packet {

	private String name;
	private String race;
	private String color;

	public Packet00Login(byte[] data) {
		super(00);
		String[] dataArray = readData(data).split(",");
		this.setName(dataArray[0]);
		this.setRace(dataArray[1]);
		this.setColor(dataArray[2]);
	}

	public Packet00Login(String name, String race, String color) {
		super(00);
		this.setName(name);
		this.setRace(race);
		this.setColor(color);
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
		return ("00" + name + "," + race + "," + color).getBytes();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
