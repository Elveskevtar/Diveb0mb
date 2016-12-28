package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet00Login extends Packet {

	private String name;

	public Packet00Login(byte[] data) {
		super(00);
		this.name = readData(data);
	}

	public Packet00Login(String name) {
		super(00);
		this.name = name;
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
		return ("00" + name).getBytes();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}