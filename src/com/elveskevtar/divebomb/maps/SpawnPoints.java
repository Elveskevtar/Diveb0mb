package com.elveskevtar.divebomb.maps;

public enum SpawnPoints {

	TESTMAP1(0, 1, -160, -1056),
	TESTMAP2(0, 1, 0, -1056),
	TESTMAP3(0, 2, -1000, -1056),
	TESTMAP4(0, 2, -1128, -1056);
	
	private int mapID;
	private int team;
	private double x;
	private double y;
	
	SpawnPoints(int mapID, int team, double x, double y) {
		this.mapID = mapID;
		this.team = team;
		this.x = x;
		this.y = y;
	}

	public int getMapID() {
		return mapID;
	}

	public void setMapID(int mapID) {
		this.mapID = mapID;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
}
