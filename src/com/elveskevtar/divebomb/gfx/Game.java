package com.elveskevtar.divebomb.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.elveskevtar.divebomb.maps.Map;
import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;
import com.elveskevtar.divebomb.net.packets.Packet05Health;
import com.elveskevtar.divebomb.net.packets.Packet15RemoveProjectile;
import com.elveskevtar.divebomb.net.packets.Packet16Suicide;
import com.elveskevtar.divebomb.net.packets.Packet17Respawn;
import com.elveskevtar.divebomb.race.Cyborg;
import com.elveskevtar.divebomb.race.Human;
import com.elveskevtar.divebomb.race.Player;
import com.elveskevtar.divebomb.weapons.Bow;
import com.elveskevtar.divebomb.weapons.Projectile;
import com.elveskevtar.divebomb.weapons.ProjectileShooter;
import com.elveskevtar.divebomb.weapons.Sword;

/**
 * Abstract class that is a JPanel object and serves as the general game
 * structure itself. A game type object can be inherited to serve as either a
 * more specialized singleplayer or multiplayer gamemode. The abstract version
 * of the game contains an enumeration of all of the game types as well as many
 * generic threads that should be used for all games, such as Repaint. Repaint
 * is a subclass that serves as a TimerTask object which runs as a separate
 * thread every certain amount of milliseconds. This is how many of the
 * subthreads are structured within the Game class and all of its
 * subclasses.<br>
 * <br>
 * Contains constructors for both client-side Game objects (whether the client
 * is also hosting a server or not) and server-side only Game objects.<br>
 * <br>
 * Implements KeyListener, MouseListener, MouseMotionListener, and
 * MouseWheelListener to track all of the necessary inputs that makes the game
 * interactive.<br>
 * <br>
 * Overall, this class contains most of the core code that makes everything run.
 * Changes to this class are less frequent and usually more for cosmetic or
 * efficiency purposes. If changes need to be made to a specific gamemode, those
 * typically occur in that specific gamemode's class file.
 * 
 * @author Elveskevtar
 * @since 0.0.1-pre-pre-alpha
 */
public abstract class Game extends JPanel
		implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	/**
	 * An enumeration of the gamemodes that currently exist within the game. For
	 * number between 0 and 7, a 0 is appended to the beggining of the integer
	 * literal. The format of having 0 as a prefix to an integer makes that
	 * integer be treated as though it were an octal number (a number with a
	 * base of 8). This is why both 08 and 09 cannot be used, since neither of
	 * them are octal numbers.<br>
	 * <br>
	 * This same convention is used when enumerating packet types and it is for
	 * the same reason we use it here. When sending packets with a byte array of
	 * information, the packet can be easier to process if the number that is
	 * expected (whether it be packet type or gamemode type) always takes up the
	 * same amount of space. By using 01, 02, 03, and so on, we can ensure that
	 * this is the case even when the number of gamemodes reaches the double
	 * digits.
	 * 
	 * @author Elveskevtar
	 * @since 0.0.1-pre-pre-alpha
	 * @see com.elveskevtar.divebomb.net.packets.Packet
	 */
	public static enum GameTypes {
		/*
		 * list of gamemode types assigned to names and given gameID's in order
		 * to call the private constructor
		 */
		DEATHMATCH(00), DEATHMATCHMP(01);

		/** The game ID number which indicates the gamemode type. */
		private int gameID;

		/**
		 * The private constructor for the GameTypes enumeration. Like a normal
		 * enumeration is only called by the declarations of each gamemode type.
		 * 
		 * @param gameID
		 *            A two digit integer literal that represents the game ID
		 *            number. Is associated by a corresponding GameType object.
		 */
		private GameTypes(int gameID) {
			this.setID(gameID);
		}

		public int getID() {
			return gameID;
		}

		public void setID(int ID) {
			this.gameID = ID;
		}
	}

	private static final long serialVersionUID = -3147254673513651984L;

	/** Corresponds to the RGB value of 7, 192, 44. Used primarily for GUI. */
	private static final Color NAMETAG_GREEN = new Color(7, 192, 44);

	/**
	 * Corresponds to the RGBA value of 32, 32, 32, 100. Used primarily for game
	 * states that would still like the game painted in the background.
	 */
	private static final Color PAUSE_OVERLAY = new Color(32, 32, 32, 100);

	/** The number of milliseconds between repitions of the TimerTasks. */
	private int speed = 16;

	/**
	 * The paint method calls various paint submethods based on this variable.
	 * Here is the list of states based on its value:
	 * <ul>
	 * <li>If state = 0, then the game itself will be painted</li>
	 * <li>If state = 1, then the puase menu will be painted over the game</li>
	 * <li>If state = 2, then the reconnect screen will be painted over the
	 * game</li>
	 * </ul>
	 */
	private int state;

	/**
	 * The gamemode ID number based on the gamemode type is stored in this
	 * variable on the call of the constructor. This is all based on the
	 * GameTypes enumeration above. For convenience's sake, here is the list of
	 * gamemodes based on ID value:
	 * <ul>
	 * <li>If gameID = 0, then the gamemode is deathmatch</li>
	 * <li>If gameID = 1, then the gamemode is multiplayer deathmatch</li>
	 * </ul>
	 * 
	 * @see com.elveskevtar.divebomb.gfx.Game.GameTypes
	 */
	private int gameID;

	/**
	 * The total number of players needed for a full game of a specific
	 * gamemode. Mostly used for multiplayer games to determine how many players
	 * are needed to start the game. Each multiplayer gamemode has a default
	 * value for playerSize that can be changed for custom games.
	 */
	private int playerSize;

	/**
	 * The game lobby time. Default of 0 for singleplayer gamemodes and default
	 * of -1 for multiplayer gamemodes. Used to count down the time remaining
	 * until the game starts.
	 * 
	 * @see com.elveskevtar.divebomb.gfx.GameLobbyMenu
	 */
	private int lobbyTime;

	/** Integer value for how graphically zoomed things should be painted. */
	private double zoom = 2.0;

	/**
	 * An ArrayList that contains all of the active Projectile objects within
	 * the game. Uses a CopyOnWriteArrayList which is a thread safe alternative
	 * to regular ArrayLists. This means that while you're modifying an element
	 * of the list in one thread, you could be iterating through the elements in
	 * another thread and no ConcurrentModificationException would be thrown.
	 * 
	 * @see java.util.ConcurrentModificationException
	 */
	private CopyOnWriteArrayList<Projectile> projectiles;

	/**
	 * An ArrayList that contains all of the collision rectangles loaded through
	 * the collision map file.
	 */
	private ArrayList<Rectangle> collisionRecs;

	/**
	 * An ArrayList that contains the ID numbers corresponding with each of the
	 * Projectile objects in the CopyOnWriteArrayList.
	 */
	private ArrayList<Integer> projectileIDs;

	/**
	 * An ArrayList that contains the active players withing the Game object.
	 */
	private ArrayList<Player> players;

	/**
	 * An ArrayList of Integers that contains the integer equivalents of the
	 * keys that are being pressed pressed at any one moment in time while the
	 * game is running.
	 */
	private ArrayList<Integer> keys;

	/**
	 * The BufferedImage object corresponding to the graphical map that should
	 * be painted for the game.
	 */
	private BufferedImage map;

	/**
	 * The BufferedImage object corresponding to the collision map which is
	 * never painted but used to determine what elements should be placed within
	 * the collisionRecs ArrayList.
	 */
	private BufferedImage collisionMap;

	/**
	 * A GameServer object which handles all of the serverside multiplayer
	 * aspects of an MP gamemode. Is never initialized in SP gamemodes.
	 */
	private GameServer socketServer;

	/**
	 * A GameClient object which handles all of the clientside multiplayer
	 * aspects of an MP gamemode. Is never initialized in SP gamemodes.
	 */
	private GameClient socketClient;

	/**
	 * This variable stores the map of the game in a Map object. The Map object
	 * is an easier way to store the BufferedImages of the collision map and
	 * graphical map, their respective file paths, and the ID number of the map.
	 */
	private Map graphicsMap;

	/**
	 * The JFrame object for the window in which this Game object, as a JPanel,
	 * is painted within.
	 */
	private JFrame frame;

	/** This Timer object controls the TimerTasks that makes the game work. */
	private Timer timer;

	/**
	 * Stores the font object which is initialized and used in the paint
	 * methods.
	 */
	private Font font;

	/** The String object that represents the user's ranged weapon. */
	private String userRanged;

	/** The String object that represents the user's color if applicable. */
	private String userColor;

	/** The String object that represents the user's melee weapon. */
	private String userMelee;

	/** The String object that represents the user's username. */
	private String userName;

	/** The String object that represents the user's race. */
	private String userRace;

	/**
	 * The String object that represents the IP that the user is connected to in
	 * multiplayer games. If the user is also hosting a server (or there is no
	 * user at all), this IP is set to 'localhost'.
	 */
	private String serverIP;

	/**
	 * Represents the Player object for the user. Remains unused for server only
	 * games since their is no game user.
	 */
	private Player user;

	/**
	 * Keeps track of when the game is running. This should be true any point
	 * the game is being painted.
	 */
	private boolean running;

	/**
	 * The general constructor for any Game type object that involves a client.
	 * This can be for SP gamemodes, hosting a MP gamemode with an attached
	 * client, or joining a MP gamemode from a client. Mainly just sets values
	 * for the JPanel component and some default values for the user.
	 * 
	 * @param gameID
	 *            The ID number associated with the game type based on the
	 *            GameType enumeration.
	 * @param frame
	 *            The JFrame object in which the Game component will be added.
	 * @see com.elveskevtar.divebomb.gfx.Game.GameTypes
	 */
	public Game(int gameID, JFrame frame) {
		/* calls the JPanel superconstructor */
		super();

		/* takes into account the insets of the JFrame when setting the size */
		this.setSize(frame.getWidth() - frame.getInsets().left - frame.getInsets().right,
				frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom);

		/* sets this component to be double buffered when painted */
		this.setDoubleBuffered(true);

		/*
		 * sets this component to be focusable and the background set to black
		 */
		this.setFocusable(true);
		this.setBackground(Color.BLACK);

		/* initializes arraylists, sets the gameID, and frame */
		this.setPlayers(new ArrayList<Player>());
		this.setCollisionRecs(new ArrayList<Rectangle>());
		this.setKeys(new ArrayList<Integer>());
		this.setProjectiles(new CopyOnWriteArrayList<Projectile>());
		this.setProjectileIDs(new ArrayList<Integer>());
		this.setGameID(gameID);
		this.setFrame(frame);

		/* default values for user info */
		this.setUserName("Bob");
		this.setUserRace("human");
		this.setUserMelee("sword");
		this.setUserRanged("bow");
		this.setUserColor(" ");

		/* calls the method that updates the user */
		this.updatePlayer();
	}

	/**
	 * The constructor specifically used for servers with no clients involved.
	 * User variables and parameters are not applicable since no client means no
	 * user. The JFrame parameter is not applicable either since servers run on
	 * the console. When there is no client, and only a server, only certain
	 * threads are needed which are specified in different methods with
	 * statement like
	 * <code>if (getSocketClient() == null && getSocketServer() != null)</code>.
	 * 
	 * @param gameID
	 *            The ID number associated with the game type based on the
	 *            GameType enumeration.
	 * @see com.elveskevtar.divebomb.gfx.Game.GameTypes
	 */
	public Game(int gameID) {
		/* calls the JPanel superconstructor */
		super();

		/* initializes arraylists and sets the gameID */
		this.setPlayers(new ArrayList<Player>());
		this.setCollisionRecs(new ArrayList<Rectangle>());
		this.setProjectiles(new CopyOnWriteArrayList<Projectile>());
		this.setProjectileIDs(new ArrayList<Integer>());
		this.setGameID(gameID);
	}

	/**
	 * Updates the user's variables such as race, color, name, melee weapon,
	 * ranged weapon, and the weapon in hand.
	 * 
	 * @see com.elveskevtar.divebomb.race.Player
	 */
	public void updatePlayer() {
		/*
		 * first block of if statements deals with user race and color based on
		 * user variables within the Game object
		 */
		if (userRace.equals("human"))
			user = new Human(this, userName, null, -1);
		else if (userRace.equals("cyborg") && userColor.equals("purple"))
			user = new Cyborg(this, "purple", userName, null, -1);
		else if (userRace.equals("cyborg") && userColor.equals("blue"))
			user = new Cyborg(this, "blue", userName, null, -1);
		else if (userRace.equals("cyborg"))
			user = new Cyborg(this, userName, -1, null, -1);
		else
			user = new Human(this, userName, null, -1);

		/*
		 * second block of if statements deals with weapons based on user
		 * variables within the Game object
		 */
		if (userMelee.equals("sword"))
			user.setMelee(new Sword(user));
		if (userRanged.equals("bow"))
			user.setRanged(new Bow(user));
		user.setInHand(user.getMelee());
	}

	/**
	 * Sets the general timers used for all gamemodes. Takes into account the
	 * presence of socketClients and socketServers.
	 */
	public void setTimers() {
		/*
		 * sets the game to 'run' mode which affects loop threads in subclasses
		 * of Game as well as online gameplay
		 */
		setRunning(true);

		/* initializes the Timer object */
		setTimer(new Timer());

		/* timers set for any Game object with a client */
		if (socketClient != null || (socketClient == null && socketServer == null)) {
			timer.scheduleAtFixedRate(new MovePlayers(), 0, speed);
			timer.scheduleAtFixedRate(new Repaint(), 0, speed);
			timer.scheduleAtFixedRate(new AnimatePlayers(), 0, speed);
			timer.scheduleAtFixedRate(new Input(), 0, speed);
		}

		/* timers set for all gamemodes */
		timer.scheduleAtFixedRate(new Stamina(), 0, speed);
		timer.scheduleAtFixedRate(new PlayerWeapons(), 0, speed);
		timer.scheduleAtFixedRate(new Projectiles(), 0, speed);
	}

	/**
	 * Typically called by either the GameClient object or the constructors of
	 * Game type subclasses. Officially starts the game by setting maps,
	 * updating the collisionRecs ArrayList, updating the user, adding
	 * listeners, and setting the timers.
	 * 
	 * @param map
	 *            The map object used that must be passed to map collision boxes
	 *            and start the game.
	 */
	public void startGame(Map map) {
		/* sets the map and collision maps */
		if (map.getMap() != null)
			setMap(map.getMap());
		if (map.getCollisionMap() != null)
			setCollisionMap(map.getCollisionMap());

		/* updates collisionRecs based on RGB values of collisionMap */
		for (int x = 0; x <= collisionMap.getWidth() - 1; x++)
			for (int y = 0; y <= collisionMap.getHeight() - 1; y++)
				if (collisionMap.getRGB(x, y) != -16777216)
					collisionRecs.add(new Rectangle(x * 8, y * 8, 8, 8));

		/* updates the user and puts them in the players arraylist */
		updatePlayer();
		players.add(user);

		/* add/start listeners for user input */
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		/*
		 * requests focus in the window and brings the JFrame object to the
		 * front of the user's screen
		 */
		requestFocusInWindow();
		frame.toFront();

		/* calls setTimers() which sets the game timertasks */
		setTimers();
	}

	/**
	 * Starts online game that does not involve a side along client. Does not
	 * necessarily have to a 'public' server, it only has to a MP gamemode type
	 * that is serverside only.
	 * 
	 * @param map
	 *            The map object used that must be passed to map collision boxes
	 *            and start the game.
	 */
	public void startPublicServerGame(Map map) {
		/* sets the map and collision maps */
		if (map.getMap() != null)
			setMap(map.getMap());
		if (map.getCollisionMap() != null)
			setCollisionMap(map.getCollisionMap());

		/* updates collisionRecs based on RGB values of collisionMap */
		for (int x = 0; x <= collisionMap.getWidth() - 1; x++) {
			for (int y = 0; y <= collisionMap.getHeight() - 1; y++) {
				if (collisionMap.getRGB(x, y) != -16777216)
					collisionRecs.add(new Rectangle(x * 8, y * 8, 8, 8));
			}
		}

		/* calls setTimers() which sets the game timertasks */
		setTimers();
	}

	/**
	 * The overriden paint method; this branches off into other paint methods
	 * based on the state variable.
	 * 
	 * @param g
	 *            The Graphics object that is passed to the other paint methods
	 */
	@Override
	public void paint(Graphics g) {
		/* calls the super method for the paint method */
		super.paint(g);

		/* if the game is running, calls various paint functions */
		if (running) {
			if (state == 0) {
				paintGame(g);
			} else if (state == 1) {
				paintGame(g);
				paintPauseMenu(g);
			} else if (state == 2) {
				paintGame(g);
				paintReconnect(g);
			}
		}
	}

	/**
	 * Paints the general aspects of the actual game itself such as the players,
	 * names, projectiles, maps, and other images that are needed for every
	 * gamemode. More specialized things such as GUI and gamemode specific
	 * objects should be painted in the overriden
	 * <code>paintGame(Graphics g)</code> methods of Game's subclasses. The
	 * <code>Graphics g</code> parameter that is passed to this method is turned
	 * into a Graphics2D object for more specialized painting.
	 * 
	 * @param g
	 *            The Graphics object used to paint the game.
	 */
	public void paintGame(Graphics g) {
		/* creates a Grahpics2D object from the Graphics object */
		Graphics2D g2d = (Graphics2D) g;

		/*
		 * sets antialiasing rendering hints for fonts and sets the font to
		 * Livewired
		 */
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		font = new Font("Livewired", Font.PLAIN, 10);

		/* translates and scales graphics based on zoom value */
		g2d.translate(-getWidth() * (0.5 * zoom - 0.5), -getHeight() * (0.5 * zoom - 0.5));
		g2d.scale(zoom, zoom);

		/* sets the color and font of the graphics */
		g2d.setColor(NAMETAG_GREEN);
		g2d.setFont(font);

		/* draws the map in relation to the users position */
		g2d.drawImage(map, (int) (user.getxPosition() + getWidth() / 2) - user.getSpriteWidth() / 2,
				(int) (user.getyPosition() + getHeight() / 2), null);

		/*
		 * draws every player that is not the user and is not dead as well as
		 * their weapons in relation to the position of the user
		 */
		for (Player p : players) {
			/* if the player is not the user and not dead */
			if (p != user && !p.isDead()) {
				/* draws the player in relation to user */
				g2d.drawImage(p.getPlayerSprite(),
						(int) (getWidth() / 2 + (user.getxPosition() - p.getxPosition())) - user.getSpriteWidth() / 2,
						(int) (getHeight() / 2 + (user.getyPosition() - p.getyPosition())), null);

				/* initializes weapon drawing variables */
				double rAngle = 0;
				double x = 0;
				double y = 0;

				/*
				 * calculates weapon drawing variables assuming that the weapon
				 * is a projectile shooter; then, rotates graphics based on
				 * rAngle
				 */
				if (p.getInHand() instanceof ProjectileShooter) {
					rAngle = ((ProjectileShooter) p.getInHand()).getrAngle();
					if (p.isFacingRight()) {
						x = getWidth() / 2 + (user.getxPosition() - p.getxPosition()) - user.getSpriteWidth() / 2
								+ p.getWeaponXTweak() + p.getInHand().getxAdjustment();
						y = getHeight() / 2 + (user.getyPosition() - p.getyPosition()) + p.getInHand().getyAdjustment()
								+ p.getWeaponYTweak() + p.getInHand().getHeight() / 2;
						g2d.rotate(rAngle, x, y);
					} else {
						x = getWidth() / 2 + (user.getxPosition() - p.getxPosition()) - user.getSpriteWidth() / 2
								+ p.getWeaponXTweak() + p.getInHand().getxAdjustment() + p.getInHand().getWidth();
						y = getHeight() / 2 + (user.getyPosition() - p.getyPosition()) + p.getInHand().getyAdjustment()
								+ p.getWeaponYTweak() + p.getInHand().getHeight() / 2;
						g2d.rotate(rAngle, x, y);
					}
				}

				/* draws weapon for players in relation to user position */
				g2d.drawImage(p.getInHand().getSprite(),
						getWidth() / 2 + (int) (user.getxPosition() - p.getxPosition()) - user.getSpriteWidth() / 2
								+ p.getWeaponXTweak() + p.getInHand().getxAdjustment(),
						getHeight() / 2 + (int) (user.getyPosition() - p.getyPosition()) + p.getWeaponYTweak()
								+ p.getInHand().getyAdjustment(),
						null);

				/* 'unrotates' graphics for projectile shooter weapons */
				if (p.getInHand() instanceof ProjectileShooter)
					g2d.rotate(-rAngle, x, y);

				/* draw names for players above their heads */
				g2d.drawString(p.getName(),
						(int) (getWidth() / 2 + (user.getxPosition() - p.getxPosition()) - p.getName().length() * 3), //
						(int) (getHeight() / 2 + (user.getyPosition() - p.getyPosition())) + 5);
			}
		}

		/* draws user, weapon, and name assuming user is not dead */
		if (!user.isDead()) {
			/* draws user in the middle of the screen */
			g2d.drawImage(user.getPlayerSprite(), getWidth() / 2 - user.getSpriteWidth() / 2, getHeight() / 2, null);

			/* initializes weapon drawing variables for user */
			double rAngle = 0;
			double x = 0;
			double y = 0;

			/*
			 * calculates weapon drawing variables assuming that the weapon is a
			 * projectile shooter; then, rotates graphics based on rAngle for
			 * user
			 */
			if (user.getInHand() instanceof ProjectileShooter) {
				rAngle = ((ProjectileShooter) user.getInHand()).getrAngle();
				if (user.isFacingRight()) {
					x = getWidth() / 2 + user.getWeaponXTweak() + user.getInHand().getxAdjustment()
							- user.getSpriteWidth() / 2;
					y = getHeight() / 2 + user.getInHand().getyAdjustment() + user.getWeaponYTweak()
							+ user.getInHand().getHeight() / 2;
					g2d.rotate(rAngle, x, y);
				} else {
					x = getWidth() / 2 + user.getWeaponXTweak() + user.getInHand().getxAdjustment()
							+ user.getInHand().getWidth() - user.getSpriteWidth() / 2;
					y = getHeight() / 2 + user.getInHand().getyAdjustment() + user.getWeaponYTweak()
							+ user.getInHand().getHeight() / 2;
					g2d.rotate(rAngle, x, y);
				}
			}

			/* draw user weapon */
			g2d.drawImage(user.getInHand().getSprite(),
					getWidth() / 2 + user.getWeaponXTweak() + user.getInHand().getxAdjustment()
							- user.getSpriteWidth() / 2,
					getHeight() / 2 + user.getInHand().getyAdjustment() + user.getWeaponYTweak(), null);

			/* 'unrotates' graphics for projectile shooter weapons */
			if (user.getInHand() instanceof ProjectileShooter)
				g2d.rotate(-rAngle, x, y);

			/* draws name of user above their head */
			g2d.drawString(user.getName(), getWidth() / 2 - user.getName().length() * 3, getHeight() / 2 + 5); //
		}

		/*
		 * cycles through projectiles arraylist and draws them with the proper
		 * angle
		 */
		for (Projectile p : projectiles) {
			double rAngle = p.getrAngle() + Math.PI;
			double x = user.getxPosition() - user.getSpriteWidth() / 2 - p.getxPosition() + getWidth() / 2
					+ p.getWidth() / 2;
			double y = user.getyPosition() - p.getyPosition() + getHeight() / 2 + p.getHeight() / 2;
			g2d.rotate(rAngle, x, y);
			g2d.drawImage(p.getSprite(),
					(int) (user.getxPosition() - p.getxPosition() + getWidth() / 2) - user.getSpriteWidth() / 2,
					(int) (user.getyPosition() - p.getyPosition() + getHeight() / 2), null);
			g2d.rotate(-rAngle, x, y);
		}

		/* repeatedly requests input focus for this jpanel */
		requestFocusInWindow();
	}

	/**
	 * This method is usually called immediately after calling
	 * <code>public void paintGame(Graphics g)</code> so that the pause menu
	 * serves as an overlay and gamemodes that do not pause, such as MP
	 * gamemodes, can continue in the background. There are typically changes to
	 * the contents of the pause menu based on the gamemode type.
	 * 
	 * @param g
	 *            The Graphics object used to paint the pause menu.
	 */
	public void paintPauseMenu(Graphics g) {
		/*
		 * creates Graphics2D object from Graphics object and sets antialiasing
		 * rendering hints for the font
		 */
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		/* draws a semi-transparent color on the entire screen */
		g2d.setColor(PAUSE_OVERLAY);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * This method is usually called immediately after calling
	 * <code>public void paintGame(Graphics g)</code> so that the game is still
	 * painted underneath the reconnect screen which only serves as an overlay
	 * to notify the user of connection issues.
	 * 
	 * @param g
	 *            The graphics object used to paint the reconnect screen.
	 */
	public void paintReconnect(Graphics g) {
		/*
		 * creates graphics2d object from graphics object and sets antialiasing
		 * rendering hints for font
		 */
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		/* draws a semi-transpart color on the entire screen */
		g2d.setColor(PAUSE_OVERLAY);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		/*
		 * creates a string literal of the word reconnecting, and either 1, 2,
		 * or 3 periods after it based on system time (each second, another
		 * period is added until 3 periods are reached where it will cycle back
		 * to 1 period)
		 */
		String reconnecting;
		if ((System.currentTimeMillis() / 1000) % 3 == 0)
			reconnecting = "Reconnecting.";
		else if ((System.currentTimeMillis() / 1000) % 3 == 1)
			reconnecting = "Reconnecting..";
		else
			reconnecting = "Reconnecting...";

		/* draws the string in the middle of the screen with a font and color */
		g2d.setColor(NAMETAG_GREEN);
		g2d.setFont(new Font("Livewired", Font.PLAIN, 30));
		g2d.drawString(reconnecting, (int) (getWidth() / 2 - reconnecting.length() * 8), getHeight() / 2);
	}

	/* all of the required listener methods */
	@Override
	public void keyPressed(KeyEvent e) {
		/*
		 * when a key is pressed, add it to the ArrayList of keys that are being
		 * pressed assuming that it is not already in there
		 */
		if (!keys.contains(e.getKeyCode()))
			keys.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		/*
		 * when a key is released, remove it from the ArrayList of keys being
		 * pressed
		 */
		keys.remove((Integer) e.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		/*
		 * when escape is typed, toggle the state between 0 and 1 (unpaused and
		 * paused)
		 */
		if (keys.contains(KeyEvent.VK_ESCAPE)) {
			if (state == 0) {
				state = 1;
			} else if (state == 1) {
				state = 0;
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		/*
		 * when the mouse is pressed, the game is unpaused, and the user is not
		 * dead, initialize an attack
		 */
		if (state == 0)
			if (!user.isDead())
				user.getInHand().attack(players, false);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		/*
		 * when the mouse is moved, calculate if the user is facing right or
		 * left and calculate the angle of their weapon (both based on mouse
		 * location; the second only matters for projectile shooters)
		 */
		if (state == 0) {
			if (e.getX() > getWidth() / 2)
				user.setFacingRight(true);
			else
				user.setFacingRight(false);
			if (user.getInHand() instanceof ProjectileShooter)
				if (user.isFacingRight())
					((ProjectileShooter) user.getInHand()).setrAngle(
							Math.atan2(e.getY() - getHeight() / 2 - 32 * zoom, e.getX() - getWidth() / 2 - 16 * zoom));
				else
					((ProjectileShooter) user.getInHand()).setrAngle(
							Math.atan2(e.getY() - getHeight() / 2 - 32 * zoom, e.getX() - getWidth() / 2 - 16 * zoom)
									- Math.PI);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		/*
		 * if the mouse wheel is moved and the game is unpaused, switch the
		 * weapon that is in the user's hand
		 */
		if (state == 0) {
			if (user.getInHand().equals(user.getMelee()))
				user.setInHand(user.getRanged());
			else
				user.setInHand(user.getMelee());
		}
	}

	/* unused listener methods that are necessary to declare */
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	/* standard get/set methods */
	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Map getGraphicsMap() {
		return graphicsMap;
	}

	public void setGraphicsMap(Map graphicsMap) {
		this.graphicsMap = graphicsMap;
	}

	public int getPlayerSize() {
		return playerSize;
	}

	public void setPlayerSize(int playerSize) {
		this.playerSize = playerSize;
	}

	public GameServer getSocketServer() {
		return socketServer;
	}

	public void setSocketServer(GameServer socketServer) {
		this.socketServer = socketServer;
	}

	public GameClient getSocketClient() {
		return socketClient;
	}

	public void setSocketClient(GameClient socketClient) {
		this.socketClient = socketClient;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserRace() {
		return userRace;
	}

	public void setUserRace(String userRace) {
		this.userRace = userRace;
	}

	public String getUserColor() {
		return userColor;
	}

	public void setUserColor(String userColor) {
		this.userColor = userColor;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getGameID() {
		return gameID;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	public int getLobbyTime() {
		return lobbyTime;
	}

	public void setLobbyTime(int lobbyTime) {
		this.lobbyTime = lobbyTime;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public CopyOnWriteArrayList<Projectile> getProjectiles() {
		return projectiles;
	}

	public void setProjectiles(CopyOnWriteArrayList<Projectile> projectiles) {
		this.projectiles = projectiles;
	}

	public ArrayList<Integer> getProjectileIDs() {
		return projectileIDs;
	}

	public void setProjectileIDs(ArrayList<Integer> projectileIDs) {
		this.projectileIDs = projectileIDs;
	}

	public String getUserMelee() {
		return userMelee;
	}

	public void setUserMelee(String userMelee) {
		this.userMelee = userMelee;
	}

	public String getUserRanged() {
		return userRanged;
	}

	public void setUserRanged(String userRanged) {
		this.userRanged = userRanged;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public void setCollisionRecs(ArrayList<Rectangle> collisions) {
		this.collisionRecs = collisions;
	}

	public ArrayList<Rectangle> getCollisionRecs() {
		return collisionRecs;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public ArrayList<Integer> getKeys() {
		return keys;
	}

	public void setKeys(ArrayList<Integer> keys) {
		this.keys = keys;
	}

	public Player getUser() {
		return user;
	}

	public void setUser(Player user) {
		this.user = user;
	}

	public BufferedImage getMap() {
		return map;
	}

	public void setMap(BufferedImage map) {
		this.map = map;
	}

	public BufferedImage getCollisionMap() {
		return collisionMap;
	}

	public void setCollisionMap(BufferedImage collisionMap) {
		this.collisionMap = collisionMap;
	}

	/* handles projectile motion and angles */
	private class Projectiles extends TimerTask {

		@Override
		public void run() {
			try {
				if (socketClient == null || socketServer != null) {
					for (Projectile p : projectiles) {
						if (p.getVelox() != 0 || p.getVeloy() != 0) {
							p.setVeloy(p.getVeloy() - p.getAirTime());
							p.setxPosition(p.getxPosition() + p.getVelox());
							p.setyPosition(p.getyPosition() + p.getVeloy());
							p.setrAngle(Math.atan2(p.getVeloy(), p.getVelox()));
						}
						for (Rectangle r : collisionRecs)
							if (new Rectangle((int) -p.getxPosition(), (int) -p.getyPosition(), p.getWidth(),
									p.getHeight()).intersects(r)) {
								p.setVelox(0);
								p.setVeloy(0);
							}
						if (socketServer != null)
							for (Player player : socketServer.connectedPlayers) {
								if (new Rectangle((int) -p.getxPosition(), (int) -p.getyPosition(), p.getWidth(),
										p.getHeight()).intersects(
												new Rectangle(player.getBounds().x + 10, player.getBounds().y + 14,
														player.getBounds().width - 20, player.getBounds().height - 14))
										&& !player.isDead() && (p.getVelox() != 0 || p.getVeloy() != 0)) {
									ArrayList<Player> attacked = new ArrayList<Player>();
									attacked.add(player);
									p.attack(attacked, true);
									projectiles.remove(p);
									projectileIDs.remove((Integer) p.getId());
									Packet15RemoveProjectile packet = new Packet15RemoveProjectile((int) p.getId());
									packet.writeData(socketServer);
								}
							}
						else
							for (Player player : players) {
								if (new Rectangle((int) -p.getxPosition(), (int) -p.getyPosition(), p.getWidth(),
										p.getHeight()).intersects(
												new Rectangle(player.getBounds().x + 10, player.getBounds().y + 14,
														player.getBounds().width - 20, player.getBounds().height - 14))
										&& !player.isDead() && (p.getVelox() != 0 || p.getVeloy() != 0)) {
									ArrayList<Player> attacked = new ArrayList<Player>();
									attacked.add(player);
									p.attack(attacked, false);
									projectiles.remove(p);
									projectileIDs.remove((Integer) p.getId());
								}
							}
						if (p.getVelox() == 0 && p.getVeloy() == 0 && p.getDeadTime() == 0)
							p.setDeadTime(System.nanoTime());
						else if (p.getVelox() == 0 && p.getVeloy() == 0
								&& System.nanoTime() >= p.getDeadTime() + 2000000000) {
							projectiles.remove(p);
							projectileIDs.remove((Integer) p.getId());
							if (socketServer != null) {
								Packet15RemoveProjectile packet = new Packet15RemoveProjectile((int) p.getId());
								packet.writeData(socketServer);
							}
						}
					}
				}
			} catch (ConcurrentModificationException e) {
				e.printStackTrace();
			}
		}
	}

	/* handles the animation of player weapons (only matters for clients) */
	private class PlayerWeapons extends TimerTask {

		@Override
		public void run() {
			for (Player p : players) {
				if (!p.isFacingRight()) {
					p.getInHand().setSpriteY(0);
				} else {
					if (p.getInHand().getSpriteY() != 1)
						p.getInHand().setSpriteY(1);
				}
				p.getInHand()
						.setSprite(p.getInHand().getImage().getSubimage(
								p.getInHand().getSpriteX() * p.getInHand().getWidth(),
								p.getInHand().getSpriteY() * p.getInHand().getHeight(), p.getInHand().getWidth(),
								p.getInHand().getHeight()));
				if (p.isFacingRight()) {
					p.getInHand().setxAdjustment(p.getInHand().getHoldingRightX());
					p.getInHand().setyAdjustment(p.getInHand().getHoldingRightY());
				} else {
					p.getInHand().setxAdjustment(p.getInHand().getHoldingLeftX());
					p.getInHand().setyAdjustment(p.getInHand().getHoldingLeftY());
				}
				if (!p.isWalking() && !p.isRunning() && p.isFacingRight()) {
					p.setWeaponXTweak(p.getStandingRightHandX());
					p.setWeaponYTweak(p.getStandingRightHandY());
				} else if (!p.isWalking() && !p.isRunning()) {
					p.setWeaponXTweak(p.getStandingLeftHandX());
					p.setWeaponYTweak(p.getStandingLeftHandY());
				} else if ((p.isWalking() || p.isRunning()) && p.isFacingRight()) {
					p.setWeaponXTweak(p.getWalkingRightHandX());
					p.setWeaponYTweak(p.getWalkingRightHandY());
				} else if ((p.isWalking() || p.isRunning())) {
					p.setWeaponXTweak(p.getWalkingLeftHandX());
					p.setWeaponYTweak(p.getWalkingLeftHandY());
				}
			}
		}
	}

	/* handles the input from the keyboard (only matters for clients) */
	private class Input extends TimerTask {

		@Override
		public void run() {
			if (state == 0) {
				if (keys.contains(KeyEvent.VK_EQUALS) && zoom < 4) {
					zoom++;
					keys.remove((Integer) KeyEvent.VK_EQUALS);
				}
				if (keys.contains(KeyEvent.VK_MINUS) && zoom > 1) {
					zoom--;
					keys.remove((Integer) KeyEvent.VK_MINUS);
				}
				if (keys.contains(KeyEvent.VK_D) && !keys.contains(KeyEvent.VK_SHIFT) || keys.contains(KeyEvent.VK_D)
						&& keys.contains(KeyEvent.VK_SHIFT) && !user.isStaminaRefilled()) {
					user.setMovingRight(true);
					user.setWalking(true);
					user.setRunning(false);
					user.setVeloX(-user.getWalkSpeed());
				}
				if (keys.contains(KeyEvent.VK_A) && !keys.contains(KeyEvent.VK_SHIFT) || keys.contains(KeyEvent.VK_A)
						&& keys.contains(KeyEvent.VK_SHIFT) && !user.isStaminaRefilled()) {
					user.setMovingRight(false);
					user.setWalking(true);
					user.setRunning(false);
					user.setVeloX(user.getWalkSpeed());
				}
				if (keys.contains(KeyEvent.VK_A) && keys.contains(KeyEvent.VK_D) && !keys.contains(KeyEvent.VK_SHIFT)
						|| keys.contains(KeyEvent.VK_D) && keys.contains(KeyEvent.VK_A)
								&& keys.contains(KeyEvent.VK_SHIFT) && !user.isStaminaRefilled()) {
					if (keys.indexOf(KeyEvent.VK_A) > keys.indexOf(KeyEvent.VK_D)) {
						user.setMovingRight(false);
						user.setWalking(true);
						user.setRunning(false);
						user.setVeloX(user.getWalkSpeed());
					} else {
						user.setMovingRight(true);
						user.setWalking(true);
						user.setRunning(false);
						user.setVeloX(-user.getWalkSpeed());
					}
				}
				if (keys.contains(KeyEvent.VK_D) && keys.contains(KeyEvent.VK_SHIFT) && user.isStaminaRefilled()) {
					user.setMovingRight(true);
					user.setWalking(false);
					user.setRunning(true);
					user.setVeloX(-user.getRunSpeed());
				}
				if (keys.contains(KeyEvent.VK_A) && keys.contains(KeyEvent.VK_SHIFT) && user.isStaminaRefilled()) {
					user.setMovingRight(false);
					user.setWalking(false);
					user.setRunning(true);
					user.setVeloX(user.getRunSpeed());
				}
				if (keys.contains(KeyEvent.VK_A) && keys.contains(KeyEvent.VK_D) && keys.contains(KeyEvent.VK_SHIFT)
						&& user.isStaminaRefilled()) {
					if (keys.indexOf(KeyEvent.VK_A) > keys.indexOf(KeyEvent.VK_D)) {
						user.setMovingRight(false);
						user.setWalking(false);
						user.setRunning(true);
						user.setVeloX(user.getRunSpeed());
					} else {
						user.setMovingRight(true);
						user.setWalking(false);
						user.setRunning(true);
						user.setVeloX(-user.getRunSpeed());
					}
				}
				if ((keys.contains(KeyEvent.VK_W) || keys.contains(KeyEvent.VK_SPACE)) && !user.isJumping()
						&& !user.isFalling())
					user.setVeloY(user.getInitJumpSpeed());
				if (keys.contains(KeyEvent.VK_T) && !user.isDead()) {
					user.setHealth(0);
					user.setDead(true);
					if (socketClient != null) {
						Packet05Health packet = new Packet05Health(user.getName(), user.getHealth());
						packet.writeData(socketClient);
						Packet16Suicide suicidePacket = new Packet16Suicide(user.getName());
						suicidePacket.writeData(socketClient);
					}
				}
				if (keys.contains(KeyEvent.VK_R) && !keys.contains(KeyEvent.VK_T) && user.isDead()) {
					if (socketClient == null) {
						user.setHealth(user.getMaxHealth());
						user.setDead(false);
					} else {
						Packet17Respawn packet = new Packet17Respawn(user.getName());
						packet.writeData(socketClient);
					}
				}
				if (!keys.contains(KeyEvent.VK_A) && !keys.contains(KeyEvent.VK_D)) {
					user.setWalking(false);
					user.setRunning(false);
					user.setVeloX(0);
				}
			}
		}
	}

	/* handles the animation of the players (only matters for clients) */
	private class AnimatePlayers extends TimerTask {

		int t = 0;
		int w = 1;
		int r = 1;

		@Override
		public void run() {
			t++;
			if (t % 24 == 0 && w != 4) {
				w++;
			} else if (t % 24 == 0) {
				w = 1;
			}
			if (t % 8 == 0 && r != 4) {
				r++;
			} else if (t % 8 == 0) {
				r = 1;
			}
			for (Player p : players) {
				if (p.isWalking() && p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(w);
					p.setSpriteY(0);
				} else if (p.isRunning() && p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(r);
					p.setSpriteY(0);
				} else if (p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(0);
					p.setSpriteY(0);
				}
				if (p.isWalking() && p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(w);
					p.setSpriteY(2);
				} else if (p.isRunning() && p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(r);
					p.setSpriteY(2);
				} else if (p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(0);
					p.setSpriteY(2);
				}
				if (p.isWalking() && !p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(w);
					p.setSpriteY(1);
				} else if (p.isRunning() && !p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(r);
					p.setSpriteY(1);
				} else if (!p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(0);
					p.setSpriteY(1);
				}
				if (p.isWalking() && !p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(w);
					p.setSpriteY(3);
				} else if (p.isRunning() && !p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(r);
					p.setSpriteY(3);
				} else if (!p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(0);
					p.setSpriteY(3);
				}
				p.setPlayerSprite(p.getPlayerSpriteSheet().getSubimage(p.getSpriteX() * p.getSpriteWidth(),
						p.getSpriteY() * p.getSpriteHeight(), p.getSpriteWidth(), p.getSpriteHeight()));
			}
		}
	}

	/*
	 * this is the subclass for handling the stamina value for each player (only
	 * matters if server or offline gameplay)
	 */
	private class Stamina extends TimerTask {

		@Override
		public void run() {
			for (Player p : players) {
				if (p.isRunning() && p.getStamina() >= 0.1)
					p.setStamina(p.getStamina() - 0.1);
				else if (p.isRunning()) {
					p.setRunning(false);
					p.setWalking(true);
					if (p.isStaminaRefilled())
						p.waitForStamina();
				}
				if (p.getStamina() + 0.075 > p.getMaxStamina())
					p.setStamina(p.getMaxStamina());
				else if (p.getStamina() + 0.075 <= p.getMaxStamina())
					p.setStamina(p.getStamina() + 0.05);
			}
		}
	}

	/*
	 * when editing this subclass, make sure to edit both the code for single
	 * player (socketClient == null) and multiplayer (socketClient != null);
	 * also, in player.checkCollisions(), 1 = left or right; 3 = up, and 4 =
	 * down
	 */
	private class MovePlayers extends TimerTask {

		@Override
		public void run() {
			if (socketClient == null) {
				for (Player p : players) {
					if (p.getVeloX() != 0 && !p.checkCollisions().contains(1) && !p.isDead())
						p.setxPosition(p.getxPosition() + p.getVeloX());
					else if (p.getVeloX() != 0 && p.checkCollisions().contains(1)) {
						boolean flag = false;
						for (Rectangle r : getCollisionRecs())
							if (new Rectangle((int) (p.getBounds().x + 10 - p.getVeloX()), p.getBounds().y + 6,
									p.getBounds().width - 20, p.getBounds().height - 14).intersects(r))
								flag = true;
						if (!flag) {
							p.setyPosition(p.getyPosition() + 8);
							p.setxPosition(p.getxPosition() + p.getVeloX());
						}
					}
					if (p.getVeloY() > 0 && !p.checkCollisions().contains(3) && !p.isDead())
						p.setyPosition(p.getyPosition() + p.getVeloY());
					else if (p.getVeloY() > 0 && !p.isDead()) {
						int h = 0;
						outerloop: for (Rectangle r : getCollisionRecs()) {
							h = 0;
							while (!new Rectangle(p.getBounds().x, p.getBounds().y - h + (64 - p.getHeight()),
									p.getBounds().width, p.getBounds().height).intersects(r)) {
								if (h > p.getVeloY())
									continue outerloop;
								h++;
							}
							break;
						}
						p.setyPosition(p.getyPosition() + h - 1);
						p.setVeloY(-0.5);
					}
					if (p.getVeloY() < 0 && !p.checkCollisions().contains(4) && !p.isDead())
						p.setyPosition(p.getyPosition() + p.getVeloY());
					else if (p.getVeloY() < 0 && !p.isDead()) {
						int h = 0;
						outerloop: for (Rectangle r : getCollisionRecs()) {
							h = 0;
							while (!new Rectangle(p.getBounds().x, p.getBounds().y - h, p.getBounds().width,
									p.getBounds().height).intersects(r)) {
								if (h < p.getVeloY())
									continue outerloop;
								h--;
							}
							break;
						}
						p.setyPosition(p.getyPosition() + h + 1);
						p.setVeloY(0);
					}
					if (p.getVeloY() == 0) {
						p.setJumping(false);
						p.setFalling(false);
					} else if (p.getVeloY() > 0 && !p.checkCollisions().contains(3)) {
						p.setJumping(true);
						p.setFalling(false);
						p.setVeloY(p.getVeloY() / 1.1);
					} else if (p.getVeloY() < 0 && p.getVeloY() >= -14) {
						p.setJumping(false);
						p.setFalling(true);
						p.setVeloY(p.getVeloY() * 1.09);
					} else {
						p.setJumping(false);
						p.setFalling(true);
					}
					if (p.getVeloY() > 0 && p.getVeloY() < 1)
						p.setVeloY(-0.5);
					if (p.canFall() && !p.isFalling() && !p.isJumping() && !p.checkCollisions().contains(4)
							&& !p.isDead())
						p.setVeloY(-0.5);
				}
			} else {
				if (user.getVeloX() != 0 && !user.checkCollisions().contains(1) && !user.isDead())
					user.setxPosition(user.getxPosition() + user.getVeloX());
				else if (user.getVeloX() != 0 && user.checkCollisions().contains(1)) {
					boolean flag = false;
					for (Rectangle r : getCollisionRecs())
						if (new Rectangle((int) (user.getBounds().x + 10 - user.getVeloX()), user.getBounds().y + 6,
								user.getBounds().width - 20, user.getBounds().height - 14).intersects(r))
							flag = true;
					if (!flag) {
						user.setyPosition(user.getyPosition() + 8);
						user.setxPosition(user.getxPosition() + user.getVeloX());
					}
				}
				if (user.getVeloY() > 0 && !user.checkCollisions().contains(3) && !user.isDead())
					user.setyPosition(user.getyPosition() + user.getVeloY());
				else if (user.getVeloY() > 0 && !user.isDead()) {
					int h = 0;
					outerloop: for (Rectangle r : getCollisionRecs()) {
						h = 0;
						while (!new Rectangle(user.getBounds().x, user.getBounds().y - h + (64 - user.getHeight()),
								user.getBounds().width, user.getBounds().height).intersects(r)) {
							if (h > user.getVeloY())
								continue outerloop;
							h++;
						}
						break;
					}
					user.setyPosition(user.getyPosition() + h - 1);
					user.setVeloY(-0.5);
				}
				if (user.getVeloY() < 0 && !user.checkCollisions().contains(4) && !user.isDead())
					user.setyPosition(user.getyPosition() + user.getVeloY());
				else if (user.getVeloY() < 0 && !user.isDead()) {
					int h = 0;
					outerloop: for (Rectangle r : getCollisionRecs()) {
						h = 0;
						while (!new Rectangle(user.getBounds().x, user.getBounds().y - h, user.getBounds().width,
								user.getBounds().height).intersects(r)) {
							if (h < user.getVeloY())
								continue outerloop;
							h--;
						}
						break;
					}
					user.setyPosition(user.getyPosition() + h + 1);
					user.setVeloY(0);
				}
				if (user.getVeloY() == 0) {
					user.setJumping(false);
					user.setFalling(false);
				} else if (user.getVeloY() > 0 && !user.checkCollisions().contains(3)) {
					user.setJumping(true);
					user.setFalling(false);
					user.setVeloY(user.getVeloY() / 1.1);
				} else if (user.getVeloY() < 0 && user.getVeloY() >= -14) {
					user.setJumping(false);
					user.setFalling(true);
					user.setVeloY(user.getVeloY() * 1.09);
				} else {
					user.setJumping(false);
					user.setFalling(true);
				}
				if (user.getVeloY() > 0 && user.getVeloY() < 1)
					user.setVeloY(-0.5);
				if (user.canFall() && !user.isFalling() && !user.isJumping() && !user.checkCollisions().contains(4)
						&& !user.isDead())
					user.setVeloY(-0.5);
			}
			for (Player p : players) {
				if (!p.isDead() && p.getHealth() <= 0) {
					p.setHealth(0);
					p.setDead(true);
				} else if (p.isDead() && p.getHealth() > 0) {
					p.setDead(false);
				}
			}
		}
	}

	/* timertask that repaints the screen */
	private class Repaint extends TimerTask {

		@Override
		public void run() {
			repaint();
		}
	}
}