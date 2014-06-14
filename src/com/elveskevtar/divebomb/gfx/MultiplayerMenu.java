package com.elveskevtar.divebomb.gfx;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MultiplayerMenu extends JPanel {

	private static final long serialVersionUID = 7789569217705480234L;

	private JFrame frame;
	private JButton back;
	private JButton privateGame;
	private JButton joinGame;
	private HintTextField ip;
	private HintTextField userName;
	private Font ipFont;
	private Font userNameFont;

	public MultiplayerMenu(JFrame frame) {
		this.setFrame(frame);
		this.setSize(frame.getWidth(), frame.getHeight());
		this.setLayout(null);
		this.setFocusable(true);
		this.ipFont = new Font("Arial Black", Font.PLAIN, getHeight() / 24);
		this.userNameFont = new Font("Arial Black", Font.PLAIN,
				getHeight() / 24);
		this.back = new JButton("Back");
		this.privateGame = new JButton("Create Private Game");
		this.joinGame = new JButton("Join Game");
		this.ip = new HintTextField("IP Address");
		this.userName = new HintTextField("Username");
		this.back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backAction();
			}
		});
		this.privateGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				privateGameAction();
			}
		});
		this.joinGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				joinGameAction();
			}
		});
		this.back.setBounds(0, 0, getWidth() / 8, getHeight() / 12);
		this.ip.setBounds(getWidth() / 2 - getWidth() / 4, getHeight() / 2
				- getHeight() / 12, getWidth() / 4, getHeight() / 12);
		this.userName.setBounds(getWidth() / 2, getHeight() / 2 - getHeight()
				/ 12, getWidth() / 4, getHeight() / 12);
		this.privateGame.setBounds(0, getHeight() - getHeight() / 12,
				getWidth() / 2, getHeight() / 12);
		this.joinGame.setBounds(getWidth() / 2, getHeight() - getHeight() / 12,
				getWidth() / 2, getHeight() / 12);
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
		getFrame().repaint();
	}

	public void privateGameAction() {
		String userText = userName.getText();
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new GameLobbyMenu(getFrame(), userText));
		getFrame().repaint();
	}

	public void joinGameAction() {
		String textIP = ip.getText();
		String userText = userName.getText();
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new GameLobbyMenu(getFrame(), textIP, userText));
		getFrame().repaint();
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
				if (!userName.getText().isEmpty()
						&& !userName.getText().equals(userName.getHint())) {
					privateGame.setEnabled(true);
					if (!ip.getText().isEmpty()
							&& !ip.getText().equals(ip.getHint()))
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
}
