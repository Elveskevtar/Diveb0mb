package com.elveskevtar.divebomb.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.elveskevtar.divebomb.maps.Map;
import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;
import com.elveskevtar.divebomb.race.Cyborg;
import com.elveskevtar.divebomb.race.Human;
import com.elveskevtar.divebomb.race.Player;
import com.elveskevtar.divebomb.weapons.Sword;

public abstract class Game extends JPanel implements KeyListener,
		MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 8757407166624267693L;
	private boolean hosting;
	private boolean started;
	private int speed = 16;
	private int state;
	private int zoom;
	private int playerSize;

	private ArrayList<Rectangle> collisionRecs;
	private ArrayList<Player> players;
	private ArrayList<Integer> keys;

	private BufferedImage map;
	private BufferedImage collisionMap;
	private GameServer socketServer;
	private GameClient socketClient;
	private Font font;
	private Player user;
	private Timer timer;
	private Map graphicsMap;
	private String userName;
	private String userRace;
	private String userColor;
	private String serverIP;

	/*
	 * This is the main constructor; it sets all of the JFrame options, adds
	 * players, initializes the collision map, loads the map images, and calls
	 * the setTimers() method; this will likely change A LOT when we start to
	 * implement different game modes; THIS CONSTRUCTOR IS FOR A FULLSCREEN
	 * WINDOW
	 */
	public Game() {
		super();
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.setBackground(new Color(0, 0, 0));
		this.players = new ArrayList<Player>();
		this.timer = new Timer();
		this.keys = new ArrayList<Integer>();
		this.zoom = 2;
		this.userName = JOptionPane.showInputDialog(this,
				"Enter your username:");
		this.userRace = JOptionPane
				.showInputDialog(this,
						"Enter what type of race you would like to be (human or cyborg):");
		this.userColor = JOptionPane.showInputDialog(this,
				"Enter what color you would like to be (cyborgs only):");
		if (userRace.equalsIgnoreCase("human"))
			user = new Human(this, userName, null, -1);
		else if (userRace.equalsIgnoreCase("cyborg")
				&& userColor.equalsIgnoreCase("purple"))
			user = new Cyborg(this, "purple", userName, null, -1);
		else if (userRace.equalsIgnoreCase("cyborg")
				&& userColor.equalsIgnoreCase("blue"))
			user = new Cyborg(this, "blue", userName, null, -1);
		else if (userRace.equalsIgnoreCase("cyborg"))
			user = new Cyborg(this, userName, -1, null, -1);
		else
			user = new Human(this, userName, null, -1);
		if (userColor.equals(""))
			setUserColor("a");
		this.user.setInHand(new Sword(user));
	}

	/*
	 * This is the main constructor; it sets all of the JFrame options, adds
	 * players, initializes the collision map, loads the map images, and calls
	 * the setTimers() method; this will likely change A LOT when we start to
	 * implement different game modes; THIS CONSTRUCTOR IS FOR A NON-FULLSCREEN
	 * WINDOW AND I HAVE NOT CODED FOR TRANSLATION SO DEPENDING ON THE SIZE IT
	 * MAY LOOK A BIT STRANGE
	 */
	public Game(int width, int height) {
		super();
		this.setSize(width, height);
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.setBackground(new Color(0, 0, 0));
		this.players = new ArrayList<Player>();
		this.timer = new Timer();
		this.collisionRecs = new ArrayList<Rectangle>();
		this.keys = new ArrayList<Integer>();
		this.zoom = 2;
		this.userName = JOptionPane.showInputDialog(this,
				"Enter your username:");
		this.userRace = JOptionPane
				.showInputDialog(this,
						"Enter what type of race you would like to be (human or cyborg):");
		this.userColor = JOptionPane.showInputDialog(this,
				"Enter what color you would like to be (cyborgs only):");
		if (JOptionPane.showConfirmDialog(this,
				"Do you want to host the server?") == 0) {
			socketServer = new GameServer(this);
			socketServer.start();
			hosting = true;
		}
		if (userRace.equalsIgnoreCase("human"))
			user = new Human(this, userName, null, -1);
		else if (userRace.equalsIgnoreCase("cyborg")
				&& userColor.equalsIgnoreCase("purple"))
			user = new Cyborg(this, "purple", userName, null, -1);
		else if (userRace.equalsIgnoreCase("cyborg")
				&& userColor.equalsIgnoreCase("blue"))
			user = new Cyborg(this, "blue", userName, null, -1);
		else if (userRace.equalsIgnoreCase("cyborg"))
			user = new Cyborg(this, userName, -1, null, -1);
		else
			user = new Human(this, userName, null, -1);
		this.user.setInHand(new Sword(user));
	}

	/*
	 * This is a special setter method I made to set the timers for the
	 * subclasses in this class; all of them are currently set to run about 60
	 * times per second and this method is called in the last statement of both
	 * constructors
	 */
	public void setTimers() {
		this.timer.scheduleAtFixedRate(new MovePlayers(), 0, speed);
		this.timer.scheduleAtFixedRate(new Repaint(), 0, speed);
		this.timer.scheduleAtFixedRate(new AnimatePlayers(), 0, speed);
		this.timer.scheduleAtFixedRate(new Input(), 0, speed);
		this.timer.scheduleAtFixedRate(new Stamina(), 0, speed);
		this.timer.scheduleAtFixedRate(new PlayerWeapons(), 0, speed);
		this.started = true;
	}

	public void startGame(Map map) {
		if (map.getMap() != null)
			this.map = map.getMap();
		if (map.getCollisionMap() != null)
			this.collisionMap = map.getCollisionMap();
		this.players.add(user);
		for (int x = 0; x <= collisionMap.getWidth() - 1; x++) {
			for (int y = 0; y <= collisionMap.getHeight() - 1; y++) {
				if (collisionMap.getRGB(x, y) != -16777216)
					collisionRecs.add(new Rectangle(x * 8, y * 8, 8, 8));
			}
		}
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		setTimers();
	}

	/*
	 * These are just some regular variable getters/setters; nothing to fancy
	 */
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

	public Player getUser() {
		return user;
	}

	public void setUser(Player user) {
		this.user = user;
	}

	/*
	 * This is a method that is part of the JPanel class and I have enabled it
	 * to run certain methods depending on the state variable (currently only
	 * the paintGame(Graphics g) method) but this will be useful when we want to
	 * add things like a pause menu
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (started) {
			if (state == 0)
				paintGame(g);
		}
	}

	/*
	 * This is the main method for graphics update; it gets called about 60
	 * times per second to enable a steady frame rate (it draws all of the GUI
	 * including the HUD)
	 */
	public void paintGame(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		font = new Font("Arial Black", 10, 10);
		g2d.translate(-getWidth() * (0.5 * zoom - 0.5), -getHeight()
				* (0.5 * zoom - 0.5));
		g2d.scale(zoom, zoom);
		g2d.setColor(new Color(10, 200, 80));
		g2d.setFont(font);
		g2d.drawImage(map, (int) (user.getxPosition() + getWidth() / 2),
				(int) (user.getyPosition() + getHeight() / 2), null);
		for (Player p : players) {
			if (p != user) {
				if (!p.isDead()) {
					g2d.drawImage(p.getPlayerSprite(),
							(int) (getWidth() / 2 + (user.getxPosition() - p
									.getxPosition())),
							(int) (getHeight() / 2 + (user.getyPosition() - p
									.getyPosition())), null);
					g2d.drawImage(p.getInHand().getSprite(), getWidth() / 2
							+ (int) (user.getxPosition() - p.getxPosition())
							+ p.getWeaponXTweak()
							+ p.getInHand().getxAdjustment(), getHeight() / 2
							+ (int) (user.getyPosition() - p.getyPosition())
							+ p.getWeaponYTweak()
							+ p.getInHand().getyAdjustment(), null);
					g2d.drawString(p.getName(), (int) (getWidth() / 2 + (user
							.getxPosition() - p.getxPosition())),
							(int) (getHeight() / 2 + (user.getyPosition() - p
									.getyPosition())) - 10);
				}
			}
		}
		if (!user.isDead()) {
			g2d.drawImage(user.getPlayerSprite(), getWidth() / 2,
					getHeight() / 2, null);
			g2d.drawImage(
					user.getInHand().getSprite(),
					getWidth() / 2 + user.getWeaponXTweak()
							+ user.getInHand().getxAdjustment(),
					getHeight() / 2 + user.getInHand().getyAdjustment()
							+ user.getWeaponYTweak(), null);
			g2d.drawString(user.getName(), getWidth() / 2, getHeight() / 2 - 10);
		}
		g2d.setColor(new Color(0, 0, 255));
		/*
		 * g2d.drawString("X: " + -user.getxPosition(), getWidth() / 2 - 475,
		 * getHeight() / 2 - 250); g2d.drawString("Y: " + -user.getyPosition(),
		 * getWidth() / 2 - 475, getHeight() / 2 - 240);
		 * g2d.drawString("Health: " + user.getHealth(), getWidth() / 2 - 475,
		 * getHeight() / 2 - 230); g2d.drawString("Stamina: " +
		 * user.getStamina(), getWidth() / 2 - 475, getHeight() / 2 - 220);
		 * g2d.drawString("X Velocity: " + -user.getVeloX(), getWidth() / 2 -
		 * 475, getHeight() / 2 - 210); g2d.drawString("Y Velocity: " +
		 * user.getVeloY(), getWidth() / 2 - 475, getHeight() / 2 - 200);
		 */
	}

	/*
	 * This method is part of the KeyListener interface and I am using it to
	 * track when keys like W, A, S, D are pressed so I can enable user input
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (!keys.contains(e.getKeyCode()))
			keys.add(e.getKeyCode());
	}

	/*
	 * This method is part of the KeyListener interface and I am using it to
	 * track when keys like W, A, S, D are released so I can enable user input
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		keys.remove((Integer) e.getKeyCode());
	}

	/*
	 * This method is part of the KeyListener interface and it works a bit
	 * differently from the KeyPressed(KeyEvent e) method because every
	 * millisecond you hold a key down the keyPressed method will be invoked
	 * however only every time you type a key will the keyTyped method be
	 * invoked so I am using it to change the speed of the game
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		if (keys.contains(KeyEvent.VK_COMMA) && speed - 50 >= 1) {
			timer.cancel();
			timer = new Timer();
			speed -= 50;
			setTimers();
		}
		if (keys.contains(KeyEvent.VK_PERIOD)) {
			timer.cancel();
			timer = new Timer();
			speed += 50;
			setTimers();
		}
	}

	/*
	 * This method is part of the MouseListener interface and all it does is
	 * call the attack method of the weapon in the users hand
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		user.getInHand().attack(players);
	}

	/*
	 * This method is part of the MouseMotionListener interface and I am using
	 * it to track which side of the screen the mouse is on (left or right) and
	 * I have implemented it to change which way the character is facing while
	 * he moves (the graphics have not been made to support this yet however
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (e.getX() > getWidth() / 2 + 16) {
			user.setFacingRight(true);
		} else {
			user.setFacingRight(false);
		}
	}

	/*
	 * All of these empty methods are part of the
	 * KeyListener/MouseListener/MouseMotionListener interfaces which I have
	 * implemented but I will likely not get to coding most of these until
	 * weapons will start to be coded
	 */
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

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
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

	public boolean isHosting() {
		return hosting;
	}

	public void setHosting(boolean hosting) {
		this.hosting = hosting;
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

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	private class PlayerWeapons extends TimerTask {

		@Override
		public void run() {
			for (Player p : players) {
				boolean change = false;
				if (!p.isMovingRight()) {
					if (p.getInHand().getSpriteY() != 0)
						change = true;
					p.getInHand().setSpriteY(0);
				} else {
					if (p.getInHand().getSpriteY() != 1)
						change = true;
					p.getInHand().setSpriteY(1);
				}
				if (change) {
					p.getInHand()
							.setSprite(
									p.getInHand()
											.getImage()
											.getSubimage(
													p.getInHand().getSpriteX()
															* p.getInHand()
																	.getWidth(),
													p.getInHand().getSpriteY()
															* p.getInHand()
																	.getHeight(),
													p.getInHand().getWidth(),
													p.getInHand().getHeight()));
					if (p.isMovingRight()) {
						p.getInHand().setxAdjustment(
								p.getInHand().getHoldingRightX());
						p.getInHand().setyAdjustment(
								p.getInHand().getHoldingRightY());
					} else {
						p.getInHand().setxAdjustment(
								p.getInHand().getHoldingLeftX());
						p.getInHand().setyAdjustment(
								p.getInHand().getHoldingLeftY());
					}
				}
				if (!p.isWalking() && !p.isRunning() && p.isMovingRight()) {
					p.setWeaponXTweak(p.getStandingRightHandX());
					p.setWeaponYTweak(p.getStandingRightHandY());
				} else if (!p.isWalking() && !p.isRunning()) {
					p.setWeaponXTweak(p.getStandingLeftHandX());
					p.setWeaponYTweak(p.getStandingLeftHandY());
				} else if ((p.isWalking() || p.isRunning())
						&& p.isMovingRight()) {
					p.setWeaponXTweak(p.getWalkingRightHandX());
					p.setWeaponYTweak(p.getWalkingRightHandY());
				} else if ((p.isWalking() || p.isRunning())) {
					p.setWeaponXTweak(p.getWalkingLeftHandX());
					p.setWeaponYTweak(p.getWalkingLeftHandY());
				}
			}
		}
	}

	/*
	 * This is the subclass that is responsible for using all of the input
	 * values from the 'keys' array list to move the user around
	 */
	private class Input extends TimerTask {

		@Override
		public void run() {
			if (keys.contains(KeyEvent.VK_ESCAPE))
				System.exit(0);
			if (keys.contains(KeyEvent.VK_D)
					&& !keys.contains(KeyEvent.VK_SHIFT)
					|| keys.contains(KeyEvent.VK_D)
					&& keys.contains(KeyEvent.VK_SHIFT)
					&& !user.isStaminaRefilled()) {
				user.setMovingRight(true);
				user.setWalking(true);
				user.setRunning(false);
				user.setVeloX(-user.getWalkSpeed());
			}
			if (keys.contains(KeyEvent.VK_A)
					&& !keys.contains(KeyEvent.VK_SHIFT)
					|| keys.contains(KeyEvent.VK_A)
					&& keys.contains(KeyEvent.VK_SHIFT)
					&& !user.isStaminaRefilled()) {
				user.setMovingRight(false);
				user.setWalking(true);
				user.setRunning(false);
				user.setVeloX(user.getWalkSpeed());
			}
			if (keys.contains(KeyEvent.VK_A) && keys.contains(KeyEvent.VK_D)
					&& !keys.contains(KeyEvent.VK_SHIFT)
					|| keys.contains(KeyEvent.VK_D)
					&& keys.contains(KeyEvent.VK_A)
					&& keys.contains(KeyEvent.VK_SHIFT)
					&& !user.isStaminaRefilled()) {
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
			if (keys.contains(KeyEvent.VK_D)
					&& keys.contains(KeyEvent.VK_SHIFT)
					&& user.isStaminaRefilled()) {
				user.setMovingRight(true);
				user.setWalking(false);
				user.setRunning(true);
				user.setVeloX(-user.getRunSpeed());
			}
			if (keys.contains(KeyEvent.VK_A)
					&& keys.contains(KeyEvent.VK_SHIFT)
					&& user.isStaminaRefilled()) {
				user.setMovingRight(false);
				user.setWalking(false);
				user.setRunning(true);
				user.setVeloX(user.getRunSpeed());
			}
			if (keys.contains(KeyEvent.VK_A) && keys.contains(KeyEvent.VK_D)
					&& keys.contains(KeyEvent.VK_SHIFT)
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
			if ((keys.contains(KeyEvent.VK_W) || keys
					.contains(KeyEvent.VK_SPACE))
					&& !user.isJumping()
					&& !user.isFalling())
				user.setVeloY(user.getInitJumpSpeed());
			if (keys.contains(KeyEvent.VK_T))
				user.setHealth(0);
			if (keys.contains(KeyEvent.VK_R) && user.isDead()) {
				user.setxPosition(-160);
				user.setyPosition(-1056);
				user.setHealth(user.getMaxHealth());
				user.setDead(false);
			}
			if (!keys.contains(KeyEvent.VK_A) && !keys.contains(KeyEvent.VK_D)) {
				user.setWalking(false);
				user.setRunning(false);
				user.setVeloX(0);
			}
		}
	}

	/*
	 * This is the subclass that involves animating the players by using a
	 * counter and then setting the X and Y for finding the sprite in the sprite
	 * sheet
	 */
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
				} else if (p.isRunning() && p.getVeloX() != 0
						&& p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(r);
					p.setSpriteY(0);
				} else if (p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(0);
					p.setSpriteY(0);
				}
				if (p.isWalking() && p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(w);
					p.setSpriteY(2);
				} else if (p.isRunning() && p.getVeloX() != 0
						&& p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(r);
					p.setSpriteY(2);
				} else if (p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(0);
					p.setSpriteY(2);
				}
				if (p.isWalking() && !p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(w);
					p.setSpriteY(1);
				} else if (p.isRunning() && p.getVeloX() != 0
						&& !p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(r);
					p.setSpriteY(1);
				} else if (!p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(0);
					p.setSpriteY(1);
				}
				if (p.isWalking() && !p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(w);
					p.setSpriteY(3);
				} else if (p.isRunning() && p.getVeloX() != 0
						&& !p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(r);
					p.setSpriteY(3);
				} else if (!p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(0);
					p.setSpriteY(3);
				}
				p.setPlayerSprite(p.getPlayerSpriteSheet().getSubimage(
						p.getSpriteX() * p.getSpriteWidth(),
						p.getSpriteY() * p.getSpriteHeight(),
						p.getSpriteWidth(), p.getSpriteHeight()));
			}
		}
	}

	/*
	 * Checks aspects of stamina like running and eventually attacking
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
				if (!p.isDead() && p.getHealth() <= 0) {
					p.setHealth(0);
					p.setDead(true);
				}
			}
		}
	}

	/*
	 * This is the subclass that gets all of the players velocities and checks
	 * for collisions and finally moves the players accordingly; it also
	 * controls running stamina usage and regeneration
	 */
	private class MovePlayers extends TimerTask {

		@Override
		public void run() {
			for (Player p : players) {
				if (p.getVeloX() < 0 && !p.checkCollisions().contains(2)
						&& !p.isDead())
					p.setxPosition(p.getxPosition() + p.getVeloX());
				if (p.getVeloX() > 0 && !p.checkCollisions().contains(1)
						&& !p.isDead())
					p.setxPosition(p.getxPosition() + p.getVeloX());
				if (p.getVeloY() > 0 && !p.checkCollisions().contains(3)
						&& !p.isDead())
					p.setyPosition(p.getyPosition() + p.getVeloY());
				else if (p.getVeloY() > 0 && !p.isDead()) {
					int h = 0;
					outerloop: for (Rectangle r : getCollisionRecs()) {
						h = 0;
						while (!new Rectangle(p.getBounds().x, p.getBounds().y
								- h + (64 - p.getHeight()),
								p.getBounds().width, p.getBounds().height)
								.intersects(r)) {
							if (h > p.getVeloY())
								continue outerloop;
							h++;
						}
						break;
					}
					p.setyPosition(p.getyPosition() + h - 1);
					p.setVeloY(-0.5);
				}
				if (p.getVeloY() < 0 && !p.checkCollisions().contains(4)
						&& !p.isDead())
					p.setyPosition(p.getyPosition() + p.getVeloY());
				else if (p.getVeloY() < 0 && !p.isDead()) {
					int h = 0;
					outerloop: for (Rectangle r : getCollisionRecs()) {
						h = 0;
						while (!new Rectangle(p.getBounds().x, p.getBounds().y
								- h, p.getBounds().width, p.getBounds().height)
								.intersects(r)) {
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
				if (p.canFall() && !p.isFalling() && !p.isJumping()
						&& !p.checkCollisions().contains(4) && !p.isDead())
					p.setVeloY(-0.5);
			}
		}
	}

	/*
	 * This is the subclass that is involved in calling the update method for
	 * the GUI
	 */
	private class Repaint extends TimerTask {

		@Override
		public void run() {
			repaint();
		}
	}
}
