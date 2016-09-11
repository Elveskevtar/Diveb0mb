package com.elveskevtar.divebomb.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.elveskevtar.divebomb.maps.Map;

public class StartMenu extends JPanel {

	private static final long serialVersionUID = -229487886910550057L;

	private boolean campaignPulse;
	private boolean multiplayerPulse;
	private boolean optionsPulse;
	private boolean exitPulse;

	private JFrame frame;
	private JButton campaign;
	private JButton multiplayer;
	private JButton options;
	private JButton exit;

	private Font campaignFont;
	private Font multiplayerFont;
	private Font optionsFont;
	private Font exitFont;

	public StartMenu(JFrame frame) {
		this.setFrame(frame);
		this.setSize(frame.getWidth(), frame.getHeight());
		this.setLayout(null);

		this.campaign = new JButton("Campaign");
		this.multiplayer = new JButton("Multiplayer");
		this.options = new JButton("Options");
		this.exit = new JButton("Exit Game");

		this.campaignFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));
		this.multiplayerFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));
		this.optionsFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));
		this.exitFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));

		this.campaign.setForeground(Color.BLACK);
		this.multiplayer.setForeground(Color.BLACK);
		this.options.setForeground(Color.BLACK);
		this.exit.setForeground(Color.BLACK);

		this.campaign.setMargin(new Insets(-100, -100, -100, -100));
		this.multiplayer.setMargin(new Insets(-100, -100, -100, -100));
		this.options.setMargin(new Insets(-100, -100, -100, -100));
		this.exit.setMargin(new Insets(-100, -100, -100, -100));

		this.campaign.setFont(campaignFont);
		this.multiplayer.setFont(multiplayerFont);
		this.options.setFont(optionsFont);
		this.exit.setFont(exitFont);

		this.campaign.setBorderPainted(false);
		this.multiplayer.setBorderPainted(false);
		this.options.setBorderPainted(false);
		this.exit.setBorderPainted(false);

		this.campaign.setContentAreaFilled(false);
		this.multiplayer.setContentAreaFilled(false);
		this.options.setContentAreaFilled(false);
		this.exit.setContentAreaFilled(false);

		this.campaign.setFocusPainted(false);
		this.multiplayer.setFocusPainted(false);
		this.options.setFocusPainted(false);
		this.exit.setFocusPainted(false);

		this.campaign.setOpaque(false);
		this.multiplayer.setOpaque(false);
		this.options.setOpaque(false);
		this.exit.setOpaque(false);

		this.campaign.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				campaignPulse = false;
				campaignAction();
			}
		});
		this.multiplayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				multiplayerPulse = false;
				multiplayerAction();
			}
		});
		this.options.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optionsPulse = false;
				optionsAction();
			}
		});
		this.exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exitPulse = false;
				exitAction();
			}
		});

		this.campaign.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				campaign.setForeground(Color.RED);
				campaignPulse = true;
				new Thread(new ButtonPulse(campaign)).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				campaign.setForeground(Color.BLACK);
				campaignPulse = false;
			}
		});
		this.multiplayer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				multiplayer.setForeground(Color.RED);
				multiplayerPulse = true;
				new Thread(new ButtonPulse(multiplayer)).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				multiplayer.setForeground(Color.BLACK);
				multiplayerPulse = false;
			}
		});
		this.options.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				options.setForeground(Color.RED);
				optionsPulse = true;
				new Thread(new ButtonPulse(options)).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				options.setForeground(Color.BLACK);
				optionsPulse = false;
			}
		});
		this.exit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				exit.setForeground(Color.RED);
				exitPulse = true;
				new Thread(new ButtonPulse(exit)).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				exit.setForeground(Color.BLACK);
				exitPulse = false;
			}
		});

		this.campaign.setBounds(getWidth() / 2 - getWidth() / 6, getHeight() / 2, getWidth() / 3, getHeight() / 16);
		this.multiplayer.setBounds(getWidth() / 2 - getWidth() / 6, (int) (getHeight() / 2 + getHeight() / 8),
				getWidth() / 3, getHeight() / 16);
		this.options.setBounds(getWidth() / 2 - getWidth() / 6, (int) (getHeight() / 2 + getHeight() / 8 * 2),
				getWidth() / 3, getHeight() / 16);
		this.exit.setBounds(getWidth() / 2 - getWidth() / 6, (int) (getHeight() / 2 + getHeight() / 8 * 3),
				getWidth() / 3, getHeight() / 16);

		this.add(campaign);
		this.add(multiplayer);
		this.add(options);
		this.add(exit);
	}

	public void campaignAction() {
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new GameDeathmatch(getFrame(), new Map("res/img/Map.png", "res/img/CollisionMap.png", 0)));
	}

	public void multiplayerAction() {
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new MultiplayerMenu(getFrame()));
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

	private class ButtonPulse extends Thread {

		private JButton button;

		public ButtonPulse(JButton button) {
			this.button = button;
		}

		@Override
		public void run() {
			long oldTime;
			long newTime;
			if (button.getText().equals("Campaign")) {
				double i = 0;
				newTime = System.currentTimeMillis();
				oldTime = newTime;
				while (campaignPulse) {
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
			} else if (button.getText().equals("Multiplayer")) {
				double i = 0;
				newTime = System.currentTimeMillis();
				oldTime = newTime;
				while (multiplayerPulse) {
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
			} else if (button.getText().equals("Options")) {
				double i = 0;
				newTime = System.currentTimeMillis();
				oldTime = newTime;
				while (optionsPulse) {
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
			} else if (button.getText().equals("Exit Game")) {
				double i = 0;
				newTime = System.currentTimeMillis();
				oldTime = newTime;
				while (exitPulse) {
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
