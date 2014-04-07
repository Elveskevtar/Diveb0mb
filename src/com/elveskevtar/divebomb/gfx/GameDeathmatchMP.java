package com.elveskevtar.divebomb.gfx;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import com.elveskevtar.divebomb.net.GameClient;
import com.elveskevtar.divebomb.net.GameServer;
import com.elveskevtar.divebomb.net.packets.Packet00Login;

public class GameDeathmatchMP extends Game {

	private static final long serialVersionUID = 1495382359826347033L;

	public GameDeathmatchMP() {
		super();
		this.setPlayerSize(8);
		if (JOptionPane.showConfirmDialog(this,
				"Do you want to host the server?") == 0) {
			setSocketServer(new GameServer(this));
			getSocketServer().start();
			setHosting(true);
		} else {
			setServerIP(JOptionPane.showInputDialog(this,
					"What is the ip of the server:"));
		}
		setSocketClient(new GameClient(this, getServerIP()));
		getSocketClient().start();
		Packet00Login packet = new Packet00Login(getUserName(), getUserRace(),
				getUserColor());
		if (isHosting()) {
			try {
				getSocketClient().setIP(InetAddress.getLocalHost());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
            getSocketServer().addConnection(getUser(), packet);
        }
		packet = new Packet00Login(getUserName(), getUserRace(),
				getUserColor());
        packet.writeData(getSocketClient());
	}

	public GameDeathmatchMP(int width, int height) {
		super(width, height);
		this.setPlayerSize(8);
		if (JOptionPane.showConfirmDialog(this,
				"Do you want to host the server?") == 0) {
			setSocketServer(new GameServer(this));
			getSocketServer().start();
			setHosting(true);
		} else {
			setServerIP(JOptionPane.showInputDialog(this,
					"What is the ip of the server:"));
		}
		setSocketClient(new GameClient(this, getServerIP()));
		getSocketClient().start();
		Packet00Login packet = new Packet00Login(getUserName(), getUserRace(),
				getUserColor());
		if (isHosting()) {
            getSocketServer().addConnection(getUser(), packet);
        }
        packet.writeData(getSocketClient());
	}
}
