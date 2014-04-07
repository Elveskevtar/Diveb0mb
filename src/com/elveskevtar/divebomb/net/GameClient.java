package com.elveskevtar.divebomb.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.elveskevtar.divebomb.gfx.Game;
import com.elveskevtar.divebomb.net.packets.Packet;
import com.elveskevtar.divebomb.net.packets.Packet.PacketTypes;
import com.elveskevtar.divebomb.net.packets.Packet00Login;
import com.elveskevtar.divebomb.race.Cyborg;
import com.elveskevtar.divebomb.race.Human;
import com.elveskevtar.divebomb.race.Player;

public class GameClient extends Thread {

	private InetAddress IP;
	private DatagramSocket socket;
	private Game game;

	public GameClient(Game game, String IP) {
		this.setGame(game);
		try {
			this.socket = new DatagramSocket();
			this.IP = InetAddress.getByName(IP);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(),
					packet.getPort());
		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			handleLogin((Packet00Login) packet, address, port);
			break;
		case DISCONNECT:
			break;
		case STARTGAME:
			break;
		}
	}

	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, IP, 4545);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleLogin(Packet00Login packet, InetAddress address, int port) {
		System.out.println("[" + address.getHostAddress() + ":" + port + "] "
				+ packet.getName() + " has joined the game...");
		Player player = null;
		if (packet.getRace() == "human")
			player = new Human(game, address, port);
		if (packet.getRace().equals("cyborg") && packet.getColor().equals(""))
			player = new Cyborg(game, packet.getName(), -1, address, port);
		if (packet.getRace().equals("cyborg") && !packet.getColor().equals(""))
			player = new Cyborg(game, packet.getColor(), packet.getName(),
					address, port);
		game.getPlayers().add(player);
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public InetAddress getIP() {
		return IP;
	}

	public void setIP(InetAddress iP) {
		IP = iP;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}
}
