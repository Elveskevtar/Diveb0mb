package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet15RemoveProjectile extends Packet {

	private int id;

	public Packet15RemoveProjectile(byte[] data) {
		super(01);
		this.id = Integer.parseInt(readData(data));
	}

	public Packet15RemoveProjectile(int id) {
		super(01);
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
		return ("15" + id).getBytes();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}