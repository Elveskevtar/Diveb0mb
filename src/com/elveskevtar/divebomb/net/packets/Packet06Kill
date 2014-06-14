package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet06Kill extends Packet {

	private String murderer;
	private String victim;

	public Packet06Kill(byte[] data) {
		super(06);
		String[] dataArray = readData(data).split(",");
		this.murderer = dataArray[0];
		this.victim = dataArray[1];
	}

	public Packet06Kill(String murderer, String victim) {
		super(06);
		this.murderer = murderer;
		this.victim = victim;
	}

	public String getMurderer() {
		return murderer;
	}

	public void setMurderer(String murderer) {
		this.murderer = murderer;
	}

	public String getVictim() {
		return victim;
	}

	public void setVictim(String victim) {
		this.victim = victim;
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
		return ("06" + murderer + "," + victim).getBytes();
	}
}
