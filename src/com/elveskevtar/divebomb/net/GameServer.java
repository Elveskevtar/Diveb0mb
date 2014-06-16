package com.elveskevtar.divebomb.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import com.elveskevtar.divebomb.gfx.Game;
import com.elveskevtar.divebomb.maps.SpawnPoints;
import com.elveskevtar.divebomb.net.packets.Packet;
import com.elveskevtar.divebomb.net.packets.Packet.PacketTypes;
import com.elveskevtar.divebomb.net.packets.Packet00Login;
import com.elveskevtar.divebomb.net.packets.Packet01Disconnect;
import com.elveskevtar.divebomb.net.packets.Packet02Startgame;
import com.elveskevtar.divebomb.net.packets.Packet03Move;
import com.elveskevtar.divebomb.net.packets.Packet04Attack;
import com.elveskevtar.divebomb.net.packets.Packet05Health;
import com.elveskevtar.divebomb.net.packets.Packet10UpdateUserInfo;
import com.elveskevtar.divebomb.net.packets.Packet11GameLobbyTime;
import com.elveskevtar.divebomb.race.Cyborg;
import com.elveskevtar.divebomb.race.Human;
import com.elveskevtar.divebomb.race.Player;
import com.elveskevtar.divebomb.weapons.Sword;
import com.elveskevtar.divebomb.weapons.Weapon;

public class GameServer extends Thread {

	public ArrayList<Player> connectedPlayers = new ArrayList<Player>();
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
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			if (connectedPlayers.size() != game.getPlayerSize()
					&& !game.isRunning()) {
				packet = new Packet00Login(data);
				System.out.println("[" + address.getHostAddress() + ":" + port
						+ "] " + ((Packet00Login) packet).getName()
						+ " has connected...");
				Player player = null;
				Weapon weapon = null;
				if (((Packet00Login) packet).getRace()
						.equalsIgnoreCase("human"))
					player = new Human(game,
							((Packet00Login) packet).getName(), address, port);
				else if (((Packet00Login) packet).getRace().equalsIgnoreCase(
						"cyborg")
						&& ((Packet00Login) packet).getColor()
								.equalsIgnoreCase(" "))
					player = new Cyborg(game,
							((Packet00Login) packet).getName(), -1, address,
							port);
				else if (((Packet00Login) packet).getRace().equalsIgnoreCase(
						"cyborg")
						&& !((Packet00Login) packet).getColor()
								.equalsIgnoreCase(" "))
					player = new Cyborg(game,
							((Packet00Login) packet).getColor(),
							((Packet00Login) packet).getName(), address, port);
				else
					player = new Human(game,
							((Packet00Login) packet).getName(), address, port);
				if (((Packet00Login) packet).getWeapon().equalsIgnoreCase(
						"sword"))
					weapon = new Sword(player);
				else
					weapon = new Sword(player);
				player.setInHand(weapon);
				addConnection(player, (Packet00Login) packet);
			}
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port
					+ "] " + ((Packet01Disconnect) packet).getName()
					+ " has disconnected...");
			connectedPlayers
					.remove(getPlayerMPIndex(((Packet01Disconnect) packet)
							.getName()));
			packet.writeData(this);
			break;
		case MOVE:
			packet = new Packet03Move(data);
			Player player = getPlayerMP(((Packet03Move) packet).getName());
			if (player != null)
				handleMove(player, (Packet03Move) packet);
			break;
		case ATTACK:
			packet = new Packet04Attack(data);
			getPlayerMP(((Packet04Attack) packet).getName()).getInHand()
					.attack(connectedPlayers, true);
			for (Player p : connectedPlayers)
				if (!p.getName().equalsIgnoreCase(
						((Packet04Attack) packet).getName()))
					sendData(packet.getData(), p.getIP(), p.getPort());
			break;
		case HEALTH:
			packet = new Packet05Health(data);
			getPlayerMP(((Packet05Health) packet).getName()).setHealth(
					((Packet05Health) packet).getHealth());
			break;
		case UPDATEUSERINFO:
			packet = new Packet10UpdateUserInfo(data);
			Weapon weapon = null;
			if (((Packet10UpdateUserInfo) packet).getRace().equalsIgnoreCase(
					"human"))
				connectedPlayers.set(
						getPlayerMPIndex(((Packet10UpdateUserInfo) packet)
								.getName()), new Human(game,
								((Packet10UpdateUserInfo) packet).getName(),
								address, port));
			else if (((Packet10UpdateUserInfo) packet).getRace()
					.equalsIgnoreCase("cyborg")
					&& ((Packet10UpdateUserInfo) packet).getColor()
							.equalsIgnoreCase(" "))
				connectedPlayers.set(
						getPlayerMPIndex(((Packet10UpdateUserInfo) packet)
								.getName()), new Cyborg(game,
								((Packet10UpdateUserInfo) packet).getName(), -1,
								address, port));
			else if (((Packet10UpdateUserInfo) packet).getRace().equalsIgnoreCase(
					"cyborg")
					&& !((Packet10UpdateUserInfo) packet).getColor().equalsIgnoreCase(
							" "))
				player = new Cyborg(game, ((Packet10UpdateUserInfo) packet).getColor(),
						((Packet10UpdateUserInfo) packet).getName(), address, port);
			else
				connectedPlayers.set(
						getPlayerMPIndex(((Packet10UpdateUserInfo) packet)
								.getName()), new Human(game,
								((Packet10UpdateUserInfo) packet).getName(), address,
								port));
			if (((Packet10UpdateUserInfo) packet).getWeapon().equalsIgnoreCase(
					"sword"))
				weapon = new Sword(
						connectedPlayers
								.get(getPlayerMPIndex(((Packet10UpdateUserInfo) packet)
										.getName())));
			else
				weapon = new Sword(
						connectedPlayers
								.get(getPlayerMPIndex(((Packet10UpdateUserInfo) packet)
										.getName())));
			connectedPlayers.get(
					getPlayerMPIndex(((Packet10UpdateUserInfo) packet)
							.getName())).setInHand(weapon);
			break;
		}
		for (Player p : connectedPlayers) {
			if (p.getHealth() <= 0) {
				p.setDead(true);
				p.setHealth(0);
			} else {
				p.setDead(false);
			}
		}
		if (connectedPlayers.size() == game.getPlayerSize()
				&& !game.isRunning()) {
			game.setRunning(true);
			game.setLobbyTime(5);
			for (int i = game.getLobbyTime(); i >= 0; i--) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				game.setLobbyTime(i);
				Packet11GameLobbyTime gameLobbyPacket = new Packet11GameLobbyTime(
						game.getLobbyTime());
				gameLobbyPacket.writeData(this);
			}
			
			int size = connectedPlayers.size();
			for (int i = 0; i < size; i++) {
				byte[] updateData = new byte[1024];
				DatagramPacket updatePacket = new DatagramPacket(updateData,
						updateData.length);
				try {
					socket.receive(updatePacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.parsePacket(updatePacket.getData(),
						updatePacket.getAddress(), updatePacket.getPort());
			}
			Packet02Startgame startGamePacket = null;
			ArrayList<SpawnPoints> spawnPoints = new ArrayList<SpawnPoints>();
			for (SpawnPoints point : SpawnPoints.values())
				if (point.getMapID() == game.getGraphicsMap().getId())
					spawnPoints.add(point);
			if (spawnPoints.size() >= connectedPlayers.size()) {
				int index = 0;
				for (SpawnPoints point : spawnPoints) {
					startGamePacket = new Packet02Startgame(game
							.getGraphicsMap().getId(), game.getGraphicsMap()
							.getMapPath(), game.getGraphicsMap()
							.getCollisionMapPath(), point.getX(), point.getY());
					startGamePacket.writeData(this, connectedPlayers.get(index)
							.getIP(), connectedPlayers.get(index).getPort());
					index++;
					if (index == connectedPlayers.size())
						break;
				}
			} else {
				int index = 0;
				while (index < connectedPlayers.size()) {
					for (SpawnPoints point : spawnPoints) {
						startGamePacket = new Packet02Startgame(game
								.getGraphicsMap().getId(), game
								.getGraphicsMap().getMapPath(), game
								.getGraphicsMap().getCollisionMapPath(),
								point.getX(), point.getY());
						startGamePacket.writeData(this,
								connectedPlayers.get(index).getIP(),
								connectedPlayers.get(index).getPort());
						index++;
						if (index == connectedPlayers.size())
							break;
					}
				}
			}
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
				sendData(packet.getData(), p.getIP(), p.getPort());
				String color = " ";
				String race = " ";
				String weapon = " ";
				if (p instanceof Human)
					race = "human";
				else if (p instanceof Cyborg)
					race = "cyborg";
				if (p.getColor() != null)
					color = p.getColor();
				if (p.getInHand() instanceof Sword)
					weapon = "sword";
				Packet00Login oldPlayerPacket = new Packet00Login(p.getName(),
						race, color, weapon);
				sendData(oldPlayerPacket.getData(), player.getIP(),
						player.getPort());
			}
		}
		if (!alreadyConnected) {
			connectedPlayers.add(player);
		}
	}

	public void handleMove(Player player, Packet03Move packet) {
		player.setxPosition(packet.getX());
		player.setyPosition(packet.getY());
		player.setWalking(packet.isWalking());
		player.setRunning(packet.isRunning());
		player.setMovingRight(packet.isMovingRight());
		player.setFacingRight(packet.isFacingRight());
		for (Player p : connectedPlayers) {
			if (!p.getName().equalsIgnoreCase(player.getName())) {
				sendData(packet.getData(), p.getIP(), p.getPort());
			}
		}
	}

	public Player getPlayerMP(String name) {
		for (Player player : connectedPlayers) {
			if (player.getName().equalsIgnoreCase(name)) {
				return player;
			}
		}
		return null;
	}

	public int getPlayerMPIndex(String name) {
		int index = 0;
		for (Player player : connectedPlayers) {
			if (player.getName().equalsIgnoreCase(name)) {
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
	
	public DatagramSocket getSocket() {
		return socket;
	}
	
	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}
}
