package com.elveskevtar.divebomb.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.elveskevtar.divebomb.net.packets.Packet10UpdateUserInfo;
import com.elveskevtar.divebomb.race.Player.PlayerTypes;
import com.elveskevtar.divebomb.weapons.Melee.MeleeWeaponTypes;
import com.elveskevtar.divebomb.weapons.ProjectileShooter.ProjectileShooterTypes;

public class GameLobbyMenu extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -3823209600661844336L;

	private boolean switchRunning;

	private ArrayList<BufferedImage> races = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> meleeWeapons = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> rangedWeapons = new ArrayList<BufferedImage>();

	private int raceSelectionPointer;
	private int colorSelectionPointer;
	private int meleeSelectionPointer;
	private int rangedSelectionPointer;
	private int mouseSelection; // 1 = race; 2 = melee; 3 = ranged
	private int raceOffset;
	private int meleeOffset;
	private int rangedOffset;
	private int mouseX;
	private int mouseY;

	private JFrame frame;
	private String ip;
	private Game game;

	/* constructor for creating a private multiplayer game */
	public GameLobbyMenu(JFrame frame, String username) {
		this.frame = frame;
		this.setLayout(null);
		this.setDoubleBuffered(true);
		this.setSize(frame.getWidth() - frame.getInsets().left - frame.getInsets().right,
				frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom);
		this.setFocusable(true);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.game = new GameDeathmatchMP("res/img/Map.png", "res/img/CollisionMap.png", 0, frame, username);
		this.game.setLobbyTime(-1);
		this.game.getUser().setName(username);
		this.game.setUserName(username);
		for (PlayerTypes type : PlayerTypes.values()) {
			try {
				races.add(ImageIO.read(new File(type.getFiles()[0])));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (MeleeWeaponTypes type : MeleeWeaponTypes.values()) {
			try {
				meleeWeapons.add(ImageIO.read(new File(type.getFiles()[0])));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (ProjectileShooterTypes type : ProjectileShooterTypes.values()) {
			try {
				rangedWeapons.add(ImageIO.read(new File(type.getFiles()[0])));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (game.getSocketServer() == null)
			new Thread(new CheckForPingDrops()).start();
	}

	/* constructor for joining a private multiplayer game */
	public GameLobbyMenu(JFrame frame, String ip, String username) {
		this.frame = frame;
		this.ip = ip;
		this.setLayout(null);
		this.setDoubleBuffered(true);
		this.setSize(frame.getWidth() - frame.getInsets().left - frame.getInsets().right,
				frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom);
		this.setFocusable(true);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.game = new GameDeathmatchMP(ip, frame, username);
		this.game.setLobbyTime(-1);
		this.game.getUser().setName(username);
		this.game.setUserName(username);
		for (PlayerTypes type : PlayerTypes.values()) {
			try {
				races.add(ImageIO.read(new File(type.getFiles()[0])));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (MeleeWeaponTypes type : MeleeWeaponTypes.values()) {
			try {
				meleeWeapons.add(ImageIO.read(new File(type.getFiles()[0])));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (ProjectileShooterTypes type : ProjectileShooterTypes.values()) {
			try {
				rangedWeapons.add(ImageIO.read(new File(type.getFiles()[0])));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (game.getSocketServer() == null)
			new Thread(new CheckForPingDrops()).start();
	}

	/* paint the game lobby menu */
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(g2d.getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("Livewired", Font.BOLD, getHeight() / 8));
		if (game.getLobbyTime() != -1)
			g2d.drawString(Integer.toString(game.getLobbyTime()), 0, g2d.getFont().getSize() * 3 / 4);
		else
			g2d.drawString("Waiting For Players...", 0, g2d.getFont().getSize() * 3 / 4);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			races.set(raceSelectionPointer, ImageIO
					.read(new File(PlayerTypes.values()[raceSelectionPointer].getFiles()[colorSelectionPointer])));
		} catch (IOException e) {
			e.printStackTrace();
		}
		/* draw races */
		for (BufferedImage race : races) {
			g2d.drawImage(race,
					(int) ((getWidth() / 2 - getWidth() / 8)
							+ (races.indexOf(race) - raceSelectionPointer) * 1.5 * (getWidth() / 4) + raceOffset),
					getHeight() / 8,
					(int) ((getWidth() / 2 + getWidth() / 8)
							+ (races.indexOf(race) - raceSelectionPointer) * 1.5 * (getWidth() / 4) + raceOffset),
					getHeight() * 7 / 8, 0, 0, 32, 64, null);
			if (PlayerTypes.values()[raceSelectionPointer].getColors() != null) {
				g2d.setColor(new Color(33, 208, 235));
				g2d.fillRect(
						getWidth() / 2 - getWidth() / 8 + getWidth() / 32 + colorSelectionPointer % 2 * getWidth() / 8
								- 4,
						getHeight() / 8 + (colorSelectionPointer / 2 + 1) * getWidth() / 32 - 4, getWidth() / 16 + 8,
						getWidth() / 16 + 8);
				for (int i = 0; i < PlayerTypes.values()[raceSelectionPointer].getColors().length; i++) {
					g2d.setColor(PlayerTypes.values()[raceSelectionPointer].getColors()[i]);
					g2d.fillRect(getWidth() / 2 - getWidth() / 8 + getWidth() / 32 + i % 2 * getWidth() / 8,
							getHeight() / 8 + (i / 2 + 1) * getWidth() / 32, getWidth() / 16, getWidth() / 16);
				}
			}
		}
		/* draw melee weapons */
		for (BufferedImage melee : meleeWeapons) {
			if ((int) ((getWidth() / 4 - getWidth() / 20)
					+ (meleeWeapons.indexOf(melee) - meleeSelectionPointer) * 1.5 * (getWidth() / 10)
					+ meleeOffset) < getWidth() / 2 - getWidth() / 10)
				g2d.drawImage(melee,
						(int) ((getWidth() / 4 - getWidth() / 20)
								+ (meleeWeapons.indexOf(melee) - meleeSelectionPointer) * 1.5 * (getWidth() / 10)
								+ meleeOffset),
						getHeight() * 7 / 8,
						(int) ((getWidth() / 4 + getWidth() / 20)
								+ (meleeWeapons.indexOf(melee) - meleeSelectionPointer) * 1.5 * (getWidth() / 10)
								+ meleeOffset),
						getHeight(), 0, 0, 32, 32, null);
		}
		/* draw ranged weapons */
		for (BufferedImage ranged : rangedWeapons) {
			if ((int) ((getWidth() * 3 / 4 - getWidth() / 20)
					+ (rangedWeapons.indexOf(ranged) - rangedSelectionPointer) * 1.5 * (getWidth() / 10)
					+ rangedOffset) > getWidth() / 2)
				g2d.drawImage(ranged,
						(int) ((getWidth() * 3 / 4 - getWidth() / 20)
								+ (rangedWeapons.indexOf(ranged) - rangedSelectionPointer) * 1.5 * (getWidth() / 10)
								+ rangedOffset),
						getHeight() * 7 / 8,
						(int) ((getWidth() * 3 / 4 + getWidth() / 20)
								+ (rangedWeapons.indexOf(ranged) - rangedSelectionPointer) * 1.5 * (getWidth() / 10)
								+ rangedOffset),
						getHeight(), 0, 0, 32, 32, null);
		}
		/* draw slight tint based on where the mouse is */
		g2d.setColor(new Color(0, 0, 0, 20));
		if (mouseY < getHeight() * 7 / 8) {
			g2d.fillRect(0, getHeight() / 8, getWidth(), getHeight() * 3 / 4);
			mouseSelection = 1;
		} else if (mouseX < getWidth() / 2) {
			g2d.fillRect(0, getHeight() * 7 / 8, getWidth() / 2, getHeight() / 8);
			mouseSelection = 2;
		} else {
			g2d.fillRect(getWidth() / 2, getHeight() * 7 / 8, getWidth() / 2, getHeight() / 8);
			mouseSelection = 3;
		}
		/* draw gradient around melee and ranged */
		Paint meleePaint = new GradientPaint(0, 0,
				new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 255),
				getWidth() / 4, 0,
				new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 0), true);
		g2d.setPaint(meleePaint);
		g2d.fillRect(0, getHeight() * 7 / 8, getWidth() / 4, getHeight() / 8);
		meleePaint = new GradientPaint(getWidth() / 4, 0,
				new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 0),
				getWidth() / 2, 0,
				new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 255), true);
		g2d.setPaint(meleePaint);
		g2d.fillRect(getWidth() / 4, getHeight() * 7 / 8, getWidth() / 4, getHeight() / 8);
		Paint rangedPaint = new GradientPaint(getWidth() / 2, 0,
				new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 255),
				getWidth() * 3 / 4, 0,
				new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 0), true);
		g2d.setPaint(meleePaint);
		g2d.fillRect(getWidth() / 2, getHeight() * 7 / 8, getWidth() / 4, getHeight() / 8);
		rangedPaint = new GradientPaint(getWidth() * 3 / 4, 0,
				new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 0),
				getWidth(), 0,
				new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 255), true);
		g2d.setPaint(rangedPaint);
		g2d.fillRect(getWidth() * 3 / 4, getHeight() * 7 / 8, getWidth() / 4, getHeight() / 8);
		g2d.setColor(g2d.getBackground());
		if (game.getLobbyTime() == 0)
			initGame(); // start game when lobby time = 0
		requestFocusInWindow();
		repaint();
	}

	/* starts game */
	public void initGame() {
		game.setLobbyTime(-1); // sets lobby time to default value
		/* receive color values for race */
		if (PlayerTypes.values()[raceSelectionPointer].getColors() != null) {
			if (PlayerTypes.values()[raceSelectionPointer].getColors()[colorSelectionPointer].equals(Color.BLUE))
				game.setUserColor("blue");
			else if (PlayerTypes.values()[raceSelectionPointer].getColors()[colorSelectionPointer]
					.equals(new Color(76, 0, 153)))
				game.setUserColor("purple");
		}
		/* quick update to user info before start of game */
		Packet10UpdateUserInfo packet = new Packet10UpdateUserInfo(game.getUserName(), game.getUserRace(),
				game.getUserColor(), game.getUserMelee(), game.getUserRanged());
		packet.writeData(game.getSocketClient());
		/* replace lobby menu panel with game panel */
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(game);
	}

	/* standard get/set methods */
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public int getRaceSelectionPointer() {
		return raceSelectionPointer;
	}

	public void setRaceSelectionPointer(int raceSelectionPointer) {
		this.raceSelectionPointer = raceSelectionPointer;
	}

	public ArrayList<BufferedImage> getRaces() {
		return races;
	}

	public void setRaces(ArrayList<BufferedImage> races) {
		this.races = races;
	}

	public int getRaceOffset() {
		return raceOffset;
	}

	public void setRaceOffset(int raceOffset) {
		this.raceOffset = raceOffset;
	}

	public boolean isSwitchRunning() {
		return switchRunning;
	}

	public void setSwitchRunning(boolean switchRunning) {
		this.switchRunning = switchRunning;
	}

	public int getColorSelectionPointer() {
		return colorSelectionPointer;
	}

	public int getMeleeSelectionPointer() {
		return meleeSelectionPointer;
	}

	public void setMeleeSelectionPointer(int meleeSelectionPointer) {
		this.meleeSelectionPointer = meleeSelectionPointer;
	}

	public int getRangedSelectionPointer() {
		return rangedSelectionPointer;
	}

	public void setRangedSelectionPointer(int rangedSelectionPointer) {
		this.rangedSelectionPointer = rangedSelectionPointer;
	}

	public int getMeleeOffset() {
		return meleeOffset;
	}

	public void setMeleeOffset(int meleeOffset) {
		this.meleeOffset = meleeOffset;
	}

	public int getRangedOffset() {
		return rangedOffset;
	}

	public void setRangedOffset(int rangedOffset) {
		this.rangedOffset = rangedOffset;
	}

	public int getMouseX() {
		return mouseX;
	}

	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}

	public int getMouseSelection() {
		return mouseSelection;
	}

	public void setMouseSelection(int mouseSelection) {
		this.mouseSelection = mouseSelection;
	}

	public void setColorSelectionPointer(int colorSelectionPointer) {
		this.colorSelectionPointer = colorSelectionPointer;
	}

	/*
	 * Called when keys are pressed; check to see if user wants to switch
	 * between races, melee, or ranged
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) && mouseSelection == 1
				&& raceSelectionPointer < PlayerTypes.values().length - 1 && !switchRunning) {
			switchRunning = true;
			colorSelectionPointer = 0;
			new Thread(new SwitchRace(1)).start();
			repaint();
		}
		if ((e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) && mouseSelection == 1
				&& raceSelectionPointer > 0 && !switchRunning) {
			switchRunning = true;
			colorSelectionPointer = 0;
			new Thread(new SwitchRace(-1)).start();
			repaint();
		}
		if ((e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) && mouseSelection == 2
				&& meleeSelectionPointer < MeleeWeaponTypes.values().length - 1 && !switchRunning) {
			switchRunning = true;
			new Thread(new SwitchMelee(1)).start();
			repaint();
		}
		if ((e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) && mouseSelection == 2
				&& meleeSelectionPointer > 0 && !switchRunning) {
			switchRunning = true;
			new Thread(new SwitchMelee(-1)).start();
			repaint();
		}
		if ((e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) && mouseSelection == 3
				&& rangedSelectionPointer < ProjectileShooterTypes.values().length - 1 && !switchRunning) {
			switchRunning = true;
			new Thread(new SwitchRanged(1)).start();
			repaint();
		}
		if ((e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) && mouseSelection == 3
				&& rangedSelectionPointer > 0 && !switchRunning) {
			switchRunning = true;
			new Thread(new SwitchRanged(-1)).start();
			repaint();
		}
	}

	/* unnecessary key listener methods */
	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	/* checks for clicks on race colors */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (PlayerTypes.values()[raceSelectionPointer].getColors() != null)
			for (int i = 0; i < PlayerTypes.values()[raceSelectionPointer].getColors().length; i++)
				if (new Rectangle(getWidth() / 2 - getWidth() / 8 + getWidth() / 32 + i % 2 * getWidth() / 8,
						getHeight() / 8 + (i / 2 + 1) * getWidth() / 32, getWidth() / 16, getWidth() / 16)
								.intersects(e.getX(), e.getY(), 1, 1))
					colorSelectionPointer = i;
	}

	/* unnecessary mouse listener events */
	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	/* tracks mouse movement for other methods */
	@Override
	public void mouseMoved(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}

	/*
	 * Class that deals with the transition of races in the lobby; deals with
	 * both raceSelectionPointer and animation of switch
	 */
	private class SwitchRace extends Thread {

		private int sign;

		public SwitchRace(int sign) {
			this.sign = sign;
		}

		@Override
		public void run() {
			if (sign == 1) {
				while (raceOffset > -1.5 * (getWidth() / 4)) {
					raceOffset -= 2;
					try {
						Thread.sleep(0, 10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				raceOffset = 0;
				raceSelectionPointer++;
			} else {
				while (raceOffset < 1.5 * (getWidth() / 4)) {
					raceOffset += 2;
					try {
						Thread.sleep(0, 10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				raceOffset = 0;
				raceSelectionPointer--;
			}
			switchRunning = false;
			game.setUserRace(PlayerTypes.values()[raceSelectionPointer].getName());
		}
	}

	/*
	 * Class that deals with the transition of melee in the lobby; deals with
	 * both meleeSelectionPointer and animation of switch
	 */
	private class SwitchMelee extends Thread {

		private int sign;

		public SwitchMelee(int sign) {
			this.sign = sign;
		}

		@Override
		public void run() {
			if (sign == 1) {
				while (meleeOffset > -1.5 * (getWidth() / 10)) {
					meleeOffset -= 2;
					try {
						Thread.sleep(0, 10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				meleeOffset = 0;
				meleeSelectionPointer++;
			} else {
				while (meleeOffset < 1.5 * (getWidth() / 10)) {
					meleeOffset += 2;
					try {
						Thread.sleep(0, 10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				meleeOffset = 0;
				meleeSelectionPointer--;
			}
			switchRunning = false;
			game.setUserMelee(MeleeWeaponTypes.values()[meleeSelectionPointer].getName());
		}
	}

	/*
	 * Class that deals with the transition of ranged in the lobby; deals with
	 * both rangedSelectionPointer and animation of switch
	 */
	private class SwitchRanged extends Thread {

		private int sign;

		public SwitchRanged(int sign) {
			this.sign = sign;
		}

		@Override
		public void run() {
			if (sign == 1) {
				while (rangedOffset > -1.5 * (getWidth() / 10)) {
					rangedOffset -= 2;
					try {
						Thread.sleep(0, 10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				rangedOffset = 0;
				rangedSelectionPointer++;
			} else {
				while (rangedOffset < 1.5 * (getWidth() / 10)) {
					rangedOffset += 2;
					try {
						Thread.sleep(0, 10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				rangedOffset = 0;
				rangedSelectionPointer--;
			}
			switchRunning = false;
			game.setUserRanged(ProjectileShooterTypes.values()[rangedSelectionPointer].getName());
		}
	}

	/*
	 * checks for giant lag spikes and disconnects; has different uses for both
	 * server and client side
	 */
	private class CheckForPingDrops extends Thread {

		@Override
		public void run() {
			while (isVisible()) {
				if (((int) (game.getUser().getLatency() * Math.pow(10, -6)) >= 2000)
						&& game.getUser().getOldTimeStamp() != 0) {
					frame.setVisible(false);
					frame.remove(GameLobbyMenu.this);
					frame.add(new StartMenu(frame));
					frame.repaint();
					JOptionPane.showMessageDialog(getFrame(), "You have been unexpectedly disconnected from the server",
							"Server Disconnection", JOptionPane.INFORMATION_MESSAGE);
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