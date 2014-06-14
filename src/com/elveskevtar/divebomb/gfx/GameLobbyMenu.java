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
	private int raceSelectionPointer;

	private ArrayList<BufferedImage> races = new ArrayList<BufferedImage>();
	private JFrame frame;
	private String ip;
	private String weapon;
	private Game game;

	public GameLobbyMenu(JFrame frame, String username) {
		this.frame = frame;
		this.setLayout(null);
		this.setSize(frame.getWidth(), frame.getHeight());
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
		this.setSize(frame.getWidth(), frame.getHeight());
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
			g2d.drawImage(race, getWidth() / 2 , arg2, getWidth() / 4, getWidth() / 2, 0, 0, 32, 64, null);
		}
		if (game.getLobbyTime() == 0)
			initGame();
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
		getFrame().repaint();
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
		if (e.getKeyCode() == KeyEvent.VK_D
				|| e.getKeyCode() == KeyEvent.VK_RIGHT) {

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
}
