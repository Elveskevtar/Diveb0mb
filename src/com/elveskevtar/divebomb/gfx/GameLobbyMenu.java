package com.elveskevtar.divebomb.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.elveskevtar.divebomb.net.packets.Packet10UpdateUserInfo;
import com.elveskevtar.divebomb.race.Player.PlayerTypes;

public class GameLobbyMenu extends JPanel implements KeyListener {

	private static final long serialVersionUID = -3823209600661844336L;
	private boolean switchRunning;
	private int raceSelectionPointer;
	private int xOffSet;

	private ArrayList<BufferedImage> races = new ArrayList<BufferedImage>();
	private JFrame frame;
	private String ip;
	private String weapon = "bow";
	private Game game;

	public GameLobbyMenu(JFrame frame, String username) {
		this.frame = frame;
		this.setLayout(null);
		this.setDoubleBuffered(true);
		this.setSize(frame.getWidth(), frame.getHeight());
		this.setFocusable(true);
		this.addKeyListener(this);
		this.game = new GameDeathmatchMP("res/img/Map.png",
				"res/img/CollisionMap.png", 0, frame, username);
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
	}

	public GameLobbyMenu(JFrame frame, String ip, String username) {
		this.frame = frame;
		this.ip = ip;
		this.setLayout(null);
		this.setDoubleBuffered(true);
		this.setSize(frame.getWidth(), frame.getHeight());
		this.setFocusable(true);
		this.addKeyListener(this);
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
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(g2d.getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("Arial Black", Font.BOLD, getHeight() / 8));
		if (game.getLobbyTime() != -1)
			g2d.drawString(Integer.toString(game.getLobbyTime()), 0, g2d
					.getFont().getSize() * 3 / 4);
		else
			g2d.drawString("Waiting For Players...", 0,
					g2d.getFont().getSize() * 3 / 4);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (BufferedImage race : races) {
			g2d.drawImage(
					race,
					(int) ((getWidth() / 2 - getWidth() / 8)
							+ (races.indexOf(race) - raceSelectionPointer)
							* 1.5 * (getWidth() / 4) + xOffSet),
					getHeight() / 8,
					(int) ((getWidth() / 2 + getWidth() / 8)
							+ (races.indexOf(race) - raceSelectionPointer)
							* 1.5 * (getWidth() / 4) + xOffSet),
					getHeight() * 7 / 8, 0, 0, 32, 64, null);
		}
		if (game.getLobbyTime() == 0)
			initGame();
		requestFocusInWindow();
		repaint();
	}

	public void initGame() {
		game.setLobbyTime(-1);
		Packet10UpdateUserInfo packet = new Packet10UpdateUserInfo(
				game.getUserName(), game.getUserRace(), game.getUserColor(),
				weapon);
		packet.writeData(game.getSocketClient());
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(game);
	}

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

	public String getWeapon() {
		return weapon;
	}

	public void setWeapon(String weapon) {
		this.weapon = weapon;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT)
				&& raceSelectionPointer < PlayerTypes.values().length - 1
				&& !switchRunning) {
			switchRunning = true;
			new Thread(new SwitchRace(1)).start();
			repaint();
		}
		if ((e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT)
				&& raceSelectionPointer > 0 && !switchRunning) {
			switchRunning = true;
			new Thread(new SwitchRace(-1)).start();
			repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

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

	public int getxOffSet() {
		return xOffSet;
	}

	public void setxOffSet(int xOffSet) {
		this.xOffSet = xOffSet;
	}

	public boolean isSwitchRunning() {
		return switchRunning;
	}

	public void setSwitchRunning(boolean switchRunning) {
		this.switchRunning = switchRunning;
	}

	public class SwitchRace extends Thread {

		private int sign;

		public SwitchRace(int sign) {
			this.sign = sign;
		}

		@Override
		public void run() {
			if (sign == 1) {
				while (xOffSet > -1.5 * (getWidth() / 4)) {
					xOffSet -= 5;
					try {
						Thread.sleep(1, 0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				xOffSet = 0;
				raceSelectionPointer++;
			} else {
				while (xOffSet < 1.5 * (getWidth() / 4)) {
					xOffSet += 5;
					try {
						Thread.sleep(1, 0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				xOffSet = 0;
				raceSelectionPointer--;
			}
			switchRunning = false;
			game.setUserRace(PlayerTypes.values()[raceSelectionPointer]
					.getName());
		}
	}
}
