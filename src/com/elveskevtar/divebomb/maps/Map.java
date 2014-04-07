package com.elveskevtar.divebomb.maps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Map {
	
	private BufferedImage map;
	private BufferedImage collisionMap;
	
	private int id;

	public Map(String map, String collisionMap, int id) {
		try {
			this.map = ImageIO.read(new File(map));
			this.collisionMap = ImageIO.read(new File(collisionMap));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setId(id);
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
