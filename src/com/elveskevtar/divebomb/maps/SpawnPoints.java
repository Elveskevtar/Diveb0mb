package com.elveskevtar.divebomb.maps;

public enum SpawnPoints {

	TESTMAP1(0, 1, 1, -160, -902), TESTMAP2(0, 1, 2, 0, -902), TESTMAP3(0, 2,
			3, -1000, -1056), TESTMAP4(0, 2, 4, -1128, -1056);

	private int mapID;
	private int team;
	private int spawnID;
	private double x;
	private double y;

	SpawnPoints(int mapID, int team, int spawnID, double x, double y) {
		this.mapID = mapID;
		this.team = team;
		this.spawnID = spawnID;
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

	public int getSpawnID() {
		return spawnID;
	}

	public void setSpawnID(int spawnID) {
		this.spawnID = spawnID;
	}
}
