package com.elveskevtar.divebomb.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import com.elveskevtar.divebomb.maps.Map;

public class StartMenu extends JLayeredPane {

	private static final long serialVersionUID = -229487886910550057L;

	private static final Color BUTTON_GREEN = new Color(7, 192, 44);
	private static final Color BUTTON_PRESSED_RED = new Color(191, 10, 28);

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

	/*
	 * Called by Frame class or other menus; everything is called in blocks of
	 * 4, one for each JButton
	 */
	public StartMenu(JFrame frame) {
		this.setFrame(frame);
		this.setSize(frame.getWidth() - frame.getInsets().left - frame.getInsets().right,
				frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom);

		this.campaign = new JButton("Campaign");
		this.multiplayer = new JButton("Multiplayer");
		this.options = new JButton("Options");
		this.exit = new JButton("Exit Game");

		this.campaignFont = new Font("Livewired Academy Italic", Font.PLAIN, (int) (getWidth() / 15.0));
		this.multiplayerFont = new Font("Livewired Academy Italic", Font.PLAIN, (int) (getWidth() / 15.0));
		this.optionsFont = new Font("Livewired Academy Italic", Font.PLAIN, (int) (getWidth() / 15.0));
		this.exitFont = new Font("Livewired Academy Italic", Font.PLAIN, (int) (getWidth() / 15.0));

		this.campaign.setForeground(BUTTON_GREEN);
		this.multiplayer.setForeground(BUTTON_GREEN);
		this.options.setForeground(BUTTON_GREEN);
		this.exit.setForeground(BUTTON_GREEN);

		this.campaign.setMargin(new Insets(-400, -400, -400, -400));
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

		/*
		 * add action listeners to buttons (listens to clicks); ensures that the
		 * actions is only performed when the player clicks over the event
		 * hitbox and not just the button
		 */
		this.campaign.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (campaignPulse) {
					campaignPulse = false;
					campaignAction();
				}
			}
		});
		this.multiplayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (multiplayerPulse) {
					multiplayerPulse = false;
					multiplayerAction();
				}
			}
		});
		this.options.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (optionsPulse) {
					optionsPulse = false;
					optionsAction();
				}
			}
		});
		this.exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (exitPulse) {
					exitPulse = false;
					exitAction();
				}
			}
		});

		/*
		 * Add mouse motion and mouse listeners to buttons (listens for mouse
		 * collision with button hit box to start pulsing); mouse motion and
		 * mouse listeners are used in conjunction because the event hitbox that
		 * I want is much smaller than the actual button hitbox
		 */
		this.campaign.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (!campaignPulse && e.getX() >= getWidth() / 4 && e.getX() <= getWidth() * 3 / 4) {
					campaign.setForeground(BUTTON_PRESSED_RED);
					campaignPulse = true;
					new Thread(new ButtonPulse(campaign)).start();
				} else if (campaignPulse && (e.getX() < getWidth() / 4 || e.getX() > getWidth() * 3 / 4)) {
					campaign.setForeground(BUTTON_GREEN);
					campaignPulse = false;
				}
			}
		});
		this.campaign.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				campaign.setForeground(BUTTON_GREEN);
				campaignPulse = false;
			}
		});
		this.multiplayer.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (!multiplayerPulse && e.getX() >= getWidth() / 4 && e.getX() <= getWidth() * 3 / 4) {
					multiplayer.setForeground(BUTTON_PRESSED_RED);
					multiplayerPulse = true;
					new Thread(new ButtonPulse(multiplayer)).start();
				} else if (multiplayerPulse && (e.getX() < getWidth() / 4 || e.getX() > getWidth() * 3 / 4)) {
					multiplayer.setForeground(BUTTON_GREEN);
					multiplayerPulse = false;
				}
			}
		});
		this.multiplayer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				multiplayer.setForeground(BUTTON_GREEN);
				multiplayerPulse = false;
			}
		});
		this.options.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (!optionsPulse && e.getX() >= getWidth() / 4 && e.getX() <= getWidth() * 3 / 4) {
					options.setForeground(BUTTON_PRESSED_RED);
					optionsPulse = true;
					new Thread(new ButtonPulse(options)).start();
				} else if (optionsPulse && (e.getX() < getWidth() / 4 || e.getX() > getWidth() * 3 / 4)) {
					options.setForeground(BUTTON_GREEN);
					optionsPulse = false;
				}
			}
		});
		this.options.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				options.setForeground(BUTTON_GREEN);
				optionsPulse = false;
			}
		});
		this.exit.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (!exitPulse && e.getX() >= getWidth() / 4 && e.getX() <= getWidth() * 3 / 4) {
					exit.setForeground(BUTTON_PRESSED_RED);
					exitPulse = true;
					new Thread(new ButtonPulse(exit)).start();
				} else if (exitPulse && (e.getX() < getWidth() / 4 || e.getX() > getWidth() * 3 / 4)) {
					exit.setForeground(BUTTON_GREEN);
					exitPulse = false;
				}
			}
		});
		this.exit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				exit.setForeground(BUTTON_GREEN);
				exitPulse = false;
			}
		});

		/*
		 * Positioning for these buttons should center based on JFrame and
		 * JPanel bounds
		 */
		this.campaign.setBounds(0, getHeight() / 2, getWidth(), getHeight() / 8);
		this.multiplayer.setBounds(0, getHeight() / 2 + getHeight() / 8, getWidth(), getHeight() / 8);
		this.options.setBounds(0, getHeight() / 2 + getHeight() * 2 / 8, getWidth(), getHeight() / 8);
		this.exit.setBounds(0, getHeight() / 2 + getHeight() * 3 / 8, getWidth(), getHeight() / 8);

		/*
		 * Adds button components and background JPanel in the layers as part of
		 * StartMenu as a JLayeredPane; ensures that the background is not
		 * painted over the buttons
		 */
		this.add(campaign, -1);
		this.add(multiplayer, -1);
		this.add(options, -1);
		this.add(exit, -1);
		this.add(new Background(), -1);
	}

	/*
	 * Action methods are called from action listeners on buttons
	 */
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

	/* Standard get/set methods */
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

	private class Background extends JPanel {

		private static final long serialVersionUID = -678879425070878600L;

		private BufferedImage image;

		public Background() {
			this.setSize(StartMenu.this.getSize());
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
		 * is set again; uses system time to avoid laggy pulsing; each
		 * buttonPulse boolean variable is dealt with separately; this can be
		 * optimized in the future
		 */
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
					Font newFont = new Font("Livewired Academy Italic", Font.PLAIN,
							(int) ((getWidth() / 15.0 * Math.abs(Math.sin(i))) + getWidth() / 15.0));
					button.setFont(newFont);
					oldTime = newTime;
				}
				Font oldFont = new Font("Livewired Academy Italic", Font.PLAIN, (int) (getWidth() / 15.0));
				button.setFont(oldFont);
			} else if (button.getText().equals("Multiplayer")) {
				double i = 0;
				newTime = System.currentTimeMillis();
				oldTime = newTime;
				while (multiplayerPulse) {
					newTime = System.currentTimeMillis();
					long difference = newTime - oldTime;
					i += difference / 250.0;
					Font newFont = new Font("Livewired Academy Italic", Font.PLAIN,
							(int) ((getWidth() / 15.0 * Math.abs(Math.sin(i))) + getWidth() / 15.0));
					button.setFont(newFont);
					oldTime = newTime;
				}
				Font oldFont = new Font("Livewired Academy Italic", Font.PLAIN, (int) (getWidth() / 15.0));
				button.setFont(oldFont);
			} else if (button.getText().equals("Options")) {
				double i = 0;
				newTime = System.currentTimeMillis();
				oldTime = newTime;
				while (optionsPulse) {
					newTime = System.currentTimeMillis();
					long difference = newTime - oldTime;
					i += difference / 250.0;
					Font newFont = new Font("Livewired Academy Italic", Font.PLAIN,
							(int) ((getWidth() / 15.0 * Math.abs(Math.sin(i))) + getWidth() / 15.0));
					button.setFont(newFont);
					oldTime = newTime;
				}
				Font oldFont = new Font("Livewired Academy Italic", Font.PLAIN, (int) (getWidth() / 15.0));
				button.setFont(oldFont);
			} else if (button.getText().equals("Exit Game")) {
				double i = 0;
				newTime = System.currentTimeMillis();
				oldTime = newTime;
				while (exitPulse) {
					newTime = System.currentTimeMillis();
					long difference = newTime - oldTime;
					i += difference / 250.0;
					Font newFont = new Font("Livewired Academy Italic", Font.PLAIN,
							(int) ((getWidth() / 15.0 * Math.abs(Math.sin(i))) + getWidth() / 15.0));
					button.setFont(newFont);
					oldTime = newTime;
				}
				Font oldFont = new Font("Livewired Academy Italic", Font.PLAIN, (int) (getWidth() / 15.0));
				button.setFont(oldFont);
			}
		}
	}
}