package com.elveskevtar.divebomb.net.packets;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;

public class Packet07Endgame extends Packet {

	private String winner;

	private int score;

	public Packet07Endgame(byte[] data) {
		super(07);
		String[] dataArray = readData(data).split(",");
		this.winner = dataArray[0];
		this.score = Integer.parseInt(dataArray[1]);
	}

	public Packet07Endgame(String winner, int score) {
		super(07);
		this.winner = winner;
		this.score = score;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
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
		return ("07" + winner + "," + score).getBytes();
	}
}
