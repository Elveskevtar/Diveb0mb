package com.elveskevtar.divebomb.gfx;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Frame extends JFrame {

	private static final long serialVersionUID = -5352440889178544005L;

	private int width;
	private int height;

	/* borderless fullscreen constructor */
	public Frame() {
		super();
		/* sets the jframe to take up the entire screen and not have a border */
		this.setUndecorated(true);
		this.width = Toolkit.getDefaultToolkit().getScreenSize().width;
		this.height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		this.add(new StartMenu(this));
		this.setVisible(true);
	}

	/* bordered screen constructor */
	public Frame(int width, int height) {
		super("DiveBomb");
		this.width = width;
		this.height = height;
		/*
		 * setSize(int width, int height) does not automatically take into
		 * account insets of jframe
		 */
		this.setSize(getInsets().left + getInsets().right + width, getInsets().top + getInsets().bottom + height);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(width, height));
		this.pack();
		this.add(new StartMenu(this));
		this.setVisible(true);
	}

	/* standard getter/setter methods */
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}