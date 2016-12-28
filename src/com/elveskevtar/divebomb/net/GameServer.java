package com.elveskevtar.divebomb.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

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
import com.elveskevtar.divebomb.net.packets.Packet06Kill;
import com.elveskevtar.divebomb.net.packets.Packet10UpdateUserInfo;
import com.elveskevtar.divebomb.net.packets.Packet11GameLobbyTime;
import com.elveskevtar.divebomb.net.packets.Packet13SendNewProjectile;
import com.elveskevtar.divebomb.net.packets.Packet16Suicide;
import com.elveskevtar.divebomb.net.packets.Packet17Respawn;
import com.elveskevtar.divebomb.net.packets.Packet18RespawnPlayer;
import com.elveskevtar.divebomb.net.packets.Packet19Ping;
import com.elveskevtar.divebomb.race.Cyborg;
import com.elveskevtar.divebomb.race.Human;
import com.elveskevtar.divebomb.race.Player;
import com.elveskevtar.divebomb.weapons.Arrow;
import com.elveskevtar.divebomb.weapons.Bow;
import com.elveskevtar.divebomb.weapons.Sword;
import com.elveskevtar.divebomb.weapons.Weapon;

public class GameServer extends Thread {

	public CopyOnWriteArrayList<Player> connectedPlayers = new CopyOnWriteArrayList<Player>();
	private DatagramSocket socket;

	private Game game;

	private boolean serverRunning;
	private boolean starting;

	private int PORT;
	private int updatedPlayerCount;

	public GameServer(Game game, int port) {
		this.PORT = port;
		this.game = game;
		try {
			this.socket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.setServerRunning(true);
		new Thread(new CheckForPingDrops()).start();
	}

	public void run() {
		while (isServerRunning()) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
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
			if (connectedPlayers.size() != game.getPlayerSize() && !game.isRunning()) {
				packet = new Packet00Login(data);
				System.out.println("[" + address.getHostAddress() + ":" + port + "] "
						+ ((Packet00Login) packet).getName() + " has connected... ");
				addConnection(new Human(game, ((Packet00Login) packet).getName(), address, port),
						(Packet00Login) packet);
			}
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] "
					+ ((Packet01Disconnect) packet).getName() + " has disconnected...");
			connectedPlayers.remove(getPlayerMPIndex(((Packet01Disconnect) packet).getName()));
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
			getPlayerMP(((Packet04Attack) packet).getName()).getInHand().attack(connectedPlayers, true);
			for (Player p : connectedPlayers)
				if (!p.getName().equalsIgnoreCase(((Packet04Attack) packet).getName()))
					sendData(packet.getData(), p.getIP(), p.getPort());
			break;
		case HEALTH:
			packet = new Packet05Health(data);
			getPlayerMP(((Packet05Health) packet).getName()).setHealth(((Packet05Health) packet).getHealth());
			break;
		case UPDATEUSERINFO:
			packet = new Packet10UpdateUserInfo(data);
			Weapon melee = null;
			Weapon ranged = null;
			if (((Packet10UpdateUserInfo) packet).getRace().equalsIgnoreCase("human"))
				connectedPlayers.set(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName()),
						new Human(game, ((Packet10UpdateUserInfo) packet).getName(), address, port));
			else if (((Packet10UpdateUserInfo) packet).getRace().equalsIgnoreCase("cyborg")
					&& ((Packet10UpdateUserInfo) packet).getColor().equalsIgnoreCase(" "))
				connectedPlayers.set(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName()),
						new Cyborg(game, ((Packet10UpdateUserInfo) packet).getName(), -1, address, port));
			else if (((Packet10UpdateUserInfo) packet).getRace().equalsIgnoreCase("cyborg")
					&& !((Packet10UpdateUserInfo) packet).getColor().equalsIgnoreCase(" "))
				connectedPlayers.set(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName()),
						new Cyborg(game, ((Packet10UpdateUserInfo) packet).getColor(),
								((Packet10UpdateUserInfo) packet).getName(), address, port));
			else
				connectedPlayers.set(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName()),
						new Human(game, ((Packet10UpdateUserInfo) packet).getName(), address, port));
			if (((Packet10UpdateUserInfo) packet).getMeleeWeapon().equalsIgnoreCase("sword"))
				melee = new Sword(connectedPlayers.get(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName())));
			else
				melee = new Sword(connectedPlayers.get(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName())));
			if (((Packet10UpdateUserInfo) packet).getRangedWeapon().equalsIgnoreCase("bow"))
				ranged = new Bow(connectedPlayers.get(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName())));
			else
				ranged = new Bow(connectedPlayers.get(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName())));
			connectedPlayers.get(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName())).setMelee(melee);
			connectedPlayers.get(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName())).setRanged(ranged);
			connectedPlayers.get(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName())).setInHand(
					connectedPlayers.get(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName())).getMelee());
			handleUpdateUserInfo(connectedPlayers.get(getPlayerMPIndex(((Packet10UpdateUserInfo) packet).getName())),
					(Packet10UpdateUserInfo) packet);
			updatedPlayerCount++;
			break;
		case SENDNEWPROJECTILE:
			packet = new Packet13SendNewProjectile(data);
			if (((Packet13SendNewProjectile) packet).getType().equalsIgnoreCase("arrow")) {
				Arrow arrow = new Arrow(getPlayerMP(((Packet13SendNewProjectile) packet).getName()),
						((Packet13SendNewProjectile) packet).getId(),
						((Packet13SendNewProjectile) packet).getxPosition(),
						((Packet13SendNewProjectile) packet).getyPosition(),
						((Packet13SendNewProjectile) packet).getrAngle());
				game.getProjectiles().add(arrow);
				game.getProjectileIDs().add(((Packet13SendNewProjectile) packet).getId());
				Packet13SendNewProjectile projectilePacket = new Packet13SendNewProjectile("arrow",
						arrow.getxPosition(), arrow.getyPosition(), arrow.getrAngle(), arrow.getPlayer().getName(),
						((Packet13SendNewProjectile) packet).getId());
				projectilePacket.writeData(this);
			}
			break;
		case SUICIDE:
			packet = new Packet16Suicide(data);
			getPlayerMP(((Packet16Suicide) packet).getName())
					.setDeaths(getPlayerMP(((Packet16Suicide) packet).getName()).getDeaths() + 1);
			Packet06Kill killPacket = new Packet06Kill(" ", ((Packet16Suicide) packet).getName());
			killPacket.writeData(this);
			break;
		case RESPAWN:
			packet = new Packet17Respawn(data);
			getPlayerMP(((Packet17Respawn) packet).getName())
					.setHealth(getPlayerMP(((Packet17Respawn) packet).getName()).getMaxHealth());
			ArrayList<SpawnPoints> spawnPoints = new ArrayList<SpawnPoints>();
			for (SpawnPoints point : SpawnPoints.values())
				if (point.getMapID() == game.getGraphicsMap().getId())
					spawnPoints.add(point);
			int r = new Random().nextInt(spawnPoints.size() - 1);
			Packet18RespawnPlayer respawnPacket = new Packet18RespawnPlayer(((Packet17Respawn) packet).getName(),
					spawnPoints.get(r).getX(), spawnPoints.get(r).getY());
			respawnPacket.writeData(this);
			break;
		case PING:
			packet = new Packet19Ping(data);
			if (getPlayerMP(((Packet19Ping) packet).getName()) != null) {
				getPlayerMP(((Packet19Ping) packet).getName()).setLatency(((Packet19Ping) packet).getTimeStamp()
						- getPlayerMP(((Packet19Ping) packet).getName()).getOldTimeStamp());
				getPlayerMP(((Packet19Ping) packet).getName()).setOldTimeStamp(((Packet19Ping) packet).getTimeStamp());
				Packet19Ping pingPacket = new Packet19Ping(((Packet19Ping) packet).getTimeStamp(),
						getPlayerMP(((Packet19Ping) packet).getName()).getLatency(), ((Packet19Ping) packet).getName());
				pingPacket.writeData(this);
			}
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
		if (connectedPlayers.size() == game.getPlayerSize() && !game.isRunning() && !isStarting()) {
			setStarting(true);
			new Thread(new initGame()).start();
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
				sendData(new Packet00Login(p.getName()).getData(), player.getIP(), player.getPort());
			}
		}
		if (!alreadyConnected) {
			connectedPlayers.add(player);
		}
	}

	public void handleUpdateUserInfo(Player player, Packet10UpdateUserInfo packet) {
		for (Player p : connectedPlayers)
			if (!p.getName().equalsIgnoreCase(player.getName()))
				sendData(packet.getData(), p.getIP(), p.getPort());
	}

	public void handleMove(Player player, Packet03Move packet) {
		player.setxPosition(packet.getX());
		player.setyPosition(packet.getY());
		player.setWalking(packet.isWalking());
		player.setRunning(packet.isRunning());
		player.setMovingRight(packet.isMovingRight());
		player.setFacingRight(packet.isFacingRight());
		if (!packet.getWeaponInHand().equalsIgnoreCase(player.getInHand().getName())) {
			if (player.getInHand().getName().equalsIgnoreCase(player.getMelee().getName()))
				player.setInHand(player.getRanged());
			else
				player.setInHand(player.getMelee());
		}
		for (Player p : connectedPlayers)
			if (!p.getName().equalsIgnoreCase(player.getName()))
				sendData(packet.getData(), p.getIP(), p.getPort());
	}

	public Player getPlayerMP(String name) {
		for (Player player : connectedPlayers)
			if (player.getName().equalsIgnoreCase(name))
				return player;
		return null;
	}

	public int getPlayerMPIndex(String name) {
		int index = 0;
		for (Player player : connectedPlayers) {
			if (player.getName().equalsIgnoreCase(name))
				break;
			index++;
		}
		return index;
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDataToAllClients(byte[] data) {
		for (Player p : connectedPlayers)
			sendData(data, p.getIP(), p.getPort());
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	public CopyOnWriteArrayList<Player> getConnectedPlayers() {
		return connectedPlayers;
	}

	public void setConnectedPlayers(CopyOnWriteArrayList<Player> connectedPlayers) {
		this.connectedPlayers = connectedPlayers;
	}

	public boolean isServerRunning() {
		return serverRunning;
	}

	public void setServerRunning(boolean serverRunning) {
		this.serverRunning = serverRunning;
	}

	public boolean isStarting() {
		return starting;
	}

	public void setStarting(boolean starting) {
		this.starting = starting;
	}

	public int getPORT() {
		return PORT;
	}

	public void setPORT(int pORT) {
		PORT = pORT;
	}

	private class initGame extends Thread {

		@Override
		public void run() {
			game.setLobbyTime(10);
			for (int i = game.getLobbyTime(); i >= 0; i--) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				game.setLobbyTime(i);
				Packet11GameLobbyTime gameLobbyPacket = new Packet11GameLobbyTime(game.getLobbyTime());
				gameLobbyPacket.writeData(GameServer.this);
				if (connectedPlayers.size() < game.getPlayerSize()) {
					game.setLobbyTime(-1);
					Packet11GameLobbyTime stopLobbyPacket = new Packet11GameLobbyTime(game.getLobbyTime());
					stopLobbyPacket.writeData(GameServer.this);
					setStarting(false);
					return;
				}
			}
			while (updatedPlayerCount != connectedPlayers.size()) {
				byte[] updateData = new byte[1024];
				DatagramPacket updatePacket = new DatagramPacket(updateData, updateData.length);
				try {
					socket.receive(updatePacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
				parsePacket(updatePacket.getData(), updatePacket.getAddress(), updatePacket.getPort());
			}
			game.setRunning(true);
			Packet02Startgame startGamePacket = null;
			ArrayList<SpawnPoints> spawnPoints = new ArrayList<SpawnPoints>();
			for (SpawnPoints point : SpawnPoints.values())
				if (point.getMapID() == game.getGraphicsMap().getId())
					spawnPoints.add(point);
			if (spawnPoints.size() >= connectedPlayers.size()) {
				int index = 0;
				for (SpawnPoints point : spawnPoints) {
					startGamePacket = new Packet02Startgame(game.getGraphicsMap().getId(),
							game.getGraphicsMap().getMapPath(), game.getGraphicsMap().getCollisionMapPath(),
							point.getX(), point.getY());
					startGamePacket.writeData(GameServer.this, connectedPlayers.get(index).getIP(),
							connectedPlayers.get(index).getPort());
					index++;
					if (index == connectedPlayers.size())
						break;
				}
			} else {
				int index = 0;
				while (index < connectedPlayers.size()) {
					for (SpawnPoints point : spawnPoints) {
						startGamePacket = new Packet02Startgame(game.getGraphicsMap().getId(),
								game.getGraphicsMap().getMapPath(), game.getGraphicsMap().getCollisionMapPath(),
								point.getX(), point.getY());
						startGamePacket.writeData(GameServer.this, connectedPlayers.get(index).getIP(),
								connectedPlayers.get(index).getPort());
						index++;
						if (index == connectedPlayers.size())
							break;
					}
				}
			}
			if (game.getSocketClient() == null)
				game.startPublicServerGame(game.getGraphicsMap());
		}
	}

	private class CheckForPingDrops extends Thread {

		@Override
		public void run() {
			while (isServerRunning()) {
				for (Player p : connectedPlayers) {
					p.setLatency(System.nanoTime() - p.getOldTimeStamp());
					if (((int) (p.getLatency() * Math.pow(10, -6)) >= 5000) && p.getOldTimeStamp() != 0) {
						Packet01Disconnect packet = new Packet01Disconnect(p.getName());
						packet.writeData(GameServer.this);
						System.out.println("[" + getSocket().getLocalAddress().getHostAddress() + ":"
								+ getSocket().getLocalPort() + "] " + p.getName() + " has disconnected...");
						connectedPlayers.remove(p);
						game.getPlayers().remove(p);
					}
				}
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}