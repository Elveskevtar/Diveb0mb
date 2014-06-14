package com.elveskevtar.divebomb.gfx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class StartMenu extends JPanel {

	private static final long serialVersionUID = -229487886910550057L;

	private JFrame frame;
	private JButton campaign;
	private JButton multiplayer;
	private JButton options;
	private JButton exit;

	public StartMenu(JFrame frame) {
		this.setFrame(frame);
		this.setSize(frame.getWidth(), frame.getHeight());
		this.setLayout(null);
		this.campaign = new JButton("Campaign");
		this.multiplayer = new JButton("Multiplayer");
		this.options = new JButton("Options");
		this.exit = new JButton("Exit Game");
		this.campaign.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				campaignAction();
			}
		});
		this.multiplayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				multiplayerAction();
			}
		});
		this.options.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optionsAction();
			}
		});
		this.exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exitAction();
			}
		});
		this.campaign.setBounds(getWidth() / 2 - getWidth() / 6,
				getHeight() / 2, getWidth() / 3, getHeight() / 16);
		this.multiplayer.setBounds(getWidth() / 2 - getWidth() / 6,
				(int) (getHeight() / 2 + getHeight() / 8), getWidth() / 3,
				getHeight() / 16);
		this.options.setBounds(getWidth() / 2 - getWidth() / 6,
				(int) (getHeight() / 2 + getHeight() / 8 * 2), getWidth() / 3,
				getHeight() / 16);
		this.exit.setBounds(getWidth() / 2 - getWidth() / 6,
				(int) (getHeight() / 2 + getHeight() / 8 * 3), getWidth() / 3,
				getHeight() / 16);
		this.add(campaign);
		this.add(multiplayer);
		this.add(options);
		this.add(exit);
	}

	public void campaignAction() {
		
	}

	public void multiplayerAction() {
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new MultiplayerMenu(getFrame()));
		getFrame().repaint();
	}

	public void optionsAction() {
		
	}

	public void exitAction() {
		System.exit(0);
	}

	public JButton getCampaign() {
		return campaign;
	}

	public void setCampaign(JButton campaign) {
		this.campaign = campaign;
	}

	public JButton getMultiplayer() {
		return multiplayer;
	}

	public void setMultiplayer(JButton multiplayer) {
		this.multiplayer = multiplayer;
	}

	public JButton getOptions() {
		return options;
	}

	public void setOptions(JButton options) {
		this.options = options;
	}

	public JButton getExit() {
		return exit;
	}

	public void setExit(JButton exit) {
		this.exit = exit;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
}
