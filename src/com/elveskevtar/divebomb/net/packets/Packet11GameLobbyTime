package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet11GameLobbyTime extends Packet {

	private int seconds;

	public Packet11GameLobbyTime(byte[] data) {
		super(11);
		this.setSeconds(Integer.parseInt(readData(data)));
	}

	public Packet11GameLobbyTime(int seconds) {
		super(11);
		this.seconds = seconds;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
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
		return ("11" + seconds).getBytes();
	}
}
