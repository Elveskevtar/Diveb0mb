package com.elveskevtar.divebomb.gfx;

import javax.swing.JFrame;

/**
 * This is a JFrame objects that sets all of the default JFrame properties
 * within the constructor. Besides that, this class is just a way to initialize
 * the window in which all of the GUI will be painted on. Usually directly adds
 * components such as JPanels and JLayeredPanes directly onto the window while
 * JButtons and JTextFields are added to the JPanels and subcomponents of the
 * JFrame. Overall, creates a very structured system of painting things on the
 * screen in the order necessary.
 * 
 * @author Elveskevtar
 * @since 0.0.1-pre-pre-alpha
 */
public class Frame extends JFrame {

	private static final long serialVersionUID = -5352440889178544005L;

	/**
	 * Is always called by the Driver class which creates a new Frame object
	 * which in turn, starts the program. Serves as the bordered screen
	 * constructor. All user operated windows will start off bordered and with
	 * the properties specified until some can be manually changed in the
	 * options menu:<br>
	 * <ul>
	 * <li>Title of Window: DiveBomb (unchangeable)</li>
	 * <li>Screen Size: Given by Driver class, also takes into account insets
	 * when using <code>this.setSize(int width, int height)</code></li>
	 * <li>Screen Properties: Bordered and not fullscreen</li>
	 * <li>Position on Monitor: Centered</li>
	 * <li>Layout: Manual a.k.a. no layout or null</li>
	 * <li>Default Close Operation: JFrame.EXIT_ON_CLOSE</li>
	 * </ul>
	 * Finally, the StartMenu is added for which the user can begin navigating
	 * the game world through a main menu.
	 * 
	 * @param width
	 *            The width of the screen not including insets
	 * @param height
	 *            The height of the screen not including insets
	 * @see javax.swing.JFrame
	 * @see com.elveskevtar.divebomb.gfx.StartMenu
	 * @see com.elveskevtar.divebomb.Driver
	 */
	public Frame(int width, int height) {
		/* calls super constructor and sets title of frame to DiveBomb */
		super("DiveBomb");

		/*
		 * setSize(int width, int height) does not automatically take into
		 * account insets of jframe so this statement does it manually
		 */
		this.setSize(getInsets().left + getInsets().right + width, getInsets().top + getInsets().bottom + height);

		/* user can only resize screen via the options menu */
		this.setResizable(false);

		/*
		 * after the screen size has been initially set, this statement centers
		 * the window on the screen
		 */
		this.setLocationRelativeTo(null);

		/* sets no layout so that everything can be drawn manually */
		this.setLayout(null);

		/*
		 * this statement allows users to end the entire program by graphically
		 * closing the game using the red X
		 */
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/*
		 * sets the preferred size of the window and then packs it; the
		 * difference between this and the previous setSize method is that this
		 * one makes the visible portion of the window to match width and height
		 * with the insets increasing the total window width and height
		 */
		this.setPreferredSize(getSize());
		this.pack();

		/* adds the StartMenu JPanel to the window to start off */
		this.add(new StartMenu(this));

		/*
		 * sets the window visible; mainly only required for JFrames but not
		 * many other subcomponents
		 */
		this.setVisible(true);
	}
}