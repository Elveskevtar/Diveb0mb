package com.elveskevtar.divebomb.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MultiplayerMenu extends JPanel {

	private static final long serialVersionUID = 7789569217705480234L;

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

	public MultiplayerMenu(JFrame frame) {
		this.setFrame(frame);
		this.setSize(frame.getWidth(), frame.getHeight());
		this.setLayout(null);
		this.setFocusable(true);

		this.back = new JButton("Back");
		this.privateGame = new JButton("Create Private Game");
		this.joinGame = new JButton("Join Game");

		this.ipFont = new Font("Livewired", Font.PLAIN, getHeight() / 24);
		this.userNameFont = new Font("Livewired", Font.PLAIN, getHeight() / 24);
		this.backFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));
		this.privateGameFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));
		this.joinGameFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));

		this.ip = new HintTextField("IP Address");
		this.userName = new HintTextField("Username");

		this.back.setForeground(Color.BLACK);
		this.privateGame.setForeground(Color.BLACK);
		this.joinGame.setForeground(Color.BLACK);

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

		this.back.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				back.setForeground(Color.RED);
				backPulse = true;
				new Thread(new ButtonPulse(back)).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				back.setForeground(Color.BLACK);
				backPulse = false;
			}
		});
		this.privateGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				privateGame.setForeground(Color.RED);
				privateGamePulse = true;
				new Thread(new ButtonPulse(privateGame)).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				privateGame.setForeground(Color.BLACK);
				privateGamePulse = false;
			}
		});
		this.joinGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				joinGame.setForeground(Color.RED);
				joinGamePulse = true;
				new Thread(new ButtonPulse(joinGame)).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				joinGame.setForeground(Color.BLACK);
				joinGamePulse = false;
			}
		});

		this.back.setBounds(0, 0, getWidth() / 8, getHeight() / 12);
		this.ip.setBounds(getWidth() / 2 - getWidth() / 4, getHeight() / 2 - getHeight() / 12, getWidth() / 4,
				getHeight() / 12);
		this.userName.setBounds(getWidth() / 2, getHeight() / 2 - getHeight() / 12, getWidth() / 4, getHeight() / 12);
		this.privateGame.setBounds(0, getHeight() - getHeight() / 12, getWidth() / 2, getHeight() / 12);
		this.joinGame.setBounds(getWidth() / 2, getHeight() - getHeight() / 12, getWidth() / 2, getHeight() / 12);

		this.ip.setFont(ipFont);
		this.userName.setFont(userNameFont);

		this.add(privateGame);
		this.add(joinGame);
		this.add(back);
		this.add(ip);
		this.add(userName);

		this.joinGame.setEnabled(false);
		this.privateGame.setEnabled(false);

		new Thread(new CheckForButtons()).start();
	}

	public void backAction() {
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new StartMenu(getFrame()));
	}

	public void privateGameAction() {
		String userText = userName.getText();
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new GameLobbyMenu(getFrame(), userText));
	}

	public void joinGameAction() {
		String textIP = ip.getText();
		String userText = userName.getText();
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new GameLobbyMenu(getFrame(), textIP, userText));
	}

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

	private class CheckForButtons extends Thread {

		@Override
		public void run() {
			while (isVisible()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!userName.getText().isEmpty() && !userName.getText().equals(userName.getHint())) {
					privateGame.setEnabled(true);
					if (!ip.getText().isEmpty() && !ip.getText().equals(ip.getHint()))
						joinGame.setEnabled(true);
					else
						joinGame.setEnabled(false);
				} else {
					privateGame.setEnabled(false);
					joinGame.setEnabled(false);
				}
			}
		}
	}

	private class HintTextField extends JTextField implements FocusListener {

		private static final long serialVersionUID = 4032504541558322544L;
		private final String hint;

		public HintTextField(final String hint) {
			super(hint);
			this.hint = hint;
			super.addFocusListener(this);
		}

		@Override
		public void focusGained(FocusEvent e) {
			if (this.getText().equals(hint))
				super.setText("");
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (this.getText().isEmpty())
				super.setText(hint);
		}

		public String getHint() {
			return hint;
		}
	}

	private class ButtonPulse extends Thread {

		private JButton button;

		public ButtonPulse(JButton button) {
			this.button = button;
		}

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
