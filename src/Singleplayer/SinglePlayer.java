package Singleplayer;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class SinglePlayer extends Canvas implements Runnable {
	/**
	 * Ryan Ballou 2013
	 */
	private static final long serialVersionUID = 1L;

	Player player;
	AI ai;
	Ball b;
	InputHandler IH;

	BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

	public static boolean running = false;
	public static final String TITLE = "Pong Singleplayer";
	public static final int WIDTH = 400;
	public static final int HEIGHT = WIDTH * 9 / 16; // Equation for widescreen
	public static final Dimension gameDim = new Dimension(WIDTH, HEIGHT);
	JFrame frame;

	// Scores
	public int p1Score, aiScore;

	double delta;
	boolean shouldRender;

	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 60D;

		long lastTimer = System.currentTimeMillis();
		delta = 0D;

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;

			// If you want to limit frame rate, shouldRender = false
			shouldRender = false;

			// If the time between ticks = 1, then various things (shouldRender = true, keeps FPS locked at UPS)
			while (delta >= 1) {
				tick();
				delta -= 1;
				shouldRender = true;
			}

			// If you should render, render!
			if (shouldRender) {
				render();
			}

			// Reset stuff every second for the new "FPS" and "UPS"
			if (System.currentTimeMillis() - lastTimer >= 1) {
				lastTimer += 1000;
			}
		}
	}

	public synchronized void start() {
		running = true;
		new Thread(this).start();
	} // End start method

	public static synchronized void stop() {
		running = false;
		System.exit(0);
	} // End stop method

	public SinglePlayer() {
		// Create the frame method
		createFrame();

		// Instantiate objects
		player = new Player(this);
		ai = new AI(this);
		b = new Ball(this);
		IH = new InputHandler(this);

		// Request focus on the game (Start right away)
		requestFocus();
	} // End Game constructor

	private void createFrame() {
		setMinimumSize(gameDim);
		setMaximumSize(gameDim);
		setPreferredSize(gameDim);
		frame = new JFrame(TITLE);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.add(this, BorderLayout.CENTER);
		frame.pack();

		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void tick() {
		player.tick(this, delta);
		ai.tick(this, delta);
		b.tick(this, delta);

		Toolkit.getDefaultToolkit().sync();
	}// End tick method

	public void render() {
		BufferStrategy bs = getBufferStrategy();

		if (bs == null) { // If the bufferstrategy doesn't exist, create one
			createBufferStrategy(4);
			return;
		}

		Graphics g = bs.getDrawGraphics(); // Grab graphics from buffer strategy for drawing

		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

		// Render the players and ball
		player.render(g);
		ai.render(g);
		b.render(g);

		// Draw scores
		g.setColor(Color.WHITE);
		g.drawString("P1: " + p1Score, 25, 10);
		g.drawString("P2: " + aiScore, getWidth() - 50, 10);

		// Clear and show
		g.dispose();
		bs.show();
		Toolkit.getDefaultToolkit().sync();
	} // End render method
}