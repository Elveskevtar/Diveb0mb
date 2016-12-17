package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet19Ping extends Packet {

	private long timeStamp;
	private long pingLatency;

	private String name;

	public Packet19Ping(byte[] data) {
		super(19);
		String dataArray[] = readData(data).split(",");
		this.timeStamp = Long.parseLong(dataArray[0]);
		this.pingLatency = Long.parseLong(dataArray[1]);
		this.name = dataArray[2];
	}

	public Packet19Ping(long timeStamp, long pingLatency, String name) {
		super(19);
		this.timeStamp = timeStamp;
		this.pingLatency = pingLatency;
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
		return ("19" + timeStamp + "," + pingLatency + "," + name).getBytes();
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getPingLatency() {
		return pingLatency;
	}

	public void setPingLatency(long pingLatency) {
		this.pingLatency = pingLatency;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}