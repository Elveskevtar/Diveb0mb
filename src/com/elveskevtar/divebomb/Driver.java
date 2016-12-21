package com.elveskevtar.divebomb;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import com.elveskevtar.divebomb.gfx.Frame;

/**
 * The Driver class holds the main method for clients, registers fonts, checks
 * screen resolution and system requirements before calling the Frame class.
 * 
 * @author Elveskevtar
 * @version 0.0.1-pre-pre-alpha
 */
public class Driver {

	/**
	 * The main method for clients. Registers the livewired fonts to the
	 * Graphics Environment while catching IOExceptions and
	 * FontFormatExceptions. Then, checks system requirements. Finally, calls
	 * Frame using maximum system requirements deemed playable.
	 * 
	 * @param args
	 *            N/A since this will not be run from a console
	 * @see com.elveskevtar.divebomb.gfx.Frame
	 * @see java.awt.GraphicsEnvironment
	 * @see java.io.IOException
	 * @see java.awt.FontFormatException
	 */
	public static void main(String[] args) {
		try {
			/*
			 * initialize GraphicsEnvironment object by obtaining the local
			 * graphics environment
			 */
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

			/* using the GraphicsEnvironment object to register fonts */
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/font/livewired.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/font/livewiredacadital.ttf")));
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}

		/* calling the Frame class to construct the window */
		new Frame(1280, 720);
	}
}