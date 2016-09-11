package com.elveskevtar.divebomb.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ConcurrentModificationException;

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
import com.elveskevtar.divebomb.net.packets.Packet10UpdateUserInfo;
import com.elveskevtar.divebomb.net.packets.Packet11GameLobbyTime;
import com.elveskevtar.divebomb.net.packets.Packet13SendNewProjectile;
import com.elveskevtar.divebomb.net.packets.Packet14UpdateProjectile;
import com.elveskevtar.divebomb.net.packets.Packet15RemoveProjectile;
import com.elveskevtar.divebomb.net.packets.Packet18RespawnPlayer;
import com.elveskevtar.divebomb.race.Cyborg;
import com.elveskevtar.divebomb.race.Human;
import com.elveskevtar.divebomb.race.Player;
import com.elveskevtar.divebomb.weapons.Arrow;
import com.elveskevtar.divebomb.weapons.Bow;
import com.elveskevtar.divebomb.weapons.ProjectileShooter;
import com.elveskevtar.divebomb.weapons.Sword;
import com.elveskevtar.divebomb.weapons.Weapon;

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
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
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
			System.out.println("[" + address.getHostAddress() + ":" + port + "] "
					+ ((Packet01Disconnect) packet).getName() + " has left the game...");
			for (int i = 0; i < game.getPlayers().size(); i++)
				if (((Packet01Disconnect) packet).getName().equalsIgnoreCase(game.getPlayers().get(i).getName()))
					game.getPlayers().remove(i);
			break;
		case STARTGAME:
			packet = new Packet02Startgame(data);
			game.setGraphicsMap(new Map(((Packet02Startgame) packet).getGraphicsMap(),
					((Packet02Startgame) packet).getCollisionMap(), ((Packet02Startgame) packet).getMapID()));
			game.startGame(game.getGraphicsMap());
			game.getUser().setxPosition(((Packet02Startgame) packet).getStartX());
			game.getUser().setyPosition(((Packet02Startgame) packet).getStartY());
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
			if (!movePacket.getWeaponInHand().equalsIgnoreCase(player.getInHand().getName())) {
				if (player.getInHand().getName().equalsIgnoreCase(player.getMelee().getName()))
					player.setInHand(player.getRanged());
				else
					player.setInHand(player.getMelee());
			}
			if (player.getInHand() instanceof ProjectileShooter)
				((ProjectileShooter) player.getInHand()).setrAngle(movePacket.getrAngle());
			break;
		case ATTACK:
			packet = new Packet04Attack(data);
			getPlayer(((Packet04Attack) packet).getName()).getInHand().attack(game.getPlayers(), false);
			break;
		case HEALTH:
			packet = new Packet05Health(data);
			getPlayer(((Packet05Health) packet).getName()).setHealth(((Packet05Health) packet).getHealth());
			break;
		case KILL:
			packet = new Packet06Kill(data);
			if (getPlayer(((Packet06Kill) packet).getMurderer()) != null
					&& !((Packet06Kill) packet).getMurderer().equalsIgnoreCase(((Packet06Kill) packet).getVictim()))
				getPlayer(((Packet06Kill) packet).getMurderer())
						.setKills(getPlayer(((Packet06Kill) packet).getMurderer()).getKills() + 1);
			getPlayer(((Packet06Kill) packet).getVictim())
					.setDeaths(getPlayer(((Packet06Kill) packet).getVictim()).getDeaths() + 1);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] "
					+ ((Packet06Kill) packet).getMurderer() + " has killed " + ((Packet06Kill) packet).getVictim());
			if (game instanceof GameDeathmatchMP) {
				if (getPlayer(((Packet06Kill) packet).getMurderer()) != null
						&& (((GameDeathmatchMP) game).getFirstPlaceName() == null || ((GameDeathmatchMP) game)
								.getFirstPlaceKills() < getPlayer(((Packet06Kill) packet).getMurderer()).getKills())) {
					((GameDeathmatchMP) game)
							.setFirstPlaceKills(getPlayer(((Packet06Kill) packet).getMurderer()).getKills());
					((GameDeathmatchMP) game).setFirstPlaceName(((Packet06Kill) packet).getMurderer());
				}
			}
			break;
		case ENDGAME:
			packet = new Packet07Endgame(data);
			System.out.println(((Packet07Endgame) packet).getWinner() + " is the winner with a score of "
					+ ((Packet07Endgame) packet).getScore() + "!");
			game.setVisible(false);
			game.getFrame().remove(game);
			game.getTimer().cancel();
			game.setRunning(false);
			if (game.getSocketServer() != null) {
				game.getSocketServer().getSocket().close();
				game.getSocketServer().stop();
				game.getFrame().add(new GameLobbyMenu(game.getFrame(), game.getUserName()));
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				game.getFrame().add(new GameLobbyMenu(game.getFrame(), game.getSocketClient().getIP().getHostAddress(),
						game.getUserName()));
			}
			game.getFrame().repaint();
			getSocket().close();
			stop();
			break;
		case UPDATEUSERINFO:
			packet = new Packet10UpdateUserInfo(data);
			handleUpdateUserInfo((Packet10UpdateUserInfo) packet, address, port);
			break;
		case GAMELOBBYTIME:
			packet = new Packet11GameLobbyTime(data);
			game.setLobbyTime(((Packet11GameLobbyTime) packet).getSeconds());
			break;
		case SENDNEWPROJECTILE:
			packet = new Packet13SendNewProjectile(data);
			if (((Packet13SendNewProjectile) packet).getType().equalsIgnoreCase("arrow")) {
				Arrow arrow = new Arrow(getPlayer(((Packet13SendNewProjectile) packet).getName()),
						((Packet13SendNewProjectile) packet).getId(),
						((Packet13SendNewProjectile) packet).getxPosition(),
						((Packet13SendNewProjectile) packet).getyPosition(),
						((Packet13SendNewProjectile) packet).getrAngle());
				if (!game.getProjectileIDs().contains(arrow.getId())) {
					game.getProjectiles().add(arrow);
					game.getProjectileIDs().add(((Packet13SendNewProjectile) packet).getId());
				}
			}
			break;
		case UPDATEPROJECTILE:
			packet = new Packet14UpdateProjectile(data);
			try {
				for (int i = 0; i < game.getProjectiles().size(); i++) {
					if (((Packet14UpdateProjectile) packet).getId() == game.getProjectiles().get(i).getId()) {
						game.getProjectiles().get(i).setxPosition(((Packet14UpdateProjectile) packet).getxPosition());
						game.getProjectiles().get(i).setyPosition(((Packet14UpdateProjectile) packet).getyPosition());
						game.getProjectiles().get(i).setrAngle(((Packet14UpdateProjectile) packet).getrAngle());
						break;
					}
				}
			} catch (ConcurrentModificationException e) {
				e.printStackTrace();
			}
			break;
		case REMOVEPROJECTILE:
			packet = new Packet15RemoveProjectile(data);
			try {
				for (int i = 0; i < game.getProjectiles().size(); i++) {
					if (game.getProjectiles().get(i).getId() == ((Packet15RemoveProjectile) packet).getId()) {
						game.getProjectiles().remove(i);
						game.getProjectileIDs().remove((Integer) ((Packet15RemoveProjectile) packet).getId());
						break;
					}
				}
			} catch (ConcurrentModificationException e) {
				e.printStackTrace();
			}
			break;
		case RESPAWNPLAYER:
			packet = new Packet18RespawnPlayer(data);
			getPlayer(((Packet18RespawnPlayer) packet).getName())
					.setxPosition(((Packet18RespawnPlayer) packet).getxPosition());
			getPlayer(((Packet18RespawnPlayer) packet).getName())
					.setyPosition(((Packet18RespawnPlayer) packet).getyPosition());
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

	public void handleLogin(Packet00Login packet, InetAddress address, int port) {
		System.out.println(
				"[" + address.getHostAddress() + ":" + port + "] " + packet.getName() + " has joined the game...");
		Player player = null;
		if (packet.getRace().equalsIgnoreCase("human"))
			player = new Human(game, packet.getName(), address, port);
		else if (packet.getRace().equalsIgnoreCase("cyborg") && packet.getColor().equalsIgnoreCase(" "))
			player = new Cyborg(game, packet.getName(), -1, address, port);
		else if (packet.getRace().equalsIgnoreCase("cyborg") && !packet.getColor().equalsIgnoreCase(" "))
			player = new Cyborg(game, packet.getColor(), packet.getName(), address, port);
		else
			player = new Human(game, packet.getName(), address, port);
		if (packet.getWeapon().equalsIgnoreCase("sword"))
			player.setInHand(new Sword(player));
		else if (packet.getWeapon().equalsIgnoreCase("bow"))
			player.setInHand(new Bow(player));
		else
			player.setInHand(new Sword(player));
		game.getPlayers().add(player);
	}

	public void handleUpdateUserInfo(Packet10UpdateUserInfo packet, InetAddress address, int port) {
		Player player = null;
		if (packet.getRace().equalsIgnoreCase("human"))
			player = new Human(game, packet.getName(), address, port);
		else if (packet.getRace().equalsIgnoreCase("cyborg") && packet.getColor().equalsIgnoreCase(" "))
			player = new Cyborg(game, packet.getName(), -1, address, port);
		else if (packet.getRace().equalsIgnoreCase("cyborg") && !packet.getColor().equalsIgnoreCase(" "))
			player = new Cyborg(game, packet.getColor(), packet.getName(), address, port);
		else
			player = new Human(game, packet.getName(), address, port);
		Weapon melee = null;
		if (packet.getMeleeWeapon().equalsIgnoreCase("sword")) {
			melee = new Sword(player);
			player.setMelee(melee);
		} else {
			melee = new Sword(player);
			player.setMelee(melee);
		}
		player.setInHand(melee);
		if (packet.getRangedWeapon().equalsIgnoreCase("bow"))
			player.setRanged(new Bow(player));
		else
			player.setRanged(new Bow(player));
		for (int i = 0; i < game.getPlayers().size(); i++)
			if (game.getPlayers().get(i).getName().equalsIgnoreCase(player.getName()))
				game.getPlayers().set(i, player);
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
