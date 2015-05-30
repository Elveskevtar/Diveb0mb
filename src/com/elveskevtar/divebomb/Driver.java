package com.elveskevtar.divebomb;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import com.elveskevtar.divebomb.gfx.Frame;

public class Driver {

	public static void main(String[] args) {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(
					"res/font/livewired.ttf")));
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
		new Frame(1280, 720);
	}
}
