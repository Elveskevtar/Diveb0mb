package com.elveskevtar.divebomb.net.packets;

import java.net.InetAddress;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet02Startgame extends Packet {

	private String graphicsMap;
	private String collisionMap;

	private int mapID;
	private double startX;
	private double startY;

	public Packet02Startgame(byte[] data) {
		super(02);
		String[] dataArray = readData(data).split(",");
		this.mapID = Integer.parseInt(dataArray[0]);
		this.graphicsMap = dataArray[1];
		this.collisionMap = dataArray[2];
		this.startX = Double.parseDouble(dataArray[3]);
		this.startY = Double.parseDouble(dataArray[4]);
	}

	public Packet02Startgame(int mapID, String graphicsMap, String collisionMap, double startX, double startY) {
		super(02);
		this.mapID = mapID;
		this.graphicsMap = graphicsMap;
		this.collisionMap = collisionMap;
		this.startX = startX;
		this.startY = startY;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	public void writeData(GameServer server, InetAddress ip, int port) {
		server.sendData(getData(), ip, port);
	}

	@Override
	public byte[] getData() {
		return ("02" + mapID + "," + graphicsMap + "," + collisionMap + "," + startX + "," + startY).getBytes();
	}

	public String getGraphicsMap() {
		return graphicsMap;
	}

	public void setGraphicsMap(String graphicsMap) {
		this.graphicsMap = graphicsMap;
	}

	public String getCollisionMap() {
		return collisionMap;
	}

	public void setCollisionMap(String collisionMap) {
		this.collisionMap = collisionMap;
	}

	public int getMapID() {
		return mapID;
	}

	public void setMapID(int mapID) {
		this.mapID = mapID;
	}

	public double getStartX() {
		return startX;
	}

	public void setStartX(double startX) {
		this.startX = startX;
	}

	public double getStartY() {
		return startY;
	}

	public void setStartY(double startY) {
		this.startY = startY;
	}
}