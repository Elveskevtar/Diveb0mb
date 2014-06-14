package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet12GameType extends Packet {

	private int type;

	public Packet12GameType(byte[] data) {
		super(12);
		this.type = Integer.parseInt(readData(data));
	}

	public Packet12GameType(int type) {
		super(12);
		this.setType(type);
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
		return ("12" + type).getBytes();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
