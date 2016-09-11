package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet10UpdateUserInfo extends Packet {

	private String name;
	private String race;
	private String color;
	private String meleeWeapon;
	private String rangedWeapon;

	public Packet10UpdateUserInfo(byte[] data) {
		super(10);
		String[] dataArray = readData(data).split(",");
		this.name = dataArray[0];
		this.race = dataArray[1];
		this.color = dataArray[2];
		this.meleeWeapon = dataArray[3];
		this.rangedWeapon = dataArray[4];
	}

	public Packet10UpdateUserInfo(String name, String race, String color, String meleeWeapon, String rangedWeapon) {
		super(10);
		this.name = name;
		this.race = race;
		this.color = color;
		this.meleeWeapon = meleeWeapon;
		this.rangedWeapon = rangedWeapon;
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
		return ("10" + name + "," + race + "," + color + "," + meleeWeapon + "," + rangedWeapon).getBytes();
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

	public String getMeleeWeapon() {
		return meleeWeapon;
	}

	public void setMeleeWeapon(String meleeWeapon) {
		this.meleeWeapon = meleeWeapon;
	}

	public String getRangedWeapon() {
		return rangedWeapon;
	}

	public void setRangedWeapon(String rangedWeapon) {
		this.rangedWeapon = rangedWeapon;
	}
}
