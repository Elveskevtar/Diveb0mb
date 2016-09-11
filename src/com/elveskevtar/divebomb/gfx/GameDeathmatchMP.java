package com.elveskevtar.divebomb.gfx;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ConcurrentModificationException;

import javax.swing.JFrame;

import com.elveskevtar.divebomb.maps.Map;
import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;
import com.elveskevtar.divebomb.net.packets.Packet00Login;
import com.elveskevtar.divebomb.net.packets.Packet03Move;
import com.elveskevtar.divebomb.net.packets.Packet05Health;
import com.elveskevtar.divebomb.net.packets.Packet07Endgame;
import com.elveskevtar.divebomb.net.packets.Packet14UpdateProjectile;
import com.elveskevtar.divebomb.race.Player;
import com.elveskevtar.divebomb.weapons.Bow;
import com.elveskevtar.divebomb.weapons.Projectile;
import com.elveskevtar.divebomb.weapons.ProjectileShooter;
import com.elveskevtar.divebomb.weapons.Sword;

public class GameDeathmatchMP extends Game {

	private static final long serialVersionUID = 1495382359826347033L;
	private int firstPlaceKills;
	private int maxKills;

	private String firstPlaceName;

	public GameDeathmatchMP(String graphicsMapName, String collisionMapName, int id, JFrame frame, String username) {
		super(01, frame);
		this.setPlayerSize(2);
		this.setMaxKills(3);
		this.setGraphicsMap(new Map(graphicsMapName, collisionMapName, id));
		this.setSocketServer(new GameServer(this));
		this.getSocketServer().start();
		this.setHosting(true);
		this.setServerIP("localhost");
		this.setSocketClient(new GameClient(this, getServerIP()));
		this.getSocketClient().start();
		this.setUserName(username);
		String weapon = " ";
		if (getUser().getInHand() instanceof Sword)
			weapon = "sword";
		if (getUser().getInHand() instanceof Bow)
			weapon = "bow";
		Packet00Login packet = new Packet00Login(getUserName(), getUserRace(), getUserColor(), weapon);
		try {
			getSocketClient().setIP(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println(getSocketClient().getIP());
		packet.writeData(getSocketClient());
	}

	public GameDeathmatchMP(String graphicsMapName, String collisionMapName, int id) {
		super(01);
		this.setPlayerSize(2);
		this.setMaxKills(3);
		this.setGraphicsMap(new Map(graphicsMapName, collisionMapName, id));
		this.setSocketServer(new GameServer(this));
		this.getSocketServer().start();
		this.setHosting(true);
		this.setServerIP("localhost");
	}

	public GameDeathmatchMP(String ip, JFrame frame, String username) {
		super(01, frame);
		this.setPlayerSize(2);
		this.setMaxKills(3);
		this.setServerIP(ip);
		this.setSocketClient(new GameClient(this, getServerIP()));
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
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		packet.writeData(getSocketClient());
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate((-getWidth() * (0.5 * getZoom() - 0.5) * (1.0 / getZoom())) * -1,
				(-getHeight() * (0.5 * getZoom() - 0.5) * (1.0 / getZoom())) * -1);
		g2d.setFont(new Font("Livewired", Font.PLAIN, 20 / getZoom()));
		g2d.drawString("Health: " + getUser().getHealth(), 0, g2d.getFont().getSize() * 3 / 4);
		g2d.drawString("Stamina: " + getUser().getStamina(), 0, g2d.getFont().getSize() * 15 / 8);
		g2d.drawString("Kills: " + getUser().getKills(), 0, g2d.getFont().getSize() * 3);
		g2d.drawString("Deaths: " + getUser().getDeaths(), 0, g2d.getFont().getSize() * 33 / 8);
	}

	@Override
	public void setTimers() {
		super.setTimers();
		if (getSocketClient() != null)
			new Thread(new SendMovePacket()).start();
		if (getSocketServer() != null) {
			new Thread(new SendHealthPacket()).start();
			new Thread(new CheckForEndGame()).start();
			new Thread(new UpdateProjectiles()).start();
		}
	}

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

	private class UpdateProjectiles extends Thread {

		@Override
		public void run() {
			while (isRunning()) {
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

	private class SendMovePacket extends Thread {

		@Override
		public void run() {
			while (isRunning()) {
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

	private class SendHealthPacket extends Thread {

		@Override
		public void run() {
			while (isRunning()) {
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

	private class CheckForEndGame extends Thread {

		@Override
		public void run() {
			while (true) {
				if (firstPlaceKills >= maxKills) {
					Packet07Endgame packet = new Packet07Endgame(firstPlaceName, firstPlaceKills);
					packet.writeData(getSocketServer());
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
}
