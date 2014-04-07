package com.elveskevtar.divebomb.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.elveskevtar.divebomb.gfx.Game;
import com.elveskevtar.divebomb.net.packets.Packet;
import com.elveskevtar.divebomb.net.packets.Packet.PacketTypes;
import com.elveskevtar.divebomb.net.packets.Packet00Login;
import com.elveskevtar.divebomb.race.Cyborg;
import com.elveskevtar.divebomb.race.Human;
import com.elveskevtar.divebomb.race.Player;

public class GameServer extends Thread {

	public List<Player> connectedPlayers = new ArrayList<Player>();
	private DatagramSocket socket;
	private Game game;

	public GameServer(Game game) {
		this.game = game;
		try {
			this.socket = new DatagramSocket(4545);
		} catch (SocketException e) {
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
		System.out.println(port);
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			if (connectedPlayers.size() != game.getPlayerSize()) {
				packet = new Packet00Login(data);
				System.out.println("[" + address.getHostAddress() + ":" + port
						+ "] " + ((Packet00Login) packet).getName()
						+ " has connected...");
				Player player = null;
				if (((Packet00Login) packet).getRace()
						.equalsIgnoreCase("human"))
					player = new Human(game,
							((Packet00Login) packet).getName(), address, port);
				if (((Packet00Login) packet).getRace().equalsIgnoreCase(
						"cyborg")
						&& ((Packet00Login) packet).getColor().equals(""))
					player = new Cyborg(game,
							((Packet00Login) packet).getName(), -1, address,
							port);
				if (((Packet00Login) packet).getRace().equalsIgnoreCase(
						"cyborg")
						&& !((Packet00Login) packet).getColor().equals(""))
					player = new Cyborg(game,
							((Packet00Login) packet).getColor(),
							((Packet00Login) packet).getName(), address, port);
				addConnection(player, (Packet00Login) packet);
			}
			break;
		case DISCONNECT:
			break;
		case STARTGAME:
			break;
		}
	}

	public void addConnection(Player player, Packet00Login packet) {
		boolean alreadyConnected = false;
		for (Player p : connectedPlayers) {
			if (player.getName().equalsIgnoreCase(p.getName())) {
				if (p.getIP() == null) {
					p.setIP(player.getIP());
				}
				if (p.getPort() == -1) {
					p.setPort(player.getPort());
				}
				alreadyConnected = true;
			} else {
				//System.out.println("[" + p.getIP() + ":" + p.getPort() + "]");
				sendData(packet.getData(), p.getIP(), p.getPort());
				String color = "";
				String race = "";
				if (player instanceof Human)
					race = "human";
				if (player instanceof Cyborg)
					race = "cyborg";
				if (player.getColor() != null)
					color = player.getColor();
				else
					color = "rand";
				packet = new Packet00Login(p.getName(), race, color);
				sendData(packet.getData(), player.getIP(), player.getPort());
			}
		}
		if (!alreadyConnected) {
			connectedPlayers.add(player);
		}
	}

	public Player getPlayerMP(String name) {
		for (Player player : connectedPlayers) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}

	public int getPlayerMPIndex(String name) {
		int index = 0;
		for (Player player : connectedPlayers) {
			if (player.getName().equals(name)) {
				break;
			}
			index++;
		}
		return index;
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length,
				ipAddress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDataToAllClients(byte[] data) {
		for (Player p : connectedPlayers) {
			sendData(data, p.getIP(), p.getPort());
		}
	}
}
