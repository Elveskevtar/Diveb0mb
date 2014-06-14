package com.elveskevtar.divebomb.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.elveskevtar.divebomb.gfx.Game;
import com.elveskevtar.divebomb.gfx.GameDeathmatchMP;
import com.elveskevtar.divebomb.gfx.GameLobbyMenu;
import com.elveskevtar.divebomb.maps.Map;
import com.elveskevtar.divebomb.net.packets.Packet;
import com.elveskevtar.divebomb.net.packets.Packet.PacketTypes;
import com.elveskevtar.divebomb.net.packets.Packet00Login;
import com.elveskevtar.divebomb.net.packets.Packet01Disconnect;
import com.elveskevtar.divebomb.net.packets.Packet02Startgame;
import com.elveskevtar.divebomb.net.packets.Packet03Move;
import com.elveskevtar.divebomb.net.packets.Packet04Attack;
import com.elveskevtar.divebomb.net.packets.Packet05Health;
import com.elveskevtar.divebomb.net.packets.Packet06Kill;
import com.elveskevtar.divebomb.net.packets.Packet07Endgame;
import com.elveskevtar.divebomb.net.packets.Packet11GameLobbyTime;
import com.elveskevtar.divebomb.race.Cyborg;
import com.elveskevtar.divebomb.race.Human;
import com.elveskevtar.divebomb.race.Player;
import com.elveskevtar.divebomb.weapons.Sword;

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

	@SuppressWarnings("deprecation")
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
			packet = new Packet01Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port
					+ "] " + ((Packet01Disconnect) packet).getName()
					+ " has left the game...");
			for (int i = 0; i < game.getPlayers().size(); i++)
				if (((Packet01Disconnect) packet).getName().equalsIgnoreCase(
						game.getPlayers().get(i).getName()))
					game.getPlayers().remove(i);
			break;
		case STARTGAME:
			packet = new Packet02Startgame(data);
			game.setGraphicsMap(new Map(((Packet02Startgame) packet)
					.getGraphicsMap(), ((Packet02Startgame) packet)
					.getCollisionMap(), ((Packet02Startgame) packet).getMapID()));
			game.getUser().setxPosition(
					((Packet02Startgame) packet).getStartX());
			game.getUser().setyPosition(
					((Packet02Startgame) packet).getStartY());
			game.startGame(game.getGraphicsMap());
			break;
		case MOVE:
			packet = new Packet03Move(data);
			Packet03Move movePacket = (Packet03Move) packet;
			Player player = getPlayer(((Packet03Move) packet).getName());
			player.setxPosition(movePacket.getX());
			player.setyPosition(movePacket.getY());
			player.setVeloX(movePacket.getVeloX());
			player.setVeloY(movePacket.getVeloY());
			player.setWalking(movePacket.isWalking());
			player.setRunning(movePacket.isRunning());
			player.setMovingRight(movePacket.isMovingRight());
			player.setFacingRight(movePacket.isFacingRight());
			break;
		case ATTACK:
			packet = new Packet04Attack(data);
			getPlayer(((Packet04Attack) packet).getName()).getInHand().attack(
					game.getPlayers(), false);
			break;
		case HEALTH:
			packet = new Packet05Health(data);
			getPlayer(((Packet05Health) packet).getName()).setHealth(
					((Packet05Health) packet).getHealth());
			break;
		case KILL:
			packet = new Packet06Kill(data);
			getPlayer(((Packet06Kill) packet).getMurderer())
					.setKills(
							getPlayer(((Packet06Kill) packet).getMurderer())
									.getKills() + 1);
			getPlayer(((Packet06Kill) packet).getVictim())
					.setDeaths(
							getPlayer(((Packet06Kill) packet).getVictim())
									.getDeaths() + 1);
			System.out.println("[" + address.getHostAddress() + ":" + port
					+ "] " + ((Packet06Kill) packet).getMurderer()
					+ " has killed " + ((Packet06Kill) packet).getVictim());
			if (game instanceof GameDeathmatchMP) {
				if (((GameDeathmatchMP) game).getFirstPlaceName() == null
						|| ((GameDeathmatchMP) game).getFirstPlaceKills() < getPlayer(
								((Packet06Kill) packet).getMurderer())
								.getKills()) {
					((GameDeathmatchMP) game).setFirstPlaceKills(getPlayer(
							((Packet06Kill) packet).getMurderer()).getKills());
					((GameDeathmatchMP) game)
							.setFirstPlaceName(((Packet06Kill) packet)
									.getMurderer());
				}
			}
			break;
		case ENDGAME:
			packet = new Packet07Endgame(data);
			System.out.println(((Packet07Endgame) packet).getWinner()
					+ " is the winner with a score of "
					+ ((Packet07Endgame) packet).getScore() + "!");
			game.setVisible(false);
			game.getFrame().remove(game);
			game.getTimer().cancel();
			game.setRunning(false);
			if (game.getSocketServer() != null) {
				game.getSocketServer().getSocket().close();
				game.getSocketServer().stop();
				game.getFrame().add(
						new GameLobbyMenu(game.getFrame(), game
								.getUserName()));
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				game.getFrame().add(
						new GameLobbyMenu(game.getFrame(), game
								.getSocketClient().getIP().getHostAddress(),
								game.getUserName()));
			}
			game.getFrame().repaint();
			stop();
			break;
		case GAMELOBBYTIME:
			packet = new Packet11GameLobbyTime(data);
			game.setLobbyTime(((Packet11GameLobbyTime) packet).getSeconds());
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
		if (packet.getRace().equalsIgnoreCase("human"))
			player = new Human(game, packet.getName(), address, port);
		else if (packet.getRace().equalsIgnoreCase("cyborg")
				&& packet.getColor().equalsIgnoreCase(" "))
			player = new Cyborg(game, packet.getName(), -1, address, port);
		else if (packet.getRace().equalsIgnoreCase("cyborg")
				&& !packet.getColor().equalsIgnoreCase(" "))
			player = new Cyborg(game, packet.getColor(), packet.getName(),
					address, port);
		else
			player = new Human(game, packet.getName(), address, port);
		if (packet.getWeapon().equalsIgnoreCase("sword"))
			player.setInHand(new Sword(player));
		else
			player.setInHand(new Sword(player));
		game.getPlayers().add(player);
	}

	public Player getPlayer(String name) {
		for (Player player : game.getPlayers())
			if (player.getName().equalsIgnoreCase(name))
				return player;
		return null;
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
