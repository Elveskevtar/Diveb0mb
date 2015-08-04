package com.elveskevtar.divebomb.net.publicserver;

import com.elveskevtar.divebomb.gfx.GameDeathmatchMP;

public class ServerDriver {

	public static void main(String[] args) {
		new GameDeathmatchMP("res/img/Map.png", "res/img/CollisionMap.png", 0);
	}
}
