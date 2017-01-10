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
import com.elveskevtar.divebomb.weapons.Projectile;
import com.elveskevtar.divebomb.weapons.ProjectileShooter;

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

		/* sets the user's name */
		this.setUserName(username);

		/* sets the maximum player size for the game (default: 2) */
		this.setPlayerSize(2);

		/* sets the number of kills needed to end the game */
		this.setMaxKills(3);

		/* sets the ip and port the server will be hosted on */
		this.setServerIP("localhost");
		this.setPORT(port);

		/* gets the Map object in the enumeration with the parameters given */
		this.setGraphicsMap(Map.getValue(graphicsMapName, collisionMapName, id));

		/*
		 * creates a new GameServer object which will be stored in Game's
		 * socketServer variable; then, starts the thread
		 */
		this.setSocketServer(new GameServer(this, port));
		this.getSocketServer().start();

		/*
		 * creates a new GameClient object which will be stored in Game's
		 * socketClient variable; then, starts the thread
		 */
		this.setSocketClient(new GameClient(this, getServerIP(), PORT));
		this.getSocketClient().start();

		/* trys to set the socketClient's IP to the serverIP */
		try {
			this.getSocketClient().setIP(InetAddress.getByName(getServerIP()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		/* creates and sends a login packet from the client to the server */
		new Packet00Login(getUserName()).writeData(getSocketClient());
	}

	/**
	 * The constructor that deals with the server only program. Requires
	 * parameters for Map object information and the port to which the server
	 * will bind. Sets the game parameters, creates the datagram socket server
	 * object only (not the client).
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
	 * @param port
	 *            The port number to which the datagram socket for the server
	 *            will bind to.
	 * @see com.elveskevtar.divebomb.gfx.Game
	 */
	public GameDeathmatchMP(String graphicsMapName, String collisionMapName, int id, int port) {
		/* calls the superconstructor of Game that deals with server only */
		super(01);

		/* sets the maximum player size for the game (default: 2) */
		this.setPlayerSize(2);

		/* sets the number of kills needed to end the game */
		this.setMaxKills(3);

		/* sets the ip and port the server will be hosted on */
		this.setServerIP("localhost");
		this.setPORT(port);

		/* gets the Map object in the enumeration with the parameters given */
		this.setGraphicsMap(Map.getValue(graphicsMapName, collisionMapName, id));

		/*
		 * creates a new GameServer object which will be stored in Game's
		 * socketServer variable; then, starts the thread
		 */
		this.setSocketServer(new GameServer(this, port));
		this.getSocketServer().start();
	}

	/**
	 * The constructor that deals with the client only program. Requires
	 * parameters for the ip that the server is hosted on, the JFrame in which
	 * the Game JComponenet will be held, the user's name, and the port to which
	 * the server will be running on. Sets the game parameters, creates the
	 * datagram socket client object only (not the server).
	 * 
	 * @param ip
	 *            The IP on which the server is hosted.
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
	public GameDeathmatchMP(String ip, JFrame frame, String username, int port) {
		/* calls the superconstructor for a generic client based Game */
		super(01, frame);

		/* sets the user's name */
		this.setUserName(username);

		/* sets the maximum player size for the game (default: 2) */
		this.setPlayerSize(2);

		/* sets the number of kills needed to end the game */
		this.setMaxKills(3);

		/* sets the ip and port the server will be hosted on */
		this.setServerIP(ip);
		this.setPORT(port);

		/*
		 * creates a new GameClient object which will be stored in Game's
		 * socketClient variable; then, starts the thread
		 */
		this.setSocketClient(new GameClient(this, getServerIP(), PORT));
		this.getSocketClient().start();

		/* trys to set the socketClient's IP to the serverIP */
		try {
			getSocketClient().setIP(InetAddress.getByName(ip));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		/* creates and sends a login packet from the client to the server */
		new Packet00Login(getUserName()).writeData(getSocketClient());
	}

	/**
	 * Calls the super method which paints the general game features then paints
	 * the gamemode specific GUI.
	 * 
	 * @param g
	 *            The Graphics object which paints the game elements.
	 */
	@Override
	public void paint(Graphics g) {
		/* calls the super method that paints the general game features */
		super.paint(g);

		/* turns the Graphics object into a Graphics2D object */
		Graphics2D g2d = (Graphics2D) g;

		/* sets the text antialiasing hints to the Graphics2D object */
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		/* sets the font for the Graphics2D object based on zoom */
		g2d.setFont(new Font("Livewired", Font.PLAIN, (int) (20 / getZoom())));

		/*
		 * 'untranslates' the Graphics2D object that was translated in the super
		 * method based on zoom
		 */
		g2d.translate((getWidth() * (0.5 * getZoom() - 0.5) * (1.0 / getZoom())),
				(getHeight() * (0.5 * getZoom() - 0.5) * (1.0 / getZoom())));

		/*
		 * draw the health, stamina, kills, deaths, and ping in the top right
		 * corner
		 */
		g2d.drawString("Health: " + Math.round(getUser().getHealth() * 10.0) / 10.0, 0,
				g2d.getFont().getSize() * 3 / 4);
		g2d.drawString("Stamina: " + Math.round(getUser().getStamina() * 10.0) / 10.0, 0,
				g2d.getFont().getSize() * 15 / 8);
		g2d.drawString("Kills: " + getUser().getKills(), 0, g2d.getFont().getSize() * 3);
		g2d.drawString("Deaths: " + getUser().getDeaths(), 0, g2d.getFont().getSize() * 33 / 8);
		g2d.drawString("Ping Latency: " + Math.min((int) (getUser().getLatency() * Math.pow(10, -6)), 999), 0,
				g2d.getFont().getSize() * 21 / 4);
	}

	/**
	 * Calls the super method to set generic Game TimerTasks, then starts new
	 * gamemode specific threads based on client side, server side, or both.
	 */
	@Override
	public void setTimers() {
		/* calls the super method that starts the generic Game TimerTasks */
		super.setTimers();

		/* new thread for either three combinations of client and server */
		new Thread(new CheckForPingDrops()).start();

		/* move packet thread for clients */
		if (getSocketClient() != null) {
			new Thread(new SendMovePacket()).start();
		}

		/*
		 * health packet, check for end game, and projectile packet threads for
		 * servers
		 */
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

	/**
	 * A server side thread that sends projectile update packets for each 'live'
	 * projectile. Only runs while the Game object is in the run state and the
	 * socketServer is also in the run state. Iterates every <code>SPEED</code>
	 * milliseconds.
	 * 
	 * @since 0.0.1-pre-pre-alpha
	 * @see com.elveskevtar.divebomb.net.packets.Packet14UpdateProjectile
	 */
	private class UpdateProjectiles extends Thread {

		@Override
		public void run() {
			/* while the game and the socketServer are in the run state */
			while (isRunning() && getSocketServer().isServerRunning()) {
				/* cycles through the Projectiles in the ArrayList */
				for (Projectile p : getProjectiles()) {
					/* creates and sends update projectile packets */
					new Packet14UpdateProjectile(p.getxPosition(), p.getyPosition(), p.getrAngle(), p.getId())
							.writeData(getSocketServer());
				}

				/* sleeps for SPEED milliseconds */
				try {
					Thread.sleep(getSPEED());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * A client side thread that sends move packets to the server which then
	 * redistributes the information to the rest of the clients. This allows all
	 * clients and the server to know the positions, velocities, directions, and
	 * weapon in hand of every Player. Iterates every <code>SPEED</code>
	 * milliseconds.
	 * 
	 * @since 0.0.1-pre-pre-alpha
	 * @see com.elveskevtar.divebomb.net.packets.Packet03Move
	 */
	private class SendMovePacket extends Thread {

		@Override
		public void run() {
			/* while the game and the socketClient are in the run state */
			while (isRunning() && getSocketClient().isClientRunning()) {
				/*
				 * initializes the rAngle and only sets it if the Player's in
				 * hand weapon is a ProjectileShooter type
				 */
				double rAngle = 0;
				if (getUser().getInHand() instanceof ProjectileShooter)
					rAngle = ((ProjectileShooter) getUser().getInHand()).getrAngle();

				/*
				 * initializes the weapon and sets it to either the name of the
				 * user's melee or the user's ranged based on which one is in
				 * the user's hand
				 */
				String weapon = "";
				if (getUser().getInHand().getName().equals(getUser().getMelee().getName()))
					weapon = getUserMelee();
				else
					weapon = getUserRanged();

				/* creates and sends a move packet */
				new Packet03Move(getUser().getName(), getUser().getxPosition(), getUser().getyPosition(),
						getUser().getVeloX(), getUser().getVeloY(), rAngle, getUser().isWalking(),
						getUser().isRunning(), getUser().isMovingRight(), getUser().isFacingRight(), weapon)
								.writeData(getSocketClient());

				/* sleep for SPEED milliseconds */
				try {
					Thread.sleep(getSPEED());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * A server side thread that sends health packets for each player. Only runs
	 * while the Game object is in the run state and the socketServer is also in
	 * the run state. Iterates every <code>100</code> milliseconds.
	 * 
	 * @since 0.0.1-pre-pre-alpha
	 * @see com.elveskevtar.divebomb.net.packets.Packet05Health
	 */
	private class SendHealthPacket extends Thread {

		@Override
		public void run() {
			/* while the game and the socketClient are in the run state */
			while (isRunning() && getSocketServer().isServerRunning()) {
				/* cycles through the Players in the ArrayList */
				for (Player player : getSocketServer().connectedPlayers) {
					/* creates and sends a health packet */
					new Packet05Health(player.getName(), player.getHealth()).writeData(getSocketServer());

					/* sleeps for 100 milliseconds */
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * A server side thread; checks to see if the game conditions have been met.
	 * Then, sends an end game packet to all Players and starts a new game if a
	 * client is present. Iterates every <code>100</code> milliseconds.
	 * 
	 * @since 0.0.1-pre-pre-alpha
	 * @see com.elveskevtar.divebomb.net.packets.Packet07Endgame
	 */
	private class CheckForEndGame extends Thread {

		@Override
		public void run() {
			/* while the game and socketServer are in the run state */
			while (isRunning() && getSocketServer().isServerRunning()) {
				/* if the first place kills is greater than the max kills */
				if (firstPlaceKills >= maxKills) {
					/* creates an end game packet and sends it */
					new Packet07Endgame(firstPlaceName, firstPlaceKills).writeData(getSocketServer());

					/* stop the socketServer and close it */
					getSocketServer().setServerRunning(false);
					getSocketServer().getSocket().close();

					/* if there is a socketClient */
					if (getSocketClient() == null) {
						/* stop the game and start a new one */
						getTimer().cancel();
						setRunning(false);
						new GameDeathmatchMP(Map.TESTMAP.getMapPath(), Map.TESTMAP.getCollisionMapPath(),
								Map.TESTMAP.getId(), getPORT());
					}
				}

				/* sleeps for 100 milliseconds */
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * A server and client side thread that checks for ping drops or a faulty
	 * connection. For the server, it checks the ping latency of all of the
	 * connected Players. If the ping latency is too high, it removes the Player
	 * from the game and sends packets to all other clients to inform them. For
	 * the client, it disconnects itself if the ping latency is too high. It
	 * iterates every <code>250</code> milliseconds.
	 * 
	 * @since 0.0.1-pre-pre-alpha
	 * @see com.elveskevtar.divebomb.net.packets.Packet01Disconnect
	 */
	private class CheckForPingDrops extends Thread {

		@Override
		public void run() {
			/*
			 * while the game, and either the server or client are in the run
			 * state
			 */
			while (isRunning() && (getSocketServer().isServerRunning() || getSocketClient().isClientRunning())) {
				/* if a socket server is not present */
				if (getSocketServer() == null) {
					/* updates the user's ping latency */
					getUser().setLatency(System.nanoTime() - getUser().getOldTimeStamp());

					/* switch between states based on the ping latency */
					if (Math.min((int) (getUser().getLatency() * Math.pow(10, -6)), 999) == 999
							&& getUser().getOldTimeStamp() != 0)
						setState(2);
					else if (GameDeathmatchMP.this.getState() == 2
							&& Math.min((int) (getUser().getLatency() * Math.pow(10, -6)), 999) != 999)
						setState(0);

					/* if the latency is too high */
					if (((int) (getUser().getLatency() * Math.pow(10, -6)) >= 5000)
							&& getUser().getOldTimeStamp() != 0) {
						/* stop the game in every sense */
						setVisible(false);
						getFrame().remove(GameDeathmatchMP.this);
						getTimer().cancel();
						setRunning(false);

						/* stop the socketClient */
						getSocketClient().getSocket().close();
						getSocketClient().setClientRunning(false);

						/* take the user to the start menu */
						getFrame().add(new StartMenu(getFrame()));
						getFrame().repaint();

						/*
						 * display an info message and set the frame to the
						 * front
						 */
						JOptionPane.showMessageDialog(getFrame(),
								"You have been unexpectedly disconnected from the server", "Server Disconnection",
								JOptionPane.INFORMATION_MESSAGE);
						getFrame().toFront();
					}

					/* sleep for 250 milliseconds */
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					/* if a socket server is present */
					try {
						/* cycle through the connectedPlayers */
						for (Player p : getSocketServer().connectedPlayers) {
							/* update the latency of each player */
							p.setLatency(System.nanoTime() - p.getOldTimeStamp());

							/* if the latency is too high */
							if (((int) (p.getLatency() * Math.pow(10, -6)) >= 5000) && p.getOldTimeStamp() != 0) {
								/* send a disconnect packet for that Player */
								new Packet01Disconnect(p.getName()).writeData(getSocketServer());

								System.out
										.println("[" + getSocketServer().getSocket().getLocalAddress().getHostAddress()
												+ ":" + getSocketServer().getSocket().getLocalPort() + "] "
												+ p.getName() + " has disconnected...");

								/*
								 * remove Player from connectedPlayers and
								 * players
								 */
								getSocketServer().connectedPlayers.remove(p);
								getPlayers().remove(p);
							}
						}

						/* sleep for 250 milliseconds */
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