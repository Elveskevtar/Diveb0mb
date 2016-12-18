package com.elveskevtar.divebomb.net.publicserver;

import java.util.Scanner;

import com.elveskevtar.divebomb.gfx.GameDeathmatchMP;

public class ServerDriver {

	public static void main(String[] args) {
		System.out.println("Port: ");
		@SuppressWarnings("resource")
		int port = new Scanner(System.in).nextInt();
		new GameDeathmatchMP("res/img/Map.png", "res/img/CollisionMap.png", 0, port);
	}
}