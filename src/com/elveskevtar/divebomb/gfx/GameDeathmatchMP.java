package com.elveskevtar.divebomb.gfx;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ConcurrentModificationException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.elveskevtar.divebomb.maps.Map;
import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;
import com.elveskevtar.divebomb.net.packets.Packet00Login;
import com.elveskevtar.divebomb.net.packets.Packet01Disconnect;
import com.elveskevtar.divebomb.net.packets.Packet03Move;
import com.elveskevtar.divebomb.net.packets.Packet05Health;
import com.elveskevtar.divebomb.net.packets.Packet07Endgame;
import com.elveskevtar.divebomb.net.packets.Packet14UpdateProjectile;
import com.elveskevtar.divebomb.race.Player;
import com.elveskevtar.divebomb.weapons.Bow;
import com.elveskevtar.divebomb.weapons.Projectile;
import com.elveskevtar.divebomb.weapons.ProjectileShooter;
import com.elveskevtar.divebomb.weapons.Sword;

/**
 * A subclass of Game that creates a deathmatch style multiplayer game.
 * Initializes three superconstructors for the various possible game
 * combinations: client + server, client only, and server only. This is mainly
 * only necessary for creating the proper datagram sockets; however, server side
 * programs must also have a specified map to create the game and eventually
 * send to all clients when the game starts. The
 * <code>public void paint(Graphics g)</code> and the
 * <code>public void setTimers()</code> methods are overriden, the former for
 * GUI purposes and the latter for more specific TimerTasks that deal with the
 * sending of packets. However, the 'TimerTasks' initialized in the overriden
 * method are actually just looped threads that sleep a certain amount of
 * milliseconds and are terminated based on the state of the game or the
 * datagram sockets. All of these threads are found as nested classes within the
 * GameDeathmatchMP class.
 * 
 * @author Elveskevtar
 * @since 0.0.1-pre-pre-alpha
 */
public class GameDeathmatchMP extends Game {

	private static final long serialVersionUID = -6543167121461636523L;

	/**
	 * The port number to which the client is connected to or the server is
	 * running on.
	 */
	private int PORT;

	/** The number of kills needed to end the game. */
	private int maxKills;

	/**
	 * Keeps track of the number of kills that the player with the most kills
	 * has.
	 */
	private int firstPlaceKills;

	/** Keeps track of the name of the player with the most kills. */
	private String firstPlaceName;

	/**
	 * The constructor that deals with the client and server bundle. Requires
	 * parameters for Map object information, the JFrame in which the Game
	 * JComponenet will be held, the user's name, and the port to which the
	 * server will be running on. Sets the game parameters, creates the datagram
	 * socket objects for both the server and the client, then sends a login
	 * packet from the client to the localhost server to initiate a connection.
	 * 
	 * @param graphicsMapName
	 *            The name of the graphics map that will be used as a parameter
	 *            when creating the Game's Map object.
	 * @param collisionMapName
	 *            The name of the collision map that will be used as a parameter
	 *            when creating the Game's Map object.
	 * @param id
	 *            The identification number that will be used as a parameter
	 *            when creating the Game's Map object.
	 * @param frame
	 *            The JFrame object in which the Game component will be added.
	 * @param username
	 *            The user's name which will be passed to
	 *            <code>setUserName(String username)</code> for use as
	 *            identification.
	 * @param port
	 *            The port number to which the datagram socket for the server
	 *            will bind to.
	 * @see com.elveskevtar.divebomb.gfx.Game
	 */
	public GameDeathmatchMP(String graphicsMapName, String collisionMapName, int id, JFrame frame, String username,
			int port) {
		/* calls the superconstructor for a generic client based Game */
		super(01, frame);
		this.setPlayerSize(2);
		this.setMaxKills(3);
		this.setPORT(port);
		this.setGraphicsMap(new Map(graphicsMapName, collisionMapName, id));
		this.setSocketServer(new GameServer(this, port));
		this.getSocketServer().start();
		this.setServerIP("localhost");
		this.setSocketClient(new GameClient(this, getServerIP(), PORT));
		this.getSocketClient().start();
		this.setUserName(username);
		String weapon = " ";
		if (getUser().getInHand() instanceof Sword)
			weapon = "sword";
		if (getUser().getInHand() instanceof Bow)
			weapon = "bow";
		Packet00Login packet = new Packet00Login(getUserName(), getUserRace(), getUserColor(), weapon);
		try {
			getSocketClient().setIP(InetAddress.getByName(getServerIP()));
			Thread.sleep(200);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		packet.writeData(getSocketClient());
	}

	/* server-side only constructor */
	public GameDeathmatchMP(String graphicsMapName, String collisionMapName, int id, int port) {
		super(01);
		this.setPlayerSize(2);
		this.setMaxKills(3);
		this.setPORT(port);
		this.setGraphicsMap(new Map(graphicsMapName, collisionMapName, id));
		this.setSocketServer(new GameServer(this, port));
		this.getSocketServer().start();
		this.setServerIP("localhost");
	}

	/* client-side only constructor */
	public GameDeathmatchMP(String ip, JFrame frame, String username, int port) {
		super(01, frame);
		this.setPORT(port);
		this.setPlayerSize(2);
		this.setMaxKills(3);
		this.setServerIP(ip);
		this.setSocketClient(new GameClient(this, getServerIP(), PORT));
		this.getSocketClient().start();
		this.setUserName(username);
		String weapon = " ";
		if (getUser().getInHand() instanceof Sword)
			weapon = "sword";
		if (getUser().getInHand() instanceof Bow)
			weapon = "bow";
		Packet00Login packet = new Packet00Login(getUserName(), getUserRace(), getUserColor(), weapon);
		try {
			getSocketClient().setIP(InetAddress.getByName(ip));
			Thread.sleep(200);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		packet.writeData(getSocketClient());
	}

	/* calls super method and paints gamemode specific GUI over it */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.translate((-getWidth() * (0.5 * getZoom() - 0.5) * (1.0 / getZoom())) * -1,
				(-getHeight() * (0.5 * getZoom() - 0.5) * (1.0 / getZoom())) * -1);
		g2d.setFont(new Font("Livewired", Font.PLAIN, (int) (20 / getZoom())));
		g2d.drawString("Health: " + getUser().getHealth(), 0, g2d.getFont().getSize() * 3 / 4);
		g2d.drawString("Stamina: " + getUser().getStamina(), 0, g2d.getFont().getSize() * 15 / 8);
		g2d.drawString("Kills: " + getUser().getKills(), 0, g2d.getFont().getSize() * 3);
		g2d.drawString("Deaths: " + getUser().getDeaths(), 0, g2d.getFont().getSize() * 33 / 8);
		g2d.drawString("Ping Latency: " + Math.min((int) (getUser().getLatency() * Math.pow(10, -6)), 999), 0,
				g2d.getFont().getSize() * 21 / 4);
		;
	}

	/* calls super method and starts gamemode specific timers as well */
	@Override
	public void setTimers() {
		super.setTimers();
		new Thread(new CheckForPingDrops()).start();
		if (getSocketClient() != null) {
			new Thread(new SendMovePacket()).start();
		}
		if (getSocketServer() != null) {
			new Thread(new SendHealthPacket()).start();
			new Thread(new CheckForEndGame()).start();
			new Thread(new UpdateProjectiles()).start();
		}
	}

	/* standard get/set methods */
	public int getFirstPlaceKills() {
		return firstPlaceKills;
	}

	public void setFirstPlaceKills(int firstPlaceKills) {
		this.firstPlaceKills = firstPlaceKills;
	}

	public String getFirstPlaceName() {
		return firstPlaceName;
	}

	public void setFirstPlaceName(String firstPlaceName) {
		this.firstPlaceName = firstPlaceName;
	}

	public int getMaxKills() {
		return maxKills;
	}

	public void setMaxKills(int maxKills) {
		this.maxKills = maxKills;
	}

	public int getPORT() {
		return PORT;
	}

	public void setPORT(int pORT) {
		PORT = pORT;
	}

	/* server-side thread; sends projectile info packets to all clients */
	private class UpdateProjectiles extends Thread {

		@Override
		public void run() {
			while (isRunning() && getSocketServer().isServerRunning()) {
				try {
					for (Projectile p : getProjectiles()) {
						Packet14UpdateProjectile packet = new Packet14UpdateProjectile(p.getxPosition(),
								p.getyPosition(), p.getrAngle(), p.getId());
						packet.writeData(getSocketServer());
					}
				} catch (ConcurrentModificationException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * client-side thread; sends move packets to the server to be redistributed
	 * to every other client; sends this info to all clients
	 */
	private class SendMovePacket extends Thread {

		@Override
		public void run() {
			while (isRunning() && getSocketClient().isClientRunning()) {
				double rAngle = 0;
				if (getUser().getInHand() instanceof ProjectileShooter)
					rAngle = ((ProjectileShooter) getUser().getInHand()).getrAngle();
				String weapon = "";
				if (getUser().getInHand().getName().equalsIgnoreCase(getUser().getMelee().getName()))
					weapon = getUserMelee();
				else
					weapon = getUserRanged();
				Packet03Move packet = new Packet03Move(getUser().getName(), getUser().getxPosition(),
						getUser().getyPosition(), getUser().getVeloX(), getUser().getVeloY(), rAngle,
						getUser().isWalking(), getUser().isRunning(), getUser().isMovingRight(),
						getUser().isFacingRight(), weapon);
				packet.writeData(getSocketClient());
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * server-side thread; only server handles health values (since only server
	 * handles attacks after attack requests are sent to the server)
	 */
	private class SendHealthPacket extends Thread {

		@Override
		public void run() {
			while (isRunning() && getSocketServer().isServerRunning()) {
				try {
					for (Player player : getSocketServer().connectedPlayers) {
						Packet05Health packet = new Packet05Health(player.getName(), player.getHealth());
						packet.writeData(getSocketServer());
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (ConcurrentModificationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/* server-side thread; checks to see if the game conditions have been met */
	private class CheckForEndGame extends Thread {

		@Override
		public void run() {
			while (isRunning() && getSocketServer().isServerRunning()) {
				if (firstPlaceKills >= maxKills) {
					Packet07Endgame packet = new Packet07Endgame(firstPlaceName, firstPlaceKills);
					packet.writeData(getSocketServer());
					getSocketServer().setServerRunning(false);
					getSocketServer().getSocket().close();
					if (getSocketClient() == null) {
						getTimer().cancel();
						setRunning(false);
						new GameDeathmatchMP("res/img/Map.png", "res/img/CollisionMap.png", 0, getPORT());
					}
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * checks for giant lag spikes and disconnects; has different uses for both
	 * server and client side
	 */
	private class CheckForPingDrops extends Thread {

		@Override
		public void run() {
			while (isRunning()) {
				if (getSocketServer() == null) {
					getUser().setLatency(System.nanoTime() - getUser().getOldTimeStamp());
					if (Math.min((int) (getUser().getLatency() * Math.pow(10, -6)), 999) == 999
							&& getUser().getOldTimeStamp() != 0)
						setState(2);
					else if (GameDeathmatchMP.this.getState() == 2
							&& Math.min((int) (getUser().getLatency() * Math.pow(10, -6)), 999) != 999)
						setState(0);
					if (((int) (getUser().getLatency() * Math.pow(10, -6)) >= 5000)
							&& getUser().getOldTimeStamp() != 0) {
						setVisible(false);
						getFrame().remove(GameDeathmatchMP.this);
						getTimer().cancel();
						setRunning(false);
						getSocketClient().getSocket().close();
						getSocketClient().setClientRunning(false);
						getFrame().add(new StartMenu(getFrame()));
						getFrame().repaint();
						JOptionPane.showMessageDialog(getFrame(),
								"You have been unexpectedly disconnected from the server", "Server Disconnection",
								JOptionPane.INFORMATION_MESSAGE);
						getFrame().toFront();
					}
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					try {
						for (Player p : getSocketServer().connectedPlayers) {
							p.setLatency(System.nanoTime() - p.getOldTimeStamp());
							if (((int) (p.getLatency() * Math.pow(10, -6)) >= 5000) && p.getOldTimeStamp() != 0) {
								Packet01Disconnect packet = new Packet01Disconnect(p.getName());
								packet.writeData(getSocketServer());
								System.out
										.println("[" + getSocketServer().getSocket().getLocalAddress().getHostAddress()
												+ ":" + getSocketServer().getSocket().getLocalPort() + "] "
												+ p.getName() + " has disconnected...");
								getSocketServer().connectedPlayers.remove(p);
								getPlayers().remove(p);
							}
						}
						Thread.sleep(250);
					} catch (ConcurrentModificationException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}