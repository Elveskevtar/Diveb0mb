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

public class PrivateGameMenu extends JLayeredPane {

	private static final long serialVersionUID = -5223934336294993908L;

	private static final Color TEXTFIELD_GREEN = new Color(7, 192, 44);
	private static final Color BUTTON_HOVERED_RED = new Color(191, 10, 28);
	private static final Color ALPHA_BLACK = new Color(0, 0, 0, 150);

	private boolean backPulse;
	private boolean startPulse;

	private String username;

	private JFrame frame;
	private JButton back;
	private JButton start;

	private HintTextField port;

	private Font backFont;
	private Font startFont;
	private Font portFont;

	public PrivateGameMenu(JFrame frame, String username) {
		this.username = username;
		this.setFrame(frame);
		this.setSize(frame.getWidth() - frame.getInsets().left - frame.getInsets().right,
				frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom);
		this.setLayout(null);
		this.setFocusable(true);

		this.back = new JButton("Back");
		this.start = new JButton("Start Private Game");

		this.portFont = new Font("Livewired", Font.PLAIN, getHeight() / 16);
		this.backFont = new Font("Livewired", Font.PLAIN, (int) (getWidth() / 38.4));
		this.startFont = new Font("LiveWired", Font.PLAIN, (int) (getWidth() / 38.4));

		this.port = new HintTextField("Port");

		this.port.setOpaque(false);
		this.port.setBorder(BorderFactory.createEmptyBorder());
		this.port.setForeground(TEXTFIELD_GREEN);
		this.port.setHorizontalAlignment(JTextField.CENTER);
		this.port.setFont(portFont);

		this.back.setForeground(ALPHA_BLACK);
		this.start.setForeground(ALPHA_BLACK);

		this.back.setMargin(new Insets(-100, -100, -100, -100));
		this.start.setMargin(new Insets(-100, -100, -100, -100));

		this.back.setFont(backFont);
		this.start.setFont(startFont);

		this.back.setBorderPainted(false);
		this.start.setBorderPainted(false);

		this.back.setContentAreaFilled(false);
		this.start.setContentAreaFilled(false);

		this.back.setFocusPainted(false);
		this.start.setFocusPainted(false);

		this.back.setOpaque(false);
		this.start.setOpaque(false);

		this.back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backPulse = false;
				backAction();
			}
		});
		this.start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startPulse = false;
				startAction();
			}
		});

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
		this.start.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				start.setForeground(BUTTON_HOVERED_RED);
				startPulse = true;
				new Thread(new ButtonPulse(start)).start();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				start.setForeground(ALPHA_BLACK);
				startPulse = false;
			}
		});

		this.back.setBounds(0, 0, getWidth() / 8, getHeight() / 12);
		this.port.setBounds(getWidth() * 3 / 8, getHeight() / 2 - getHeight() / 12, getWidth() / 4, getHeight() / 6);
		this.start.setBounds(getWidth() / 4, getHeight() - getHeight() / 12, getWidth() / 2, getHeight() / 12);

		this.add(back, -1);
		this.add(start, -1);
		this.add(port, -1);
		this.add(new Background(), -1);

		this.start.setEnabled(false);

		new Thread(new CheckForButtons()).start();
	}

	private void backAction() {
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new MultiplayerMenu(getFrame()));
	}

	private void startAction() {
		setVisible(false);
		getFrame().remove(this);
		getFrame().add(new GameLobbyMenu(getFrame(), username, Integer.parseInt(port.getText())));
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public HintTextField getPort() {
		return port;
	}

	public void setPort(HintTextField port) {
		this.port = port;
	}

	public JButton getBack() {
		return back;
	}

	public void setBack(JButton back) {
		this.back = back;
	}

	public JButton getStart() {
		return start;
	}

	public void setStart(JButton start) {
		this.start = start;
	}

	public boolean isBackPulse() {
		return backPulse;
	}

	public void setBackPulse(boolean backPulse) {
		this.backPulse = backPulse;
	}

	public boolean isStartPulse() {
		return startPulse;
	}

	public void setStartPulse(boolean startPulse) {
		this.startPulse = startPulse;
	}

	public Font getBackFont() {
		return backFont;
	}

	public void setBackFont(Font backFont) {
		this.backFont = backFont;
	}

	public Font getStartFont() {
		return startFont;
	}

	public void setStartFont(Font startFont) {
		this.startFont = startFont;
	}

	public Font getPortFont() {
		return portFont;
	}

	public void setPortFont(Font portFont) {
		this.portFont = portFont;
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
				boolean integer = true;
				try {
					Integer.parseInt(port.getText());
				} catch (NumberFormatException e) {
					integer = false;
				}
				if (!port.getText().isEmpty() && !port.getText().equals(port.getHint()) && integer) {
					start.setEnabled(true);
				} else {
					start.setEnabled(false);
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

		private static final long serialVersionUID = -6560323492056593079L;

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

		private static final long serialVersionUID = 4752016032544682814L;

		private BufferedImage image;

		public Background() {
			this.setSize(PrivateGameMenu.this.getSize());
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
			} else if (button.getText().equals("Start Private Game")) {
				double i = 0;
				newTime = System.currentTimeMillis();
				oldTime = newTime;
				while (startPulse) {
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
			}
		}
	}
}