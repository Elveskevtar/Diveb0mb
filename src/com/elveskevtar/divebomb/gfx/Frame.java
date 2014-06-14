package com.elveskevtar.divebomb.gfx;

import java.awt.Toolkit;

import javax.swing.JFrame;

public class Frame extends JFrame {

	private static final long serialVersionUID = -5352440889178544005L;

	public Frame() {
		super();
		this.setUndecorated(true);
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		this.add(new StartMenu(this));
		this.setVisible(true);
	}

	public Frame(int width, int height) {
		super("DiveBomb");
		this.setSize(width, height);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(new StartMenu(this));
		this.setVisible(true);
	}
}
