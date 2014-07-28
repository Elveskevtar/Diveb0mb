package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet13SendNewProjectile extends Packet {

	public Packet13SendNewProjectile(int packetId) {
		super(packetId);
	}

	@Override
	public void writeData(GameClient client) {
		
	}

	@Override
	public void writeData(GameServer server) {
		
	}

	@Override
	public byte[] getData() {
		return null;
	}
}
