package com.elveskevtar.divebomb.gfx;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import com.elveskevtar.divebomb.maps.Map;
import com.elveskevtar.divebomb.race.Cyborg;
import com.elveskevtar.divebomb.race.Player;
import com.elveskevtar.divebomb.weapons.Sword;

public class GameDeathmatch extends Game {

	private static final long serialVersionUID = -1382236417102373948L;

	private Player player1;
	private Player player2;
	private Player player3;
	private Player player4;
	private Player player5;
	private Player player6;
	private Player player7;
	private Player player8;

	public GameDeathmatch(JFrame frame, Map map) {
		super(00, frame.getWidth(), frame.getHeight(), frame);
		this.player1 = getUser();
		try {
			this.player2 = new Cyborg(this, InetAddress.getLocalHost(), 6768);
			this.player3 = new Cyborg(this, InetAddress.getLocalHost(), 6769);
			this.player4 = new Cyborg(this, InetAddress.getLocalHost(), 6770);
			this.player5 = new Cyborg(this, InetAddress.getLocalHost(), 6771);
			this.player6 = new Cyborg(this, InetAddress.getLocalHost(), 6772);
			this.player7 = new Cyborg(this, InetAddress.getLocalHost(), 6773);
			this.player8 = new Cyborg(this, InetAddress.getLocalHost(), 6774);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.player2.setInHand(new Sword(player2));
		this.player3.setInHand(new Sword(player3));
		this.player4.setInHand(new Sword(player4));
		this.player5.setInHand(new Sword(player5));
		this.player6.setInHand(new Sword(player6));
		this.player7.setInHand(new Sword(player7));
		this.player8.setInHand(new Sword(player8));
		this.getPlayers().add(player2);
		this.getPlayers().add(player3);
		this.getPlayers().add(player4);
		this.getPlayers().add(player5);
		this.getPlayers().add(player6);
		this.getPlayers().add(player7);
		this.getPlayers().add(player8);
		this.setPlayerSize(8);
		this.startGame(map);
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public Player getPlayer3() {
		return player3;
	}

	public void setPlayer3(Player player3) {
		this.player3 = player3;
	}

	public Player getPlayer4() {
		return player4;
	}

	public void setPlayer4(Player player4) {
		this.player4 = player4;
	}

	public Player getPlayer5() {
		return player5;
	}

	public void setPlayer5(Player player5) {
		this.player5 = player5;
	}

	public Player getPlayer6() {
		return player6;
	}

	public void setPlayer6(Player player6) {
		this.player6 = player6;
	}

	public Player getPlayer7() {
		return player7;
	}

	public void setPlayer7(Player player7) {
		this.player7 = player7;
	}

	public Player getPlayer8() {
		return player8;
	}

	public void setPlayer8(Player player8) {
		this.player8 = player8;
	}
}
