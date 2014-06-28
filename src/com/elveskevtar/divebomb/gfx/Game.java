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
import java.util.ConcurrentModificationException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.elveskevtar.divebomb.maps.Map;
import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;
import com.elveskevtar.divebomb.net.packets.Packet01Disconnect;
import com.elveskevtar.divebomb.net.packets.Packet05Health;
import com.elveskevtar.divebomb.race.Cyborg;
import com.elveskevtar.divebomb.race.Human;
import com.elveskevtar.divebomb.race.Player;
import com.elveskevtar.divebomb.weapons.Bow;
import com.elveskevtar.divebomb.weapons.Projectile;
import com.elveskevtar.divebomb.weapons.ProjectileShooter;

public abstract class Game extends JPanel implements KeyListener,
		MouseListener, MouseMotionListener {

	public static enum GameTypes {
		DEATHMATCH(00), DEATHMATCHMP(01);

		private int gameID;

		private GameTypes(int gameID) {
			this.gameID = gameID;
		}

		public int getID() {
			return gameID;
		}
	}

	private static final long serialVersionUID = 8757407166624267693L;
	private boolean hosting;
	private boolean running;
	private int speed = 16;
	private int state;
	private int zoom;
	private int playerSize;
	private int gameID;
	private int lobbyTime;

	private ArrayList<Rectangle> collisionRecs;
	private ArrayList<Projectile> projectiles;
	private ArrayList<Player> players;
	private ArrayList<Integer> keys;
	private ArrayList<Integer> projectileIDs;

	private JFrame frame;
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

	public Game(int gameID, JFrame frame) {
		super();
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.setBackground(new Color(0, 0, 0));
		this.players = new ArrayList<Player>();
		this.timer = new Timer();
		this.setGameID(gameID);
		this.collisionRecs = new ArrayList<Rectangle>();
		this.keys = new ArrayList<Integer>();
		this.projectiles = new ArrayList<Projectile>();
		this.projectileIDs = new ArrayList<Integer>();
		this.zoom = 2;
		this.setFrame(frame);
		this.userName = "Bob";
		this.userRace = "human";
		this.userColor = "";
		this.updatePlayer();
		this.user.setInHand(new Bow(user));
		if (userColor.equalsIgnoreCase(""))
			setUserColor(" ");
	}

	public Game(int gameID, int width, int height, JFrame frame) {
		super();
		this.setSize(width, height);
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.setBackground(new Color(0, 0, 0));
		this.players = new ArrayList<Player>();
		this.timer = new Timer();
		this.setGameID(gameID);
		this.collisionRecs = new ArrayList<Rectangle>();
		this.keys = new ArrayList<Integer>();
		this.projectiles = new ArrayList<Projectile>();
		this.projectileIDs = new ArrayList<Integer>();
		this.zoom = 2;
		this.setFrame(frame);
		this.userName = "Bob";
		this.userRace = "human";
		this.userColor = "";
		this.updatePlayer();
		this.user.setInHand(new Bow(user));
		if (userColor.equalsIgnoreCase(""))
			setUserColor(" ");
	}

	public void updatePlayer() {
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
	}

	public void setTimers() {
		this.timer = new Timer();
		this.timer.scheduleAtFixedRate(new MovePlayers(), 0, speed);
		this.timer.scheduleAtFixedRate(new Repaint(), 0, speed);
		this.timer.scheduleAtFixedRate(new AnimatePlayers(), 0, speed);
		this.timer.scheduleAtFixedRate(new Input(), 0, speed);
		this.timer.scheduleAtFixedRate(new Stamina(), 0, speed);
		this.timer.scheduleAtFixedRate(new PlayerWeapons(), 0, speed);
		this.timer.scheduleAtFixedRate(new Projectiles(), 0, speed);
		this.running = true;
	}

	public void startGame(Map map) {
		if (map.getMap() != null)
			this.map = map.getMap();
		if (map.getCollisionMap() != null)
			this.collisionMap = map.getCollisionMap();
		for (int x = 0; x <= collisionMap.getWidth() - 1; x++) {
			for (int y = 0; y <= collisionMap.getHeight() - 1; y++) {
				if (collisionMap.getRGB(x, y) != -16777216)
					collisionRecs.add(new Rectangle(x * 8, y * 8, 8, 8));
			}
		}
		this.updatePlayer();
		this.user.setInHand(new Bow(user));
		this.players.add(user);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.requestFocusInWindow();
		this.setTimers();
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

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (running) {
			if (state == 0)
				paintGame(g);
		}
	}

	public void paintGame(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		font = new Font("Arial Black", Font.PLAIN, 10);
		g2d.translate(-getWidth() * (0.5 * zoom - 0.5), -getHeight()
				* (0.5 * zoom - 0.5));
		g2d.scale(zoom, zoom);
		g2d.setColor(new Color(10, 200, 80));
		g2d.setFont(font);
		g2d.drawImage(map, (int) (user.getxPosition() + getWidth() / 2),
				(int) (user.getyPosition() + getHeight() / 2), null);
		for (Projectile p : projectiles) {
			g2d.rotate((p.getrAngle() + Math.PI),
					user.getxPosition() - p.getxPosition() + getWidth() / 2,
					user.getyPosition() - p.getyPosition() + getHeight() / 2);
			g2d.drawImage(
					p.getSprite(),
					(int) (user.getxPosition() - p.getxPosition() + getWidth() / 2),
					(int) (user.getyPosition() - p.getyPosition() + getHeight() / 2),
					null);
			g2d.rotate(-(p.getrAngle() + Math.PI),
					user.getxPosition() - p.getxPosition() + getWidth() / 2,
					user.getyPosition() - p.getyPosition() + getHeight() / 2);
		}
		for (Player p : players) {
			if (p != user && !p.isDead()) {
				g2d.drawImage(p.getPlayerSprite(),
						(int) (getWidth() / 2 + (user.getxPosition() - p
								.getxPosition())),
						(int) (getHeight() / 2 + (user.getyPosition() - p
								.getyPosition())), null);
				g2d.drawImage(
						p.getInHand().getSprite(),
						getWidth()
								/ 2
								+ (int) (user.getxPosition() - p.getxPosition())
								+ p.getWeaponXTweak()
								+ p.getInHand().getxAdjustment(),
						getHeight()
								/ 2
								+ (int) (user.getyPosition() - p.getyPosition())
								+ p.getWeaponYTweak()
								+ p.getInHand().getyAdjustment(), null);
				g2d.drawString(p.getName(), (int) (getWidth() / 2 + (user
						.getxPosition() - p.getxPosition())),
						(int) (getHeight() / 2 + (user.getyPosition() - p
								.getyPosition())) - 10);
			}
		}
		if (!user.isDead()) {
			g2d.drawImage(user.getPlayerSprite(), getWidth() / 2,
					getHeight() / 2, null);
			if (user.getInHand() instanceof ProjectileShooter)
				if (user.isFacingRight())
					g2d.rotate(
							((ProjectileShooter) user.getInHand()).getrAngle(),
							getWidth() / 2 + user.getWeaponXTweak()
									+ user.getInHand().getxAdjustment(),
							getHeight() / 2 + user.getInHand().getyAdjustment()
									+ user.getWeaponYTweak()
									+ user.getInHand().getHeight() / 2);
				else
					g2d.rotate(
							((ProjectileShooter) user.getInHand()).getrAngle(),
							getWidth() / 2 + user.getWeaponXTweak()
									+ user.getInHand().getxAdjustment()
									+ user.getInHand().getWidth(), getHeight()
									/ 2 + user.getInHand().getyAdjustment()
									+ user.getWeaponYTweak()
									+ user.getInHand().getHeight() / 2);
			g2d.drawImage(
					user.getInHand().getSprite(),
					getWidth() / 2 + user.getWeaponXTweak()
							+ user.getInHand().getxAdjustment(),
					getHeight() / 2 + user.getInHand().getyAdjustment()
							+ user.getWeaponYTweak(), null);
			if (user.getInHand() instanceof ProjectileShooter)
				if (user.isFacingRight())
					g2d.rotate(
							-((ProjectileShooter) user.getInHand()).getrAngle(),
							getWidth() / 2 + user.getWeaponXTweak()
									+ user.getInHand().getxAdjustment(),
							getHeight() / 2 + user.getInHand().getyAdjustment()
									+ user.getWeaponYTweak()
									+ user.getInHand().getHeight() / 2);
				else
					g2d.rotate(
							-((ProjectileShooter) user.getInHand()).getrAngle(),
							getWidth() / 2 + user.getWeaponXTweak()
									+ user.getInHand().getxAdjustment()
									+ user.getInHand().getWidth(), getHeight()
									/ 2 + user.getInHand().getyAdjustment()
									+ user.getWeaponYTweak()
									+ user.getInHand().getHeight() / 2);
			g2d.drawString(user.getName(), getWidth() / 2, getHeight() / 2 - 10);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!keys.contains(e.getKeyCode()))
			keys.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys.remove((Integer) e.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (keys.contains(KeyEvent.VK_COMMA) && speed - 50 >= 1) {
			timer.cancel();
			speed -= 50;
			setTimers();
		}
		if (keys.contains(KeyEvent.VK_PERIOD)) {
			timer.cancel();
			speed += 50;
			setTimers();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!user.isDead())
			user.getInHand().attack(players, false);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (e.getX() > getWidth() / 2 + 16) {
			user.setFacingRight(true);
		} else {
			user.setFacingRight(false);
		}
		if (user.getInHand() instanceof ProjectileShooter)
			if (user.isFacingRight())
				((ProjectileShooter) user.getInHand()).setrAngle(Math.atan2(
						e.getY() - getHeight() / 2 - 32 * zoom, e.getX()
								- getWidth() / 2 - 16 * zoom));
			else
				((ProjectileShooter) user.getInHand()).setrAngle(Math.atan2(
						e.getY() - getHeight() / 2 - 32 * zoom, e.getX()
								- getWidth() / 2 - 16 * zoom)
						- Math.PI);
	}

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

	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}

	public void setProjectiles(ArrayList<Projectile> projectiles) {
		this.projectiles = projectiles;
	}

	public ArrayList<Integer> getProjectileIDs() {
		return projectileIDs;
	}

	public void setProjectileIDs(ArrayList<Integer> projectileIDs) {
		this.projectileIDs = projectileIDs;
	}

	private class Projectiles extends TimerTask {

		@Override
		public void run() {
			try {
				for (Projectile p : projectiles) {
					p.setVeloy(p.getVeloy() - p.getAirTime());
					p.setxPosition(p.getxPosition() + p.getVelox());
					p.setyPosition(p.getyPosition() + p.getVeloy());
					p.setrAngle(Math.atan2(p.getVeloy(), p.getVelox()));
					if (p.getVelox() == 0 && p.getVeloy() == 0) {
						projectiles.remove(p);
						projectileIDs.remove((Integer) p.getId());
					}
				}
			} catch (ConcurrentModificationException e) {
				e.printStackTrace();
			}
		}
	}

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
				p.getInHand().setSprite(
						p.getInHand()
								.getImage()
								.getSubimage(
										p.getInHand().getSpriteX()
												* p.getInHand().getWidth(),
										p.getInHand().getSpriteY()
												* p.getInHand().getHeight(),
										p.getInHand().getWidth(),
										p.getInHand().getHeight()));
				if (p.isFacingRight()) {
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
				if (!p.isWalking() && !p.isRunning() && p.isFacingRight()) {
					p.setWeaponXTweak(p.getStandingRightHandX());
					p.setWeaponYTweak(p.getStandingRightHandY());
				} else if (!p.isWalking() && !p.isRunning()) {
					p.setWeaponXTweak(p.getStandingLeftHandX());
					p.setWeaponYTweak(p.getStandingLeftHandY());
				} else if ((p.isWalking() || p.isRunning())
						&& p.isFacingRight()) {
					p.setWeaponXTweak(p.getWalkingRightHandX());
					p.setWeaponYTweak(p.getWalkingRightHandY());
				} else if ((p.isWalking() || p.isRunning())) {
					p.setWeaponXTweak(p.getWalkingLeftHandX());
					p.setWeaponYTweak(p.getWalkingLeftHandY());
				}
			}
		}
	}

	private class Input extends TimerTask {

		@Override
		public void run() {
			if (keys.contains(KeyEvent.VK_ESCAPE)) {
				if (!hosting && socketClient != null) {
					Packet01Disconnect packet = new Packet01Disconnect(
							user.getName());
					packet.writeData(socketClient);
				}
				System.exit(0);
			}
			if (keys.contains(KeyEvent.VK_EQUALS) && zoom < 4) {
				zoom++;
				keys.remove((Integer) KeyEvent.VK_EQUALS);
			}
			if (keys.contains(KeyEvent.VK_MINUS) && zoom > 1) {
				zoom--;
				keys.remove((Integer) KeyEvent.VK_MINUS);
			}
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
			if (keys.contains(KeyEvent.VK_T) && !user.isDead()) {
				user.setHealth(0);
				if (socketClient != null) {
					Packet05Health packet = new Packet05Health(user.getName(),
							user.getHealth());
					packet.writeData(socketClient);
				}
			}
			if (keys.contains(KeyEvent.VK_R) && user.isDead()) {
				user.setxPosition(-160);
				user.setyPosition(-1056);
				user.setHealth(user.getMaxHealth());
				user.setDead(false);
				if (socketClient != null) {
					Packet05Health packet = new Packet05Health(user.getName(),
							user.getHealth());
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
				} else if (p.isRunning() && p.isMovingRight()
						&& p.isFacingRight()) {
					p.setSpriteX(r);
					p.setSpriteY(0);
				} else if (p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(0);
					p.setSpriteY(0);
				}
				if (p.isWalking() && p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(w);
					p.setSpriteY(2);
				} else if (p.isRunning() && p.isMovingRight()
						&& !p.isFacingRight()) {
					p.setSpriteX(r);
					p.setSpriteY(2);
				} else if (p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(0);
					p.setSpriteY(2);
				}
				if (p.isWalking() && !p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(w);
					p.setSpriteY(1);
				} else if (p.isRunning() && !p.isMovingRight()
						&& p.isFacingRight()) {
					p.setSpriteX(r);
					p.setSpriteY(1);
				} else if (!p.isMovingRight() && p.isFacingRight()) {
					p.setSpriteX(0);
					p.setSpriteY(1);
				}
				if (p.isWalking() && !p.isMovingRight() && !p.isFacingRight()) {
					p.setSpriteX(w);
					p.setSpriteY(3);
				} else if (p.isRunning() && !p.isMovingRight()
						&& !p.isFacingRight()) {
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

	private class MovePlayers extends TimerTask {

		@Override
		public void run() {
			if (socketClient == null) {
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
							while (!new Rectangle(p.getBounds().x,
									p.getBounds().y - h + (64 - p.getHeight()),
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
							while (!new Rectangle(p.getBounds().x,
									p.getBounds().y - h, p.getBounds().width,
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
					} else if (p.getVeloY() > 0
							&& !p.checkCollisions().contains(3)) {
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
			} else {
				if (user.getVeloX() < 0 && !user.checkCollisions().contains(2)
						&& !user.isDead())
					user.setxPosition(user.getxPosition() + user.getVeloX());
				if (user.getVeloX() > 0 && !user.checkCollisions().contains(1)
						&& !user.isDead())
					user.setxPosition(user.getxPosition() + user.getVeloX());
				if (user.getVeloY() > 0 && !user.checkCollisions().contains(3)
						&& !user.isDead())
					user.setyPosition(user.getyPosition() + user.getVeloY());
				else if (user.getVeloY() > 0 && !user.isDead()) {
					int h = 0;
					outerloop: for (Rectangle r : getCollisionRecs()) {
						h = 0;
						while (!new Rectangle(user.getBounds().x,
								user.getBounds().y - h
										+ (64 - user.getHeight()),
								user.getBounds().width, user.getBounds().height)
								.intersects(r)) {
							if (h > user.getVeloY())
								continue outerloop;
							h++;
						}
						break;
					}
					user.setyPosition(user.getyPosition() + h - 1);
					user.setVeloY(-0.5);
				}
				if (user.getVeloY() < 0 && !user.checkCollisions().contains(4)
						&& !user.isDead())
					user.setyPosition(user.getyPosition() + user.getVeloY());
				else if (user.getVeloY() < 0 && !user.isDead()) {
					int h = 0;
					outerloop: for (Rectangle r : getCollisionRecs()) {
						h = 0;
						while (!new Rectangle(user.getBounds().x,
								user.getBounds().y - h, user.getBounds().width,
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
				} else if (user.getVeloY() > 0
						&& !user.checkCollisions().contains(3)) {
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
				if (user.canFall() && !user.isFalling() && !user.isJumping()
						&& !user.checkCollisions().contains(4)
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

	private class Repaint extends TimerTask {

		@Override
		public void run() {
			repaint();
		}
	}
}