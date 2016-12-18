package com.elveskevtar.divebomb.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MultiplayerMenu extends JLayeredPane {

	private static final long serialVersionUID = 7789569217705480234L;

	private static final Color TEXTFIELD_GREEN = new Color(7, 192, 44);
	private static final Color BUTTON_HOVERED_RED = new Color(191, 10, 28);
	private static final Color ALPHA_BLACK = new Color(0, 0, 0, 150);

	private boolean backPulse;
	private boolean privateGamePulse;
	private boolean joinGamePulse;

	private JFrame frame;
	private JButton back;
	private JButton privateGame;
	private JButton joinGame;

	private HintTextField ip;
	private HintTextField userName;

	private Font ipFont;
	private Font userNameFont;
	private Font backFont;
	private Font privateGameFont;
	private Font joinGameFont;

	/*
	 * Called by StartMenu class or other menus; most things are called in
	 * blocks of 3, one for each JButton
	 */
	public MultiplayerMenu(JFrame frame) {
		this.setFrame(frame);
		this.setSize(frame.getWidth() - frame.getInsets().left - frame.getInsets().right,
				frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom);
		this.setLayout(null);
		this.setFocusable(true);

		this.back = new JButton("Back");
		this.privateGame = new JButton("Create Private Game");
		this.joinGame = new JButton("Join Game");

		this.ipFont = new Font("Livewired", Font.PLAIN, getHeight() / 16);
		this.userNameFont = new Font("Livewired", Font.PLAIN, getHeight() / 16);
		this.backFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));
		this.privateGameFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));
		this.joinGameFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));

		this.ip = new HintTextField("Address:Port");
		this.userName = new HintTextField("Username");

		this.ip.setOpaque(false);
		this.userName.setOpaque(false);

		this.ip.setBorder(BorderFactory.createEmptyBorder());
		this.userName.setBorder(BorderFactory.createEmptyBorder());

		this.ip.setForeground(TEXTFIELD_GREEN);
		this.userName.setForeground(TEXTFIELD_GREEN);

		this.back.setForeground(ALPHA_BLACK);
		this.privateGame.setForeground(ALPHA_BLACK);
		this.joinGame.setForeground(ALPHA_BLACK);

		this.back.setMargin(new Insets(-100, -100, -100, -100));
		this.privateGame.setMargin(new Insets(-100, -100, -100, -100));
		this.joinGame.setMargin(new Insets(-100, -100, -100, -100));

		this.back.setFont(backFont);
		this.privateGame.setFont(privateGameFont);
		this.joinGame.setFont(joinGameFont);

		this.back.setBorderPainted(false);
		this.privateGame.setBorderPainted(false);
		this.joinGame.setBorderPainted(false);

		this.back.setContentAreaFilled(false);
		this.privateGame.setContentAreaFilled(false);
		this.joinGame.setContentAreaFilled(false);

		this.back.setFocusPainted(false);
		this.privateGame.setFocusPainted(false);
		this.joinGame.setFocusPainted(false);

		this.back.setOpaque(false);
		this.privateGame.setOpaque(false);
		this.joinGame.setOpaque(false);

		/* add action listeners to buttons (listens to clicks) */
		this.back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backPulse = false;
				backAction();
			}
		});
		this.privateGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				privateGamePulse = false;
				privateGameAction();
			}
		});
		this.joinGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				joinGamePulse = false;
				joinGameAction();
			}

		});

		/*
		 * Add mouse listeners to buttons (listens for mouse collision with
		 * button hit box to start pulsing)
		 */
		this.back.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				back.setForeground(BUTTON_HOVERED_RED);
				backPulse = true;
				new Thread(new ButtonPulse(back)).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				back.setForeground(ALPHA_BLACK);
				backPulse = false;
			}
		});
		this.privateGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				privateGame.setForeground(BUTTON_HOVERED_RED);
				privateGamePulse = true;
				new Thread(new ButtonPulse(privateGame)).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				privateGame.setForeground(ALPHA_BLACK);
				privateGamePulse = false;
			}
		});
		this.joinGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				joinGame.setForeground(BUTTON_HOVERED_RED);
				joinGamePulse = true;
				new Thread(new ButtonPulse(joinGame)).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				joinGame.setForeground(ALPHA_BLACK);
				joinGamePulse = false;
			}
		});

		/*
		 * Positioning for these buttons should center based on JFrame and
		 * JPanel bounds
		 */
		this.back.setBounds(0, 0, getWidth() / 8, getHeight() / 12);
		this.ip.setBounds(getWidth() / 8, getHeight() / 2 - getHeight() / 12, getWidth() / 4, getHeight() / 6);
		this.userName.setBounds(getWidth() * 5 / 8, getHeight() / 2 - getHeight() / 12, getWidth() / 4,
				getHeight() / 6);
		this.privateGame.setBounds(0, getHeight() - getHeight() / 12, getWidth() / 2, getHeight() / 12);
		this.joinGame.setBounds(getWidth() / 2, getHeight() - getHeight() / 12, getWidth() / 2, getHeight() / 12);

		this.ip.setFont(ipFont);
		this.userName.setFont(userNameFont);

		this.add(privateGame, -1);
		this.add(joinGame, -1);
		this.add(back, -1);
		this.add(ip, -1);
		this.add(userName, -1);
		this.add(new Background(), -1);

		this.joinGame.setEnabled(false);
		this.privateGame.setEnabled(false);

		/* starts a new thread to check if buttons can be enbaled */
		new Thread(new CheckForButtons()).start();
	}

	/* action methods are called on button clicks from action listeneres */
	public void backAction() {
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new StartMenu(getFrame()));
	}

	public void privateGameAction() {
		String userText = userName.getText();
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new PrivateGameMenu(getFrame(), userText));
	}

	public void joinGameAction() {
		String textIP = ip.getText();
		String userText = userName.getText();
		String IPtext = textIP.split(":")[0];
		int port = Integer.parseInt(textIP.split(":")[1]);
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new GameLobbyMenu(getFrame(), IPtext, userText, port));
	}

	/* standard get/set methods */
	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public JButton getBack() {
		return back;
	}

	public void setBack(JButton back) {
		this.back = back;
	}

	public HintTextField getIp() {
		return ip;
	}

	public void setIp(HintTextField ip) {
		this.ip = ip;
	}

	public JButton getPrivateGame() {
		return privateGame;
	}

	public void setPrivateGame(JButton privateGame) {
		this.privateGame = privateGame;
	}

	public JButton getJoinGame() {
		return joinGame;
	}

	public void setJoinGame(JButton joinGame) {
		this.joinGame = joinGame;
	}

	public HintTextField getUserName() {
		return userName;
	}

	public void setUserName(HintTextField userName) {
		this.userName = userName;
	}

	public Font getUserNameFont() {
		return userNameFont;
	}

	public void setUserNameFont(Font userNameFont) {
		this.userNameFont = userNameFont;
	}

	/*
	 * Thread that can be run to check and see if buttons should or should not
	 * be enabled; e.g. so that an ip address is required to join a game or a
	 * username is required to create/join one
	 */
	private class CheckForButtons extends Thread {

		@Override
		public void run() {
			while (isVisible()) {
				if (!userName.getText().isEmpty() && !userName.getText().equals(userName.getHint())) {
					privateGame.setEnabled(true);
					if (!ip.getText().isEmpty() && !ip.getText().equals(ip.getHint())
							&& ip.getText().split(":").length == 2) {
						String port = ip.getText().split(":")[1];
						boolean number = true;
						try {
							Integer.parseInt(port);
						} catch (NumberFormatException e) {
							number = false;
						}
						joinGame.setEnabled(number);
					} else {
						joinGame.setEnabled(false);
					}
				} else {
					privateGame.setEnabled(false);
					joinGame.setEnabled(false);
				}
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Class that implements Focus Listener so that explanation text is filled
	 * into the text boxes when the focus is not on that text box (when the box
	 * has not been clicked or has been clicked away from)
	 */
	private class HintTextField extends JTextField implements FocusListener {

		private static final long serialVersionUID = 4032504541558322544L;

		private final String hint;

		/* hint parameter is the text that should be filled in the text box */
		public HintTextField(final String hint) {
			super(hint);
			this.hint = hint;
			super.addFocusListener(this);
		}

		/* when box is clicked, empty box text */
		@Override
		public void focusGained(FocusEvent e) {
			if (this.getText().equals(hint))
				super.setText("");
		}

		/* when box is clicked away from, place hint back into box */
		@Override
		public void focusLost(FocusEvent e) {
			if (this.getText().isEmpty())
				super.setText(hint);
		}

		public String getHint() {
			return hint;
		}
	}

	private class Background extends JPanel {

		private static final long serialVersionUID = -678879425070878600L;

		private BufferedImage image;

		public Background() {
			this.setSize(MultiplayerMenu.this.getSize());
			this.setIgnoreRepaint(true);
			try {
				this.image = ImageIO.read(new File("res/img/DiveB0mbBackground.jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void paint(Graphics g) {
			g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		}
	}

	/* Thread for pulsing buttons; optimized to not lag program */
	private class ButtonPulse extends Thread {

		private JButton button;

		public ButtonPulse(JButton button) {
			this.button = button;
		}

		/*
		 * Pulsing is done by changing the font size of individual buttons using
		 * an absolute value sine function; when pulsing is done, old font size
		 * is set again; uses system time to avoid laggy pulsing;each
		 * buttonPulse boolean variable is dealt with separately; this can be
		 * optimized in the future
		 */
		@Override
		public void run() {
			long oldTime;
			long newTime;
			if (button.getText().equals("Back")) {
				double i = 0;
				newTime = System.currentTimeMillis();
				oldTime = newTime;
				while (backPulse) {
					newTime = System.currentTimeMillis();
					long difference = newTime - oldTime;
					i += difference / 250.0;
					Font newFont = new Font("Livewired", Font.PLAIN,
							(int) ((getWidth() / 38.4 * Math.abs(Math.sin(i))) + getWidth() / 38.4));
					button.setFont(newFont);
					oldTime = newTime;
				}
				Font oldFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));
				button.setFont(oldFont);
			} else if (button.getText().equals("Create Private Game")) {
				double i = 0;
				newTime = System.currentTimeMillis();
				oldTime = newTime;
				while (privateGamePulse) {
					newTime = System.currentTimeMillis();
					long difference = newTime - oldTime;
					i += difference / 250.0;
					Font newFont = new Font("Livewired", Font.PLAIN,
							(int) ((getWidth() / 43.4 * Math.abs(Math.sin(i))) + getWidth() / 38.4));
					button.setFont(newFont);
					oldTime = newTime;
				}
				Font oldFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));
				button.setFont(oldFont);
			} else if (button.getText().equals("Join Game")) {
				double i = 0;
				newTime = System.currentTimeMillis();
				oldTime = newTime;
				while (joinGamePulse) {
					newTime = System.currentTimeMillis();
					long difference = newTime - oldTime;
					i += difference / 250.0;
					Font newFont = new Font("Livewired", Font.PLAIN,
							(int) ((getWidth() / 38.4 * Math.abs(Math.sin(i))) + getWidth() / 38.4));
					button.setFont(newFont);
					oldTime = newTime;
				}
				Font oldFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));
				button.setFont(oldFont);
			}
		}
	}
}